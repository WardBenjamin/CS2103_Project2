import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Code to test an <tt>LRUCache</tt> implementation.
 */
public class CacheTest {

    class TestProvider implements DataProvider<Integer, String> {
        public int fetches = 0;

        @Override
        public String get(Integer key) {
            fetches++;
            return key.toString();
        }
    }

    final int CAPACITY = 5;

    @Test
    public void leastRecentlyUsedIsCorrect() {
        DataProvider<Integer, String> provider = new TestProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, CAPACITY);

        // Fill the cache
        for (int j = 0; j < CAPACITY; j++) {
            cache.get(j);
        }

        int misses = cache.getNumMisses();

        cache.get(CAPACITY); // Discard 0 from the cache
        assertEquals(cache.getNumMisses(), misses + 1);

        misses = cache.getNumMisses();

        cache.get(0); // Try to get 2, which should miss
        assertEquals(cache.getNumMisses(), misses + 1);
    }

    @Test
    public void leastRecentlyUsedIsCorrect2() {
        DataProvider<Integer, String> provider = new TestProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, CAPACITY);

        // Fill the cache
        for (int j = 0; j < CAPACITY; j++) {
            cache.get(j);
        }

        int misses = cache.getNumMisses();

        cache.get(0); // Move 0 to the front of the cache
        assertEquals(cache.getNumMisses(), misses);

        cache.get(CAPACITY); // Discard 1 from the cache
        assertEquals(cache.getNumMisses(), misses + 1); // Make sure that there was an element discarded

        cache.get(0);
        assertEquals(cache.getNumMisses(), misses + 1); // Make sure that 0 was not the discarded element

        cache.get(1);
        assertEquals(cache.getNumMisses(), misses + 2); // Make sure that 1 was actually the LRU element
    }

    @Test
    public void numMisses() {
        TestProvider provider = new TestProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, CAPACITY);

        final int NUMBER_OF_PASSES = 10; // Arbitrary

        for (int i = 0; i < NUMBER_OF_PASSES; i++) {
            for (int j = 0; j < CAPACITY; j++) {
                cache.get(j);
            }
        }

        assertEquals(cache.getNumMisses(), provider.fetches);
    }

    @Test
    public void numMisses2() {
        TestProvider provider = new TestProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, CAPACITY);

        // Fill cache with initial values
        for (int j = 0; j < CAPACITY; j++) {
            cache.get(j);
        }

        final int initialNumMisses = cache.getNumMisses();

        for (int j = 0; j < CAPACITY; j++) {
            cache.get(j);
        }

        assertEquals(cache.getNumMisses(), initialNumMisses);
    }

    @Test
    public void providerReturnsExpectedResult() {
        TestProvider provider = new TestProvider();
        Cache<Integer, String> cache = new LRUCache<Integer, String>(provider, CAPACITY);

        final int TEST_LENGTH = 10; // Arbitrary

        for (int i = 0; i < TEST_LENGTH; i++) {
            assertEquals(provider.get(i), cache.get(i));
        }
    }
}
