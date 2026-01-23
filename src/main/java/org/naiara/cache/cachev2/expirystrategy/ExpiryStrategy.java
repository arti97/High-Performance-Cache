package org.naiara.cache.cachev2.expirystrategy;

import org.naiara.cache.cachev2.GenericCacheNode;

public interface ExpiryStrategy<K, V> {
    boolean isExpired(GenericCacheNode<K, V> node, long ttl);
}
