package org.naiara;

import org.naiara.cacheconfig.CacheConfig;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.naiara.utils.Constants.DEAFULT_CACHE_SIZE;
import static org.naiara.utils.Constants.NULL_INPUT_EXCEPTION;

public class HighPerformanceCache {

    private final CacheConfig config;
    private final Map<String, String> cacheMap;
    private final Deque<String> orderedCacheList;

    public HighPerformanceCache(CacheConfig config) {
        this.config = config != null ? config : new CacheConfig(DEAFULT_CACHE_SIZE);
        this.cacheMap = new ConcurrentHashMap<>();
        this.orderedCacheList = new ConcurrentLinkedDeque<>();
    }

    /*
    get(key): Retrieve the value associated with the given key from
    the cache. If the key is not present in the cache, retrieve it
    from a backing store, add it to the cache, and return it. If the
    value is not found in the backing store, return null.
     */
    public String get(String key) {
        checkForNullValue(key);
        if (!cacheMap.containsKey(key)) {
            // If the key is not present in the cache, retrieve it from a backing store, add it to the cache, and return it??
            // If the value is not found in the backing store, return null??
            // TODO: Clarify Backing Store
            return null;
        }
        orderedCacheList.remove(key);
        orderedCacheList.addFirst(key);
        return cacheMap.get(key);
    }


    /*
    put(key, value): Add a new key-value pair to the cache. If the
    cache is at capacity, evict the least recently used item before
    adding the new item.
     */
    public void put(String key, String value) {
        checkForNullValue(key);
        checkForNullValue(value);
        if (cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
            orderedCacheList.remove(key);
        } else {
            if (cacheMap.size() >= config.getCapacity()) {
                String leastUsedKey = orderedCacheList.removeLast();
                cacheMap.remove(leastUsedKey);
            }
            cacheMap.put(key, value);
        }
        orderedCacheList.addFirst(key);
    }

    /*
    remove(key): Remove the key-value pair associated with the given
    key from the cache.
     */
    public boolean remove(String key) {
        checkForNullValue(key);
        if (!cacheMap.containsKey(key)) return false;
        orderedCacheList.remove(key);
        cacheMap.remove(key);
        return true;
    }

    private void checkForNullValue(String elememt){
        if (elememt == null) {
            throw new IllegalArgumentException(NULL_INPUT_EXCEPTION);
        }
    }

    public int size(){
        return cacheMap.size();
    }
}
