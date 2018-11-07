import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Code to test an <tt>LRUCache</tt> implementation.
 */
public class RaviTest {
    private Provider provider;
    private static class Provider implements DataProvider<Integer,String> {
        public Provider() {}
        public String get(Integer key) {
            return "" + key;
        }
    }
    /**
     * Tests that the Cache is providing everything correctly, and misses are being
     * counted fine.
     */
    @Test
    public void cacheProvidingIsCorrect() {
        final LRUCache<Integer,String> cache = new LRUCache<Integer,String>(provider, 3);
        assertEquals(cache.get(1),"1");
        cache.get(2);
        cache.get(3);
        assertEquals(cache.getNumMisses(), 3);
        cache.get(4); //get rid of 1 for 4
        assertEquals(cache.getNumMisses(), 4);
        cache.get(2); //get 2, doesn't need to provide
        assertEquals(cache.getNumMisses(), 4);
        cache.get(5); //get rid of 3 for 5
        assertEquals(cache.getNumMisses(), 5);
        cache.get(2);
        cache.get(5);
        cache.get(4);
        //should not need to provide for any of these
        assertEquals(cache.getNumMisses(), 5);
    }

    /**
     * Tests that the cache is missing correctly and evicting is working fine.
     */
    @Test
    public void testMisses() {
        final LRUCache<Integer,String> cache = new LRUCache<Integer,String>(provider, 3);
        for(int i = 0; i < 50; i++) {
            cache.get(i); //get all num from 0 to 49, should have 47/48/49 in cache and 50 misses
        }
        cache.get(49); //gets 49, should have 50 misses still
        assertEquals(cache.getNumMisses(), 50);
        cache.get(42); //gets 42, 42 was in but now isn't, so should get 51 misses
        assertEquals(cache.getNumMisses(), 51); //cache should be 42 49 48
        cache.get(47); //testing that 47 was evicted
        assertEquals(cache.getNumMisses(), 52);
    }

    /**
     * Testing that nothing will be evicted. as you are just looping through the same 3 numbers over and
     * over again.
     */
    @Test
    public void testLackOfEviction() {
        final LRUCache<Integer,String> cache = new LRUCache<Integer,String>(provider, 3);
        for(int i = 0; i < 23; i++) {
            for(int j = 0; j < 3; j++) {
                cache.get(j);
                //just going through the same numbers 23 times.
            }
        }
        assertEquals(3,cache.getNumMisses()); //should only be 3 misses from the first pass.
    }

    /**
     * Makes sure that the cache can handle big numbers and that runtime is not
     * change by big numbers.
     */
    @Test
    public void testBigCache() {
        final LRUCache<Integer,String> cache = new LRUCache<Integer,String>(provider, 500);
        for(int i = 0; i < 500; i++) {
            cache.get(i);
        }
        assertEquals(cache.getNumMisses(), 500); //all misses
        cache.get(499); //get last element
        assertEquals(cache.getNumMisses(), 500);
        cache.get(254); //get middle element
        assertEquals(cache.getNumMisses(), 500);
        cache.get(254); //get first element
        assertEquals(cache.getNumMisses(), 500);
        cache.get(35240324); //tests evicting from big list
        assertEquals(cache.getNumMisses(), 501);
    }



    @Before
    public void init() {
        provider = new Provider();
    }
}
