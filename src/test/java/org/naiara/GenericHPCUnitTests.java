package org.naiara;

import org.naiara.cache.CacheConfig;
import org.junit.jupiter.api.*;
import org.naiara.store.storeV2.*;

import static org.naiara.utils.Constants.*;

public class GenericHPCUnitTests {
    private final CacheConfig cacheConfigTest = new CacheConfig(DEAFULT_CACHE_SIZE);

    @Test
    void testAddAndRetrieve() throws Exception {
        try(GenericHighPerformanceCache<String, String> genericHpcTest1 = new GenericHighPerformanceCache<>(cacheConfigTest, null)){
            genericHpcTest1.put(KEY1, VALUE1);
            String test1Result = genericHpcTest1.get(KEY1);
            assert VALUE1.equals(test1Result) : "Expected 'value1', got " + test1Result;
        }
    }

    @Test
    void testRetrieveNonExistentKey() throws Exception {
        try(GenericHighPerformanceCache<String, String> genericHpcTest2= new GenericHighPerformanceCache<>(cacheConfigTest, null)){
            String test2Result = genericHpcTest2.get(KEY2);
            assert test2Result == null : "Expected null, got " + test2Result;
        }
    }

    @Test
    void testUpdateExistingKey() throws Exception {
        try(GenericHighPerformanceCache<String, String> genericHpcTest3 = new GenericHighPerformanceCache<>(cacheConfigTest, null)){
            genericHpcTest3.put(KEY1, VALUE1);
            String previousValue = genericHpcTest3.put(KEY1, "value2");
            String test3Result = genericHpcTest3.get(KEY1);
            assert previousValue.equals(VALUE1) : "Expected 'value1', got " + previousValue;
            assert test3Result.equals("value2") : "Expected 'value2', got " + test3Result;
        }
    }

    @Test
    void testRemoveKey() throws Exception {
        try(GenericHighPerformanceCache<String, String> genericHpcTest4 = new GenericHighPerformanceCache<>(cacheConfigTest, null)){
            genericHpcTest4.put(KEY1, VALUE1);
            String removed = genericHpcTest4.remove(KEY1);
            String test4Result = genericHpcTest4.get(KEY1);
            assert removed.equals(VALUE1) : "Remove should return previous value (value1) got: " + removed;
            assert test4Result == null : "Expected null after removal";
            assert genericHpcTest4.size() == 0 : "Expected size 0, got " + genericHpcTest4.size();
        }
    }

    @Test
    void testCapacityAndEvictionPolicy() throws Exception {
        try(GenericHighPerformanceCache<String, String> genericHpcTest5 = new GenericHighPerformanceCache<>(new CacheConfig(2), null)){
            genericHpcTest5.put(KEY1, VALUE1);
            genericHpcTest5.put(KEY2, VALUE2);
            genericHpcTest5.put(KEY3, VALUE3);
            assert genericHpcTest5.get(KEY1) == null : "key1 should be evicted";
            assert genericHpcTest5.get(KEY2) != null : "key2 should exist";
            assert genericHpcTest5.get(KEY3) != null : "key3 should exist";
            assert genericHpcTest5.size() == 2 : "Expected size 2, got " + genericHpcTest5.size();
        }
    }

    // Extra tests, not in provided PDF
    @Test
    void testLoadFromStoreOnCacheMiss() throws Exception {
        GenericStoreNode<String, String> newStoreNode = new GenericStoreNode<>(BACKEND_KEY, BACKEND_VALUE);
        BackStore<String, String> storeLoaderTest = new BackStore<>();
        storeLoaderTest.addNodeToStore(newStoreNode);
        try(GenericHighPerformanceCache<String, String> genericHpcTest6 = new GenericHighPerformanceCache<>(cacheConfigTest, storeLoaderTest)){
            String genericHpcTest6Result = genericHpcTest6.get(BACKEND_KEY);
            assert genericHpcTest6Result != null && genericHpcTest6Result.equals(BACKEND_VALUE) : "Should load from backing store";
            genericHpcTest6.put(BACKEND_KEY, VALUE3);
            genericHpcTest6Result = genericHpcTest6.get(BACKEND_KEY);
            assert genericHpcTest6Result != null && genericHpcTest6Result.equals(VALUE3) : "Should retrieve updated value from cache: " + VALUE3 + ", instead got previous backend value "+ genericHpcTest6Result;
        }
    }
}

