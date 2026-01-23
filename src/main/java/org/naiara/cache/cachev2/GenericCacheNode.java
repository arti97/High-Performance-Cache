package org.naiara.cache.cachev2;

import org.naiara.cache.cachev2.expirystrategy.ExpiryStrategy;

import java.util.logging.Logger;

public class GenericCacheNode<K, V> {
    private K key;
    private V value;
    private long createdTime;
    private long lastAccessedTime;
    private long lastModifiedTime;
    private int cacheVersion;
    private int storeVersion;
    private static final Logger logger = Logger.getLogger(GenericCacheNode.class.getName());

    public GenericCacheNode(K key, V value) {
        this.key = key;
        this.value = value;
        this.createdTime = System.currentTimeMillis();
        this.lastAccessedTime = createdTime;
        this.lastModifiedTime = createdTime;
        this.cacheVersion = 0;
        this.storeVersion = -1;
    }

    public GenericCacheNode(Builder<K, V> storeBuilder) {
        this.key = storeBuilder.key;
        this.value = storeBuilder.value;;
        this.createdTime = System.currentTimeMillis();
        this.lastAccessedTime = createdTime;
        this.lastModifiedTime = createdTime;
        this.cacheVersion = storeBuilder.storeVersion;
        this.storeVersion = storeBuilder.storeVersion;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public int getCacheVersion() {
        return cacheVersion;
    }

    public void setCacheVersion(int cacheVersion) {
        this.cacheVersion = cacheVersion;
    }

    public int getStoreVersion() {
        return storeVersion;
    }

    public void setStoreVersion(int storeVersion) {
        this.storeVersion = storeVersion;
    }

    public boolean isExpired(ExpiryStrategy<K, V> expiryStrategy, long ttl){
        return expiryStrategy.isExpired(this, ttl);
    }

    public static class Builder<K, V> {
        private K key;
        private V value;
        private int storeVersion;

        public Builder<K, V> key(K key) {
            this.key = key;
            return this;
        }

        public Builder<K, V> value(V value) {
            this.value = value;
            return this;
        }

        public Builder<K, V> storeVersion(int storeVersion) {
            this.storeVersion = storeVersion;
            return this;
        }

        public GenericCacheNode<K, V> build() {
            return new GenericCacheNode<K, V>(this);
        }
    }
}
