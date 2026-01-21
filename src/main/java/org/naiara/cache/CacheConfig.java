package org.naiara.cache;

/*
Additionally, the cache should support several configuration options,
such as:
    ● Expiration strategies: Specify the time-to-live (TTL) for items
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
    private final ExpiryStrategy expiryStrategy;
    private final long ttl;

    public CacheConfig(int capacity) {
        this.capacity = capacity;
        checkCapacity();
        this.writePolicy = WritePolicy.WRITE_THROUGH;
        this.expiryStrategy = ExpiryStrategy.NO_EXPIRY;
        this.ttl = 0;
    }

    public CacheConfig(int capacity, WritePolicy writePolicy, ExpiryStrategy expiryStrategy, long ttl) {
        this.capacity = capacity;
        checkCapacity();
        this.writePolicy = writePolicy;
        this.expiryStrategy = expiryStrategy;
        this.ttl = ttl;
    }

    private void checkCapacity(){
        if(this.capacity <= 0) throw new IllegalArgumentException(INVALID_CAPACITY_EXCEPTION);
    }

    public int getCapacity() {
        return capacity;
    }

    public ExpiryStrategy getExpiryStrategy() {
        return expiryStrategy;
    }

    public long getTtl() {
        return ttl;
    }
}
