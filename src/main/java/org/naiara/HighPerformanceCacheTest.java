package org.naiara;

public class HighPerformanceCacheTest {

    static void main() {

        HighPerformanceCache highPerformanceCache = new HighPerformanceCache(2);

        /*
        Test Case 1: Add and Retrieve
         Input:-> put('key1', 'value1')
                -> get('key1')
          Expected Output: 'value1'
         */
        highPerformanceCache.put("key1", "value1");
        System.out.println(highPerformanceCache.get("key1"));


        /*
        Test Case 2: Retrieve Non-existent Key
            Input:-> get('keyX') (where 'keyX' is not in cache)
            Expected Output: Retrieve from backing store or null if not found
         */
        System.out.println(highPerformanceCache.get("keyX"));

        /*
        Test Case 3: Update Existing Key
            Input:-> put('key1', 'value1')
                     put('key1', 'value2')
                    -> get('key1')
             Expected Output: 'value2'
         */
        highPerformanceCache.put("key1", "value1");
        highPerformanceCache.put("key1", "value2");
        System.out.println(highPerformanceCache.get("key1"));

        /*
        Test Case 4: Remove Key
            Input:-> put('key1', 'value1'),
                    -> remove('key1')
                    -> get('key1')
            Expected Output: Retrieve from backing store or null if not found
         */
        highPerformanceCache.put("key1", "value1");
        highPerformanceCache.remove("key1");
        System.out.println(highPerformanceCache.get("key1"));
    }
}
