package org.naiara.cache.cachev2.expirystrategy;

import org.naiara.cache.cachev2.GenericCacheNode;

public class LastAccessExpiry<K, V> implements ExpiryStrategy<K, V>{
    @Override
    public boolean isExpired(GenericCacheNode<K, V> node, long ttl) {
        return (System.currentTimeMillis() - node.getLastAccessedTime()) >= ttl;
    }
}

