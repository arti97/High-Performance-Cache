package org.naiara.store;

public class StoreNode {
    String key;
    String value;
    long createdTime;
    long lastUpdatedTime;
    int storeVersion;

    public StoreNode(String key, String value) {
        this.key = key;
        this.value = value;
        this.createdTime = System.currentTimeMillis();
        this.lastUpdatedTime = this.createdTime;
        this.storeVersion = 0;
    }

    public String getValue() {
        return value;
    }
}
