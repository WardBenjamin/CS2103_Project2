import sun.util.resources.cldr.be.CalendarData_be_BY;

import java.util.HashMap;
import java.util.Objects;

/**
 * An implementation of <tt>Cache</tt> that uses a least-recently-used (LRU)
 * eviction policy.
 */
public class LRUCache<T, U> implements Cache<T, U> {

	private final HashMap<T, LinkedU> _map = new HashMap<>();
	private final DataProvider<T, U> _baseProvider;

	private final int _capacity;

    private LinkedU mostRecent, leastRecent;

    // The number of times that a get request misses the cache.
    private int _numMisses = 0;

	/**
	 * @param provider the data provider to consult for a cache miss
	 * @param capacity the exact number of (key,value) pairs to store in the cache
	 */
	public LRUCache (DataProvider<T, U> provider, int capacity) {
	    _baseProvider = provider;
	    _capacity = capacity;
	}

	/**
	 * Returns the value associated with the specified key.
	 * @param key the key
	 * @return the value associated with the key
	 */
	public U get (T key) {
	    // If the cache map already contains the key, return it directly (this is _fast_).
	    if(_map.containsKey(key))
		    return _map.get(key).value;

	    _numMisses++;

	    // Generate a new link to add to the linked list.
	    LinkedU link = new LinkedU(key, _baseProvider.get(key));
	    _map.put(key, link);

	    // Insert the link into the linked list.
	    if(leastRecent == null) {
	        // The least recent item only needs updated initially and when we exceed the capacity.
	        leastRecent = link;
        }
        // Save the newest link as the most recent.
        if(mostRecent != null) {
            link.previous = mostRecent;
            mostRecent.next = link;
        }
        mostRecent = link;

	    // Remove an item from the linked list and map.
        if(_map.size() > _capacity) {
            // When we exceed the capacity, remove the least recent element from the map.
            _map.remove(leastRecent.key);
            // Then, make the next-least-recent element the least recent.
            LinkedU next = leastRecent.next;
            next.previous = null;
            leastRecent = next;
        }

        return link.value;
	}

	/**
	 * Returns the number of cache misses since the object's instantiation.
	 * @return the number of cache misses since the object's instantiation.
	 */
	public int getNumMisses () {
		return _numMisses;
	}

    /**
     * Basis of a doubly-linked list containing a value of type {@code <U>} and key of type {@code <T>}.
     */
	class LinkedU {
	    T key;
	    U value;

	    LinkedU next;
	    LinkedU previous;

	    private LinkedU(T key, U value) {
            this.key = key;
	        this.value = value;
        }

        private LinkedU getRoot() {
	        if(next != null)
	            return next.getRoot();
	        return this;
        }

        @Override
        public int hashCode() {
	        return Objects.hash(key);
        }
    }
}
