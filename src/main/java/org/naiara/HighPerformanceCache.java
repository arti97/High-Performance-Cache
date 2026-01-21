package org.naiara;

import org.naiara.cache.CacheConfig;
import org.naiara.cache.CacheLoader;
import org.naiara.cache.CacheNode;
import org.naiara.cache.ExpiryStrategy;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.naiara.utils.Constants.DEAFULT_CACHE_SIZE;
import static org.naiara.utils.Constants.NULL_INPUT_EXCEPTION;

public class HighPerformanceCache {

    private final CacheConfig config;
    private final Map<String, CacheNode> cacheMap;
    private final Deque<String> orderedCacheList;
    private final CacheLoader cacheLoader;
    private static final Logger logger = Logger.getLogger(HighPerformanceCache.class.getName());

    public HighPerformanceCache(CacheConfig config, CacheLoader cacheLoader) {
        this.config = config != null ? config : new CacheConfig(DEAFULT_CACHE_SIZE);
        this.cacheMap = new ConcurrentHashMap<>();
        this.orderedCacheList = new ConcurrentLinkedDeque<>();
        this.cacheLoader = cacheLoader;
    }

    /*
    get(key): Retrieve the value associated with the given key from
    the cache. If the key is not present in the cache, retrieve it
    from a backing store, add it to the cache, and return it. If the
    value is not found in the backing store, return null.
     */
    public String get(String key) {
        checkForNullValue(key);
        logger.log(Level.INFO, "GET operation for key: " + key);
        if (cacheMiss(key)) return fetchFromStore(key);
        logger.log(Level.INFO, "Cache Hit for key: " + key);
        CacheNode cacheNode = cacheMap.get(key);
        updateBasedOnLatestAccess(cacheNode);
        return cacheNode.getValue();
    }

    /*
    put(key, value): Add a new key-value pair to the cache. If the
    cache is at capacity, evict the least recently used item before
    adding the new item.
     */
    public String put(String key, String value) {
        checkForNullValue(key);
        checkForNullValue(value);
        logger.log(Level.INFO, "PUT operation for key: " + key + " value: " + value);
        if(isCacheFull()) evictLru();
        if(cacheHit(key)){
            logger.log(Level.INFO, "Cache Hit for key: " + key);
            CacheNode existingNode = cacheMap.get(key);
            String previousValue = existingNode.getValue();
            existingNode.setValue(value);
            logger.log(Level.INFO, "Updating previous value: " + previousValue+ " to: " + value);
            updateBasedOnLatestAccess(existingNode);
            return previousValue;
        }
        logger.log(Level.INFO, "Cache miss; Adding new node to cache & list");
        cacheMap.put(key, new CacheNode(key, value));
        orderedCacheList.addFirst(key);
        logger.log(Level.INFO, "LIST FIRST POINTER AT: " + orderedCacheList.getFirst());
        return null;
    }

    /*
    remove(key): Remove the key-value pair associated with the given
    key from the cache.
     */
    public String remove(String key) {
        checkForNullValue(key);
        logger.log(Level.INFO, "REMOVE operation for key: " + key);
        if (cacheMiss(key)) return null;
        orderedCacheList.remove(key);
        return cacheMap.remove(key).getValue();
    }

    //TODO: Provide empty/clear cache functionality

    public int size(){
        int size = cacheMap.size();
        logger.log(Level.INFO, "SIZE check: "+ size);
        logger.log(Level.INFO, orderedCacheList.toString());
        return size;
    }



    /* ------------------ ------------- ------------------ */
    /* ------------------ PRIVATE UTILS ------------------ */
    /* ------------------ ------------- ------------------ */
    private void checkForNullValue(String elememt){
        if (elememt == null) {
            logger.log(Level.SEVERE, "Null value passed");
            throw new IllegalArgumentException(NULL_INPUT_EXCEPTION);
        }
    }

    private String fetchFromStore(String key){
        if (cacheLoader != null) {
            logger.log(Level.INFO, "Fetching from store for key: " + key);
            String value = cacheLoader.load(key);
            if (value != null) {
                logger.log(Level.INFO, "Found in store for key: " + key + " value:" + value);
                put(key, value);
                return value;
            }
        }
        logger.log(Level.INFO, "Store is null!");
        return null;
    }

    private boolean isCacheFull(){
        return cacheMap.size() >= config.getCapacity();
    }

    private void updateOrderedCacheList(String key){
        orderedCacheList.remove(key);
        orderedCacheList.addFirst(key);
        logger.log(Level.INFO, "LIST FIRST POINTER AT: " + orderedCacheList.getFirst());
    }

    private void evictLru(){
        logger.log(Level.INFO, "-----------CACHE FULL!, evicting LRU-----------");
        String leastUsedKey = orderedCacheList.removeLast();
        cacheMap.remove(leastUsedKey);
    }

    private boolean cacheMiss(String key){
        return !cacheHit(key);
    }

    private boolean cacheHit(String key){
        return cacheMap.containsKey(key);
    }

    private void updateBasedOnLatestAccess(CacheNode cacheNode){
        cacheNode.setLastAccessedTime(System.currentTimeMillis());
        updateOrderedCacheList(cacheNode.getKey());
    }

    public void runExpirationStrategy(){
        System.out.println("--------- TRIGGERING EXPIRY -----------");
        ExpiryStrategy expiryStrategy = config.getExpiryStrategy();
        long ttl = config.getTtl();
        System.out.println("--------- STRATEGY: " + expiryStrategy + " TTL: " + ttl + "-----------");
        for(CacheNode eachNode: cacheMap.values()){
            if(eachNode.isExpired(expiryStrategy, ttl)){
                System.out.println("Removing: "+ eachNode.getKey());
                cacheMap.remove(eachNode.getKey());
                orderedCacheList.remove(eachNode.getKey());
            }
        }
    }
}
