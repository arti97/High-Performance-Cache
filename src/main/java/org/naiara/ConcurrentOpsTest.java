package org.naiara;

import org.naiara.cache.CacheConfig;
import org.naiara.cache.ExpiryStrategy;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.naiara.utils.Constants.*;

public class ConcurrentOpsTest {

    public static void main(String[] args) throws InterruptedException {

        final CacheConfig cacheConfig = new CacheConfig(DEAFULT_CACHE_SIZE, null,
                ExpiryStrategy.LAST_ACCESS_BASED_EXPIRY, 25000);
        final HighPerformanceCache cacheThreadTest = new HighPerformanceCache(cacheConfig, null);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

        Runnable addEntryTask = () -> {
            for (int idx = 1; idx <= 15; idx++) {
                System.out.println("ADDING " + idx);
                cacheThreadTest.put("KEY"+idx, "VALUE"+idx);
                printCacheSize(cacheThreadTest);
                triggerSleep(5000);
            }
        };

        Runnable updateEntryTask = () -> {
            for (int idx = 1; idx <= 15; idx += 1) {
                System.out.println("UPDATING " + idx);
                cacheThreadTest.put("KEY"+idx, "VALUE UPDATED"+idx);
                printCacheSize(cacheThreadTest);
                triggerSleep(7000);
            }
        };

        Runnable removeEntryTask = () -> {
            for (int idx = 1; idx <= 15; idx += 2) {
                System.out.println("REMOVING " + idx);
                cacheThreadTest.remove("KEY"+idx);
                printCacheSize(cacheThreadTest);
                triggerSleep(15000);
            }
        };

        Runnable triggerExpiry = () -> {
            cacheThreadTest.runExpirationStrategy();
            triggerSleep(30000);
        };

        scheduler.schedule(addEntryTask, 0, TimeUnit.SECONDS);
        scheduler.schedule(updateEntryTask, 4, TimeUnit.SECONDS);
        scheduler.schedule(removeEntryTask, 10, TimeUnit.SECONDS);
        scheduler.schedule(triggerExpiry, 29, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    private static void printCacheSize(HighPerformanceCache cache){
        System.out.println("Cache size: " + cache.size());
    }

    private static void triggerSleep(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
