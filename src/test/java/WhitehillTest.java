import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

/**
 * Code to test an <tt>LRUCache</tt> implementation.
 */
public class WhitehillTest {
    private static final int CAPACITY = 5;
    private static final int NUM_PASSES = 10;
    private Cache<Integer,String> _cache;

    private static class TestDataProvider implements DataProvider<Integer,String> {
        private int _numFetches = 0;
        public String get (Integer i) {
            _numFetches++;
            return "" + i;
        }
        public int getNumFetches () {
            return _numFetches;
        }
    };
    TestDataProvider _provider;

    @Test
    public void canRetrieveResultWithEvictions () {
        // Initialize the cache
        for (int i = 0; i < CAPACITY; i++) {
            _cache.get(i);
        }

        for (int i = 0; i < NUM_PASSES; i++) {
            final int OFFSET = 10;
            for (int j = 0; j < CAPACITY; j++) {
                assertEquals("" + (j + OFFSET), _cache.get(j + OFFSET));
            }
        }
        assertTrue(_provider.getNumFetches() > 0);  // make sure it actually delegates to provider
        System.out.println("CS210XGRDR +3 canRetrieveResultWithEvictions");
    }

    @Test
    public void checkNumMisses () {
        for (int i = 0; i < NUM_PASSES; i++) {
            for (int j = 0; j < CAPACITY; j++) {
                _cache.get(j);
            }
        }

        assertEquals(_provider.getNumFetches(), _cache.getNumMisses());
        System.out.println("CS210XGRDR +2 checkNumMisses");
    }

    @Test
    public void canRetrieveResultWithoutEvictions () {
        for (int i = 0; i < CAPACITY; i++) {
            assertEquals("" + i, _cache.get(i));
        }
        assertTrue(_provider.getNumFetches() > 0);  // make sure it actually delegates to provider
        System.out.println("CS210XGRDR +3 canRetrieveResultWithoutEvictions");
    }

    @Test
    public void doesNotRefetchUnnecessarily () {
        final int NUM_PASSES = 10;
        for (int i = 0; i < NUM_PASSES; i++) {
            for (int j = 0; j < CAPACITY; j++) {
                assertEquals("" + j, _cache.get(j));
            }
        }
        assertEquals(_provider.getNumFetches(), CAPACITY);
        System.out.println("CS210XGRDR +3 doesNotRefetchUnnecessarily");
    }

    @Test
    public void leastRecentlyUsedIsCorrect1 () {
        for (int j = 0; j < CAPACITY; j++) {
            _cache.get(j);
        }
        assertEquals("5", _cache.get(5));  // evict 0, fetch 5
        assertEquals(CAPACITY + 1, _provider.getNumFetches());
        assertEquals("0", _cache.get(0));  // evict 1, fetch 0
        assertEquals(CAPACITY + 2, _provider.getNumFetches());
        assertEquals("5", _cache.get(5));  // no eviction
        assertEquals(CAPACITY + 2, _provider.getNumFetches());
        assertEquals("1", _cache.get(1));  // evict 2, fetch 1
        assertEquals(CAPACITY + 3, _provider.getNumFetches());
        System.out.println("CS210XGRDR +3 leastRecentlyUsedIsCorrect1");
    }

    @Test
    public void leastRecentlyUsedIsCorrect2 () {
        for (int j = 0; j < CAPACITY; j++) {
            _cache.get(j);
        }
        assertEquals("0", _cache.get(0));  // no eviction
        assertEquals(CAPACITY + 0, _provider.getNumFetches());
        assertEquals("5", _cache.get(5));  // evict 1, fetch 5
        assertEquals(CAPACITY + 1, _provider.getNumFetches());
        assertEquals("0", _cache.get(0));  // no eviction
        assertEquals(CAPACITY + 1, _provider.getNumFetches());
        System.out.println("CS210XGRDR +3 leastRecentlyUsedIsCorrect2");
    }

    @Before
    public void setUp () {
        _provider = new TestDataProvider();
        _cache = new LRUCache<Integer,String>(_provider, CAPACITY);
    }
}