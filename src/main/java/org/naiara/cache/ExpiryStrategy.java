package org.naiara.cache;

public enum ExpiryStrategy {

    NO_EXPIRY{
        @Override
        boolean isExpired(CacheNode cacheNode, long ttl){
            return false;
        }
    },
    CREATE_TIME_BASED_EXPIRY{
        @Override
        boolean isExpired(CacheNode cacheNode, long ttl){
            long age = (System.currentTimeMillis() - cacheNode.getCreatedTime());
            System.out.println("Current Age: "+ age);
            return age >= ttl;
        }
    },
    LAST_ACCESS_BASED_EXPIRY{
        @Override
        boolean isExpired(CacheNode cacheNode, long ttl){
            long age = (System.currentTimeMillis() - cacheNode.getLastAccessedTime());
            System.out.println("Current Age: "+ age);
            return age >= ttl;
        }
    };

    abstract boolean isExpired(CacheNode cacheNode, long ttl);
}
