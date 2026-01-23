package org.naiara;

import org.naiara.cache.CacheConfig;
import org.naiara.cache.cachev2.expirystrategy.ExpiryStrategy;
import org.naiara.cache.cachev2.GenericCacheNode;
import org.naiara.cache.cachev2.expirystrategy.LastAccessExpiry;
import org.naiara.store.storeV2.GenericStoreNode;
import org.naiara.store.storeV2.Store;

import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.naiara.utils.Constants.*;

public class GenericHighPerformanceCache<K, V> implements AutoCloseable {
    private final CacheConfig config;
    private final Map<K, GenericCacheNode<K, V>> cacheMap;
    private final Deque<K> orderedCacheList;
    private final Store<K, V> storeLoader;
    private static final Logger logger = Logger.getLogger(GenericHighPerformanceCache.class.getName());
    private final ScheduledExecutorService scheduler;

    public GenericHighPerformanceCache(CacheConfig config, Store<K, V> storeLoader) {
        this.config = config != null ? config : new CacheConfig(DEAFULT_CACHE_SIZE);
        this.cacheMap = new ConcurrentHashMap<>();
        this.orderedCacheList = new ConcurrentLinkedDeque<>();
        this.storeLoader = storeLoader;
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.scheduler.schedule(this::loadCacheFromStore, 0, TimeUnit.SECONDS);
        this.scheduler.scheduleWithFixedDelay(this::removeExpiredItems, 30, 30, TimeUnit.SECONDS);
    }

    public V get(K key) {
        try{
            validateNullInput(key);
        } catch(Exception e) {
            logger.log(Level.WARNING, e.getMessage());
            return null;
        }
        logger.log(Level.INFO, "GET operation for key: " + key);
        V returnValue = null;
        /* Note: READ THROUGH PATTERN
        In Cache-Aside, the responsibility of querying main store incase of a cache miss sits with the calling application
         */
        if (cacheMiss(key)) {
            logger.log(Level.INFO, "Cache miss!");
            try{
                GenericStoreNode<K, V> storeNode = storeLoader.fetchNodeFromStore(key);
                returnValue = storeNode.getValue();
                loadStoreNodeToCache(storeNode);
            } catch (Exception e){
                logger.log(Level.WARNING, e.getMessage());
                return null;
            }
        } else {
            logger.log(Level.INFO, "Cache Hit!");
            GenericCacheNode<K, V> cacheNode = cacheMap.get(key);
            updateBasedOnLatestAccess(cacheNode);
            returnValue = cacheNode.getValue();
        }
        return returnValue;
    }

    public V put(K key, V value){
        try{
            validateNullInput(key);
            // Value can be null?
        } catch(Exception e) {
            logger.log(Level.WARNING, e.getMessage());
            return null;
        }
        logger.log(Level.INFO, "PUT operation for key: " + key + " value: " + value);
        V prevValue = null;
        // TODO: Implement WRITE-AROUND
        GenericCacheNode<K, V> putNode;
        if(cacheHit(key)){
            logger.log(Level.INFO, "Cache Hit!");
            putNode = cacheMap.get(key);
            prevValue = putNode.getValue();
            putNode.setValue(value);
            putNode.setLastModifiedTime(System.currentTimeMillis());
            putNode.setCacheVersion(putNode.getCacheVersion()+1);
            logger.log(Level.INFO, "Updating previous value: " + prevValue + " to: " + value);
        } else {
            logger.log(Level.INFO, "Cache miss!");
            if(isCacheFull()) evictLru();
            logger.log(Level.INFO, "Adding new node to cache");
            putNode = new GenericCacheNode<>(key, value);
            cacheMap.put(key, putNode);
            orderedCacheList.addFirst(key);
        }
        updateBasedOnLatestAccess(putNode);
        // TODO: Implement WRITE-THROUGH
        // TODO: Implement async WRITE-BACK
        return prevValue;
    }

    public V remove(K key) {
        try{
            validateNullInput(key);
        } catch(Exception e) {
            logger.log(Level.WARNING, e.getMessage());
            return null;
        }
        logger.log(Level.INFO, "REMOVE operation for key: " + key);
        if (cacheMiss(key)){
            logger.log(Level.INFO, "Cache miss!");
            return null;
        }
        logger.log(Level.INFO, "Cache Hit!");
        orderedCacheList.remove(key);
        return cacheMap.remove(key).getValue();
    }

    public int size(){
        int size = orderedCacheList.size();
        logger.log(Level.INFO, "SIZE check: "+ size);
        logger.log(Level.INFO, orderedCacheList.toString());
        return size;
    }

    @Override
    public void close() throws Exception {
        logger.log(Level.INFO, "Closing object, stopping thread.");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    /* ------------------------- PRIVATE UTILS ------------------------- */

    private boolean cacheMiss(K key){
        return !cacheHit(key);
    }

    private boolean cacheHit(K key){
        return cacheMap.containsKey(key);
    }

    private void validateNullInput(K input){
        if (input == null) throw new IllegalArgumentException(NULL_INPUT_EXCEPTION);
    }

    private void updateBasedOnLatestAccess(GenericCacheNode<K, V> cacheNode){
        cacheNode.setLastAccessedTime(System.currentTimeMillis());
        updateOrderedCacheList(cacheNode.getKey());
    }

    private void updateOrderedCacheList(K key){
        orderedCacheList.remove(key);
        orderedCacheList.addFirst(key);
        logger.log(Level.INFO, "Most recently used updated to: " + orderedCacheList.getFirst());
    }

    private boolean isCacheFull(){
        return cacheMap.size() >= config.getCapacity();
    }

    private void evictLru(){
        logger.log(Level.INFO, "-----------CACHE FULL!, evicting LRU-----------");
        K leastUsedKey = orderedCacheList.removeLast();
        cacheMap.remove(leastUsedKey);
    }

    private void removeExpiredItems(){
        System.out.println("--------- TRIGGERING EXPIRY -----------");
        System.out.println("IN THREAD " + Thread.currentThread().getName());
        // TODO: Hardcoding for now, change in cache config
        ExpiryStrategy<K, V> expiryStrategy = new LastAccessExpiry<>();
        long ttl = config.getTtl();
        System.out.println("--------- STRATEGY: " + expiryStrategy + " TTL: " + ttl + "-----------");
        for(GenericCacheNode<K, V> node: cacheMap.values()){
            if(node.isExpired(expiryStrategy, ttl)){
                System.out.println("Removing: "+ node.getKey());
                remove(node.getKey());
            }
        }
    }

    // TODO: Ideally should move this away from Cache class. This is store loader's responsibility.
    private void loadCacheFromStore(){
        System.out.println("IN THREAD " + Thread.currentThread().getName());
        logger.log(Level.INFO, "--------- TRIGGERING INITIAL LOAD -----------");
        List<GenericStoreNode<K, V>> storeNodesList = storeLoader.fetchAllFromStore();
        //TODO: Some issue
//        storeNodesList.sort(Comparator.comparingLong(GenericStoreNode::getLastModifiedTime));
        storeNodesList = storeNodesList.subList(0, DEAFULT_CACHE_SIZE-1);
        for(GenericStoreNode<K, V> storeNode: storeNodesList){
            loadStoreNodeToCache(storeNode);
        }
        logger.log(Level.INFO, "Done Loading. Final size check: "+ orderedCacheList.size());
    }

    private void loadStoreNodeToCache(GenericStoreNode<K, V> storeNode){
        GenericCacheNode<K, V> cacheNode = new GenericCacheNode.Builder<K, V>()
                .key(storeNode.getKey())
                .value(storeNode.getValue())
                .storeVersion(storeNode.getStoreVersion())
                .build();
        if(isCacheFull()) evictLru();
        cacheMap.put(storeNode.getKey(), cacheNode);
        orderedCacheList.addFirst(storeNode.getKey());
    }
}
