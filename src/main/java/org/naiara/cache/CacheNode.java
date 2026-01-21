package org.naiara.cache;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CacheNode {
    String key;
    String value;
    long createdTime;
    long lastAccessedTime;
    private static final Logger logger = Logger.getLogger(CacheNode.class.getName());

    public CacheNode(String key, String value) {
        this.key = key;
        this.value = value;
        this.createdTime = System.currentTimeMillis();
        logger.log(Level.INFO, "Creating new cache node for key: " + key + " value: " + value);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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

    public boolean isExpired(ExpiryStrategy expiryStrategy, long ttl){
        return expiryStrategy.isExpired(this, ttl);
    }
}
