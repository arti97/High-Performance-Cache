package org.naiara;

import org.naiara.cacheconfig.CacheConfig;

import static org.naiara.TestUtils.passTest;
import static org.naiara.utils.Constants.DEAFULT_CACHE_SIZE;

public class HighPerformanceCacheTest {

    //TODO: add both happy path tests and others

    static void main() {

        /*
        Test Case 1: Add and Retrieve
         Input:-> put('key1', 'value1')
                -> get('key1')
          Expected Output: 'value1'
         */
        HighPerformanceCache hpcTest1 = new HighPerformanceCache(new CacheConfig(DEAFULT_CACHE_SIZE));
        String key1 = "key1";
        String inputValue = "value1";
        hpcTest1.put(key1, inputValue);
        String test1Result = hpcTest1.get(key1);
        assert test1Result.equals(inputValue) : "Expected 'value1', got " + test1Result;
        passTest();

        /*
        Test Case 2: Retrieve Non-existent Key
            Input:-> get('keyX') (where 'keyX' is not in cache)
            Expected Output: Retrieve from backing store or null if not found
         */
        String test2Result = hpcTest1.get("keyX");
        assert test2Result == null : "Expected null, got " + test2Result;
        passTest();

        /*
        Test Case 3: Update Existing Key
            Input:-> put('key1', 'value1')
                     put('key1', 'value2')
                    -> get('key1')
             Expected Output: 'value2'
         */
        HighPerformanceCache hpcTest3 = new HighPerformanceCache(new CacheConfig(DEAFULT_CACHE_SIZE));
        hpcTest3.put("key1", "value1");
        hpcTest3.put("key1", "value2");
        String test3Result = hpcTest3.get("key1");
        assert test3Result.equals("value2") : "Expected 'value2', got " + test3Result;
        assert hpcTest3.size() == 1 : "Expected size 1, got " + hpcTest3.size();
        passTest();

        /*
        Test Case 4: Remove Key
            Input:-> put('key1', 'value1'),
                    -> remove('key1')
                    -> get('key1')
            Expected Output: Retrieve from backing store or null if not found
         */
        HighPerformanceCache hpcTest4 = new HighPerformanceCache(new CacheConfig(DEAFULT_CACHE_SIZE));
        hpcTest4.put("key1", "value1");
        boolean removed = hpcTest4.remove("key1");
        String test4Result = hpcTest4.get("key1");
        assert removed : "Remove should return true";
        assert test4Result == null : "Expected null after removal";
        assert hpcTest4.size() == 0 : "Expected size 0, got " + hpcTest4.size();
        passTest();
    }
}
