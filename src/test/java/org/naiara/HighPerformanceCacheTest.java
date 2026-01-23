package org.naiara;

import org.junit.jupiter.api.Test;
import org.naiara.cache.CacheConfig;
import org.naiara.store.DummyStore;
import org.naiara.store.Store;
import org.naiara.store.StoreNode;
import java.util.Map;

import static org.naiara.utils.Constants.*;

public class HighPerformanceCacheTest {

    //TODO: add both happy path tests and others

    public final CacheConfig cacheConfigTest = new CacheConfig(DEAFULT_CACHE_SIZE);

//    TODO: Tests
//
//        Order test:
//        { add some keys,
//        access first-inserted to make it at first-pointer
//        add more keys to evict 2nd added/3rd addded}
//
//        Edge cases:
//         - null key
//         - null value
//         - ops on empty cache
//         - clear cache
//         */

    @Test
    public void testAddAndRetrieve(){
        /*
        Test Case 1: Add and Retrieve
         Input:-> put('key1', 'value1')
                -> get('key1')
          Expected Output: 'value1'
         */
        HighPerformanceCache hpcTest1 = new HighPerformanceCache(cacheConfigTest, null);
        hpcTest1.put(KEY1, VALUE1);
        String test1Result = hpcTest1.get(KEY1);
        assert test1Result.equals(VALUE1) : "Expected 'value1', got " + test1Result;
    }

    @Test
    public void testRetrieveNonExistentKey(){
        /*
        Test Case 2: Retrieve Non-existent Key
            Input:-> get('keyX') (where 'keyX' is not in cache)
            Expected Output: Retrieve from backing store or null if not found
         */
        HighPerformanceCache hpcTest2 = new HighPerformanceCache(cacheConfigTest, null);
        String test2Result = hpcTest2.get(KEY2);
        assert test2Result == null : "Expected null, got " + test2Result;
    }

    @Test
    public void testUpdateExistingKey(){
        /*
        Test Case 3: Update Existing Key
            Input:-> put('key1', 'value1')
                     put('key1', 'value2')
                    -> get('key1')
             Expected Output: 'value2'
         */
        HighPerformanceCache hpcTest3 = new HighPerformanceCache(cacheConfigTest, null);
        hpcTest3.put(KEY1, VALUE1);
        String previousValue = hpcTest3.put(KEY1, "value2");
        String test3Result = hpcTest3.get(KEY1);
        assert previousValue.equals(VALUE1) : "Expected 'value1', got " + previousValue;
        assert test3Result.equals("value2") : "Expected 'value2', got " + test3Result;
        assert hpcTest3.size() == 1 : "Expected size 1, got " + hpcTest3.size();
    }

    @Test
    public void testRemoveKey(){
        /*
        Test Case 4: Remove Key
            Input:-> put('key1', 'value1'),
                    -> remove('key1')
                    -> get('key1')
            Expected Output: Retrieve from backing store or null if not found
         */
        HighPerformanceCache hpcTest4 = new HighPerformanceCache(cacheConfigTest, null);
        hpcTest4.put(KEY1, VALUE1);
        String removed = hpcTest4.remove(KEY1);
        String test4Result = hpcTest4.get(KEY1);
        assert removed.equals(VALUE1) : "Remove should return previous value (value1) got: " + removed;
        assert test4Result == null : "Expected null after removal";
        assert hpcTest4.size() == 0 : "Expected size 0, got " + hpcTest4.size();
    }

    @Test
    public void testCapacityAndEvictionPolicy(){
            /*
            Test Case 5: Evict on Capacity
                Setup: Cache capacity set to 2
                Input:-> put('key1', 'value1')
                       -> put('key2'  , 'value2')
                        -> put('key3' , 'value3')
                        -> get('key1')
                Expected Output: null (or retrieved from backing store if applicable), 'key1' should be evicted
             */
        HighPerformanceCache hpcTest5 = new HighPerformanceCache(new CacheConfig(2), null);
        hpcTest5.put(KEY1, VALUE1);
        hpcTest5.put(KEY2, VALUE2);
        hpcTest5.put(KEY3, VALUE3);
        assert hpcTest5.get(KEY1) == null : "key1 should be evicted";
        assert hpcTest5.get(KEY2) != null : "key2 should exist";
        assert hpcTest5.get(KEY3) != null : "key3 should exist";
        assert hpcTest5.size() == 2 : "Expected size 2, got " + hpcTest5.size();
    }

    // Extra tests, not in provided PDF
    @Test
    public void testLoadFromStoreOnCacheMiss() {
        Store cacheLoaderTest = new DummyStore(Map.of(BACKEND_KEY, new StoreNode(BACKEND_KEY, BACKEND_VALUE)));
        HighPerformanceCache hpcTest6 = new HighPerformanceCache(cacheConfigTest, cacheLoaderTest);
        String hpcTest6Result = hpcTest6.get(BACKEND_KEY);
        assert hpcTest6Result != null && hpcTest6Result.equals(BACKEND_VALUE) : "Should load from backing store";
        hpcTest6.put(BACKEND_KEY, VALUE3);
        hpcTest6Result = hpcTest6.get(BACKEND_KEY);
        assert hpcTest6Result != null && hpcTest6Result.equals(VALUE3) : "Should retrieve updated value from cache: " + VALUE3 + ", instead got previous backend value "+ hpcTest6Result;
    }
}
