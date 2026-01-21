package org.naiara.cacheconfig;

/*
Additionally, the cache should support several configuration options,
such as:
    ● TODO: Expiration strategies: Specify the time-to-live (TTL) for items
    in the cache. Expired items should be removed from the cache
    automatically.
        ○ Expire entries after the specified duration has passed since
    the entry was last accessed (read/write).
    ● Maximum size: specify the maximum number of items allowed in the
    cache.
    ● Write Policy: specify the methodology used to propagate updates
    in cached data
 */

import static org.naiara.utils.Constants.INVALID_CAPACITY_EXCEPTION;

public class CacheConfig {
    private final int capacity;
    private final WritePolicy writePolicy;

    public CacheConfig(int capacity) {
        this.capacity = capacity;
        checkCapacity();
        this.writePolicy = WritePolicy.WRITE_THROUGH;
    }

    public CacheConfig(int capacity, WritePolicy writePolicy) {
        this.capacity = capacity;
        checkCapacity();
        this.writePolicy = writePolicy;
    }

    private void checkCapacity(){
        if(this.capacity <= 0) throw new IllegalArgumentException(INVALID_CAPACITY_EXCEPTION);
    }

    public int getCapacity() {
        return capacity;
    }
}
