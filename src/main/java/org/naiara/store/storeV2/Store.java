package org.naiara.store.storeV2;

import java.util.List;

public interface Store<K, V> {
    GenericStoreNode<K, V> fetchNodeFromStore(K key);

    V addNodeToStore(GenericStoreNode<K, V> node);

    List<GenericStoreNode<K, V>> fetchAllFromStore();

}
