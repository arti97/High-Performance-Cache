package org.naiara.store.storeV2;

public class GenericStoreNode<K, V> {
    private K key;
    private V value;
    private long createdTime;
    private long lastAccessedTime;
    private long lastModifiedTime;
    private int storeVersion;
    private int cacheVersion;

    public GenericStoreNode(K key, V value) {
        this.key = key;
        this.value = value;
        this.createdTime = System.currentTimeMillis();
        this.lastAccessedTime = createdTime;
        this.lastModifiedTime = createdTime;
        this.storeVersion = 0;
        this.cacheVersion = -1;
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

    public int getStoreVersion() {
        return storeVersion;
    }

    public void setStoreVersion(int storeVersion) {
        this.storeVersion = storeVersion;
    }

    public int getCacheVersion() {
        return cacheVersion;
    }

    public void setCacheVersion(int cacheVersion) {
        this.cacheVersion = cacheVersion;
    }
}
