# High-performance-Cache
PhonePe Interview | Machine Coding round

Documenting all decisions/thoughts here.

First, since the description of put(key, value) says '...If the cache is at capacity, evict the *least recently used* item...', this is clearly supposed to be an LRU cache.

So now the question is which data structure to use?
Array/ArrayList is the first data structure that comes to minf, but any get/put op will be O(n) time/space complexity.
Can we do better?

Hashmaps are the next obvious answer -> which reduces our get/put time to O(1), but then how to tracking LRU will be O(n). We need something to track BOTH LRU and MRU...a list can be used in combination to achieve this.