import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Code to test an <tt>LRUCache</tt> implementation.
 */
public class CacheTest {
	@Test
	public void leastRecentlyUsedIsCorrect () {
		DataProvider<Integer,String> provider = new TestProvider(); // Need to instantiate an actual DataProvider
		Cache<Integer,String> cache = new LRUCache<Integer,String>(provider, 5);
        cache.get(1);
        cache.get(2);
        cache.get(3);
        cache.get(4);
        cache.get(5);
        int misses = cache.getNumMisses();
        cache.get(1);
        assertEquals(cache.getNumMisses(), misses);
        cache.get(6);
        assertEquals(cache.getNumMisses(), misses + 1);
        cache.get(1);
        assertEquals(cache.getNumMisses(), misses + 1);
    }
}
