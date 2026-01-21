package org.naiara.cacheconfig;

public interface CacheLoader {

    /*
    If the key is not present in the cache, retrieve it
    from a backing store, add it to the cache, and return it. If the
    value is not found in the backing store, return null.
     */
    String load(String key);
}
