package org.naiara;

import org.naiara.cache.CacheConfig;
import org.naiara.cache.ExpiryStrategy;
import org.naiara.store.DummyStore;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.naiara.utils.Constants.*;
import static org.naiara.utils.TestUtils.*;

public class ConcurrentOpsTest {

    static final CacheConfig cacheConfig = new CacheConfig(DEAFULT_CACHE_SIZE, null,
            ExpiryStrategy.LAST_ACCESS_BASED_EXPIRY, 25000);
    static final DummyStore dummyStore = initStore();
    static final HighPerformanceCache cacheThreadTest = new HighPerformanceCache(cacheConfig, dummyStore);

    public static void main(String[] args) throws InterruptedException {
        cacheThreadTest.loadFromStore();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
        scheduler.schedule(addEntryTask, 32, TimeUnit.SECONDS);
        scheduler.schedule(getEntryTask, 6, TimeUnit.SECONDS);
        scheduler.schedule(updateEntryTask, 4, TimeUnit.SECONDS);
        scheduler.schedule(removeEntryTask, 10, TimeUnit.SECONDS);
        scheduler.schedule(triggerExpiry, 29, TimeUnit.SECONDS);

        // TODO: Discuss Refresh
        //scheduler.schedule(triggerRefresh, 90, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    static Runnable addEntryTask = () -> {
        for (int idx = 9; idx <= 15; idx++) {
            System.out.println("ADDING " + idx);
            cacheThreadTest.put("KEY"+idx, "CACHE_VALUE"+idx);
            printCacheSize(cacheThreadTest);
            triggerSleep(5000);
        }
    };

    static Runnable updateEntryTask = () -> {
        for (int idx = 1; idx <= 15; idx += 1) {
            System.out.println("UPDATING " + idx);
            cacheThreadTest.put("KEY"+idx, "VALUE_UPDATED"+idx);
            printCacheSize(cacheThreadTest);
            triggerSleep(7000);
        }
    };

    static Runnable removeEntryTask = () -> {
        for (int idx = 1; idx <= 15; idx += 2) {
            System.out.println("REMOVING " + idx);
            cacheThreadTest.remove("KEY"+idx);
            printCacheSize(cacheThreadTest);
            triggerSleep(15000);
        }
    };

    static Runnable triggerExpiry = () -> {
        cacheThreadTest.runExpirationStrategy();
        triggerSleep(30000);
    };

    static Runnable triggerRefresh = () -> {
        cacheThreadTest.runExpirationStrategy();
    };

    static Runnable getEntryTask = () -> {
        for(int i = 0; i < 10; i++){
            int idx = new Random().nextInt(16);
            System.out.println("ACCESSING " + idx);
            cacheThreadTest.get("KEY"+idx);
            triggerSleep(8000);
        }
    };
}
