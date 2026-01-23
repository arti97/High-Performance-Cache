package org.naiara.store.storeV2;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackStore<K, V> implements Store<K, V>{
    private final Map<K, GenericStoreNode<K, V>> storeMap;
    private static final Logger logger = Logger.getLogger(BackStore.class.getName());

    public BackStore() {
        this.storeMap = new ConcurrentHashMap<>();
    }

    public BackStore(Map<K, GenericStoreNode<K, V>> storeMap) {
        this.storeMap = storeMap;
    }

    @Override
    public GenericStoreNode<K, V> fetchNodeFromStore(K key) {
        try{
            GenericStoreNode<K, V> storeNode = storeMap.get(key);
            storeNode.setLastAccessedTime(System.currentTimeMillis());
            return storeNode;
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
            return null;
        }
    }

    @Override
    public V addNodeToStore(GenericStoreNode<K, V> node) {
        try{
            storeMap.put(node.getKey(), node);
            return node.getValue();
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
            throw e;
        }
    }

    @Override
    public List<GenericStoreNode<K, V>> fetchAllFromStore() {
        try{
            return storeMap.values().stream().toList();
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
            throw e;
        }
    }

}
