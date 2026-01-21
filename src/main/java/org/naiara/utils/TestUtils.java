package org.naiara.utils;

import org.naiara.HighPerformanceCache;
import org.naiara.store.DummyStore;
import org.naiara.store.StoreNode;

import java.util.HashMap;
import java.util.Map;

public class TestUtils {
    public static void passTest(){
        System.out.println("Test Passed!");
    }

    public static DummyStore initStore(){
        Map<String, StoreNode> initialData = new HashMap<>();
        for(int idx = 1; idx <=15; idx++) initialData.put("KEY"+idx, new StoreNode("KEY"+idx, "STORE_VALUE"+idx));
        return new DummyStore(initialData);
    }

    public static void printCacheSize(HighPerformanceCache cache){
        System.out.println("Cache size: " + cache.size());
    }

    public static void triggerSleep(int time){
        try {
            System.out.println("!!!!! SLEEPIING: " + Thread.currentThread().getName());
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
