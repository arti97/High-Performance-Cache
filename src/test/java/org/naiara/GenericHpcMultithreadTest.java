package org.naiara;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.naiara.cache.CacheConfig;
import org.naiara.cache.ExpiryStrategy;
import org.naiara.store.storeV2.BackStore;
import org.naiara.store.storeV2.GenericStoreNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.naiara.utils.Constants.*;
import static org.naiara.utils.TestUtils.triggerSleep;

public class GenericHpcMultithreadTest {
    private final CacheConfig cacheConfigMultithreadTest = new CacheConfig(DEAFULT_CACHE_SIZE, null,
            ExpiryStrategy.LAST_ACCESS_BASED_EXPIRY, 25000);
    private final ScheduledExecutorService testScheduler = Executors.newScheduledThreadPool(4);
    private static BackStore<String, String> storeLoaderTest;

    @BeforeAll
    public static void setUp(){
        Map<String, GenericStoreNode<String, String>> testStoreMap = new HashMap<>();
        for(int idx = 1; idx<15; idx++){
            String key = "KEY"+idx;
            String value = "VALUE"+idx;
            GenericStoreNode<String, String> newStoreNode = new GenericStoreNode<>(key, value);
            testStoreMap.put(key, newStoreNode);
        }
        storeLoaderTest = new BackStore<>(testStoreMap);
    }

    @Test
    void testConcurrentOps() throws Exception {
        try(GenericHighPerformanceCache<String, String> genericHpcMultithreadTest = new GenericHighPerformanceCache<>(cacheConfigMultithreadTest, storeLoaderTest)){
            Runnable addEntryTask = () -> {
                for (int idx = 20; idx>15; idx--) {
                    System.out.println("IN THREAD " + Thread.currentThread().getName());
                    System.out.println("ADDING " + idx);
                    genericHpcMultithreadTest.put("KEY"+idx, "CACHE_VALUE"+idx);
                    printCacheSize(genericHpcMultithreadTest);
                    triggerSleep(5000);
                }
            };

            Runnable updateEntryTask = () -> {
                for (int idx = 10; idx <= 15; idx += 1) {
                    System.out.println("IN THREAD " + Thread.currentThread().getName());
                    System.out.println("UPDATING " + idx);
                    genericHpcMultithreadTest.put("KEY"+idx, "VALUE_UPDATED"+idx);
                    printCacheSize(genericHpcMultithreadTest);
                    triggerSleep(7000);
                }
            };

            Runnable removeEntryTask = () -> {
                for (int idx = 1; idx <= 20; idx += 3) {
                    System.out.println("IN THREAD " + Thread.currentThread().getName());
                    System.out.println("REMOVING " + idx);
                    genericHpcMultithreadTest.remove("KEY"+idx);
                    printCacheSize(genericHpcMultithreadTest);
                    triggerSleep(15000);
                }
            };

            Runnable getEntryTask = () -> {
                System.out.println("IN THREAD " + Thread.currentThread().getName());
                int idx = new Random().nextInt(20);
                System.out.println("ACCESSING " + idx);
                genericHpcMultithreadTest.get("KEY"+idx);
                printCacheSize(genericHpcMultithreadTest);
                triggerSleep(10000);
            };
            testScheduler.schedule(addEntryTask, 10, TimeUnit.SECONDS);
            testScheduler.schedule(getEntryTask, 10, TimeUnit.SECONDS);
            testScheduler.schedule(updateEntryTask, 15,  TimeUnit.SECONDS);
            testScheduler.schedule(removeEntryTask, 20, TimeUnit.SECONDS);
            System.out.println("MAIN THREAD SLEEPING! " + Thread.currentThread().getName());
            Thread.sleep(120000);
            System.out.println("MAIN THREAD ACTIVATED AGAIN " + Thread.currentThread().getName());
            testScheduler.shutdown();
        }
    }


    public void printCacheSize(GenericHighPerformanceCache<String, String> cache){
        System.out.println("Cache size: " + cache.size());
    }
}
