package org.naiara;

import org.naiara.cacheconfig.CacheConfig;

import static org.naiara.TestUtils.passTest;
import static org.naiara.utils.Constants.DEAFULT_CACHE_SIZE;

public class HighPerformanceCacheTest {

    //TODO: add both happy path tests and others

    private static final CacheConfig cacheConfigTest = new CacheConfig(DEAFULT_CACHE_SIZE);

    static void main() {

        testAddAndRetrieve();
        testRetrieveNonExistentKey();
        testUpdateExistingKey();
        testRemoveKey();
        testCapacityAndEvictionPolicy();
    }

    private static void testAddAndRetrieve(){
        /*
        Test Case 1: Add and Retrieve
         Input:-> put('key1', 'value1')
                -> get('key1')
          Expected Output: 'value1'
         */
        HighPerformanceCache hpcTest1 = new HighPerformanceCache(cacheConfigTest, null);
        String key1 = "key1";
        String inputValue = "value1";
        hpcTest1.put(key1, inputValue);
        String test1Result = hpcTest1.get(key1);
        assert test1Result.equals(inputValue) : "Expected 'value1', got " + test1Result;
        passTest();
    }

    private static void testRetrieveNonExistentKey(){
        /*
        Test Case 2: Retrieve Non-existent Key
            Input:-> get('keyX') (where 'keyX' is not in cache)
            Expected Output: Retrieve from backing store or null if not found
         */
        HighPerformanceCache hpcTest2 = new HighPerformanceCache(cacheConfigTest, null);
        String test2Result = hpcTest2.get("keyX");
        assert test2Result == null : "Expected null, got " + test2Result;
        passTest();
    }

    private static void testUpdateExistingKey(){
        /*
        Test Case 3: Update Existing Key
            Input:-> put('key1', 'value1')
                     put('key1', 'value2')
                    -> get('key1')
             Expected Output: 'value2'
         */
        HighPerformanceCache hpcTest3 = new HighPerformanceCache(cacheConfigTest, null);
        hpcTest3.put("key1", "value1");
        hpcTest3.put("key1", "value2");
        String test3Result = hpcTest3.get("key1");
        assert test3Result.equals("value2") : "Expected 'value2', got " + test3Result;
        assert hpcTest3.size() == 1 : "Expected size 1, got " + hpcTest3.size();
        passTest();
    }

    private static void testRemoveKey(){
        /*
        Test Case 4: Remove Key
            Input:-> put('key1', 'value1'),
                    -> remove('key1')
                    -> get('key1')
            Expected Output: Retrieve from backing store or null if not found
         */
        HighPerformanceCache hpcTest4 = new HighPerformanceCache(cacheConfigTest, null);
        hpcTest4.put("key1", "value1");
        boolean removed = hpcTest4.remove("key1");
        String test4Result = hpcTest4.get("key1");
        assert removed : "Remove should return true";
        assert test4Result == null : "Expected null after removal";
        assert hpcTest4.size() == 0 : "Expected size 0, got " + hpcTest4.size();
        passTest();
    }

    private static void testCapacityAndEvictionPolicy(){
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
        hpcTest5.put("key1", "value1");
        hpcTest5.put("key2", "value2");
        hpcTest5.put("key3", "value3");
        String test5Result = hpcTest5.get("key1");
        assert hpcTest5.get("key1") == null : "key1 should be evicted";
        assert hpcTest5.get("key2") != null : "key2 should exist";
        assert hpcTest5.get("key3") != null : "key3 should exist";
        assert hpcTest5.size() == 2 : "Expected size 2, got " + hpcTest5.size();
        passTest();
    }
}
