package org.naiara;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class HighPerformanceCache {

    private final int maxSize;
    private Map<String, String> cacheMap;
    private Deque<String> orderedCacheList;

    public HighPerformanceCache(int maxSize) {
        this.maxSize = maxSize;
        this.cacheMap = new HashMap<>();
        this.orderedCacheList = new ConcurrentLinkedDeque<>();
    }

    /*
    get(key): Retrieve the value associated with the given key from
    the cache. If the key is not present in the cache, retrieve it
    from a backing store, add it to the cache, and return it. If the
    value is not found in the backing store, return null.
     */
    public String get(String key) {
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
        if (cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
            orderedCacheList.remove(key);
        } else {
            if (cacheMap.size() >= maxSize) {
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
    public void remove(String key) {
        if (!cacheMap.containsKey(key)) return;
        orderedCacheList.remove(key);
        cacheMap.remove(key);
    }
}
