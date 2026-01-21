package org.naiara.store;

import java.util.Map;

public interface Store {
    String load(String key);

    Map<String, StoreNode> getStoreData();
}
