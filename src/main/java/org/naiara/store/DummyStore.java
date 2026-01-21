package org.naiara.store;

import java.util.Map;

public class DummyStore implements Store {
    Map<String, StoreNode> storeData;
    long lastRefreshTime;

    public DummyStore(Map<String, StoreNode> storeData) {
        this.storeData = storeData;
    }

    public String load(String key) {
        return storeData.get(key).getValue();
    }

    public Map<String, StoreNode> getStoreData() {
        return storeData;
    }
}
