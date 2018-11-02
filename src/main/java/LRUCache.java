import java.util.HashMap;
import java.util.Objects;

/**
 * An implementation of <tt>Cache</tt> that uses a least-recently-used (LRU)
 * eviction policy.
 */
public class LRUCache<T, U> implements Cache<T, U> {

    private final HashMap<T, Link> _map = new HashMap<>();
    private final DataProvider<T, U> _baseProvider;

    private final int _capacity;

    private Link mostRecent, leastRecent;

    // The number of times that a get request misses the cache.
    private int _numMisses = 0;

    /**
     * @param provider the data provider to consult for a cache miss
     * @param capacity the exact number of (key,value) pairs to store in the cache
     */
    public LRUCache(DataProvider<T, U> provider, int capacity) {
        _baseProvider = provider;
        _capacity = capacity;
    }

    /**
     * Returns the value associated with the specified key.
     *
     * @param key the key
     * @return the value associated with the key
     */
    public U get(T key) {
        Link link;

        // If the cache map already contains the key, return it directly (this is _fast_).
        if (_map.containsKey(key)) {
            link = _map.get(key);
            moveToFront(link);
        } else {
            _numMisses++;

            // Generate a new link to add to the linked list.
            link = new Link(key, _baseProvider.get(key));
            insert(link);

            // When we exceed the capacity, remove an item from the linked list and map.
            if (_map.size() > _capacity) {
                removeLeastRecent();
            }
        }

        return link.value;
    }

    /**
     * Returns the number of cache misses since the object's instantiation.
     *
     * @return the number of cache misses since the object's instantiation.
     */
    public int getNumMisses() {
        return _numMisses;
    }

    private void insert(Link link) {
        _map.put(link.key, link);

        // Insert the link into the linked list.
        if (leastRecent == null) {
            // The least recent item only needs updated initially and when we exceed the capacity.
            leastRecent = link;
        }
        // Save the newest link as the most recent.
        if (mostRecent != null) {
            link.previous = mostRecent;
            mostRecent.next = link;
        }
        mostRecent = link;
    }

    private void removeLeastRecent() {
        // Remove the least recent element from the map.
        _map.remove(leastRecent.key);
        // Then, make the next-least-recent element the least recent.
        Link next = leastRecent.next;
        next.previous = null;
        leastRecent = next;
    }

    /**
     * Move the specified linked list node to the front of the list. Since this can only be called when a
     * key is already present in the linked list, a large amount of null checking can be avoided for speed/simplicity.
     * @param link The link to move to the front of the lists
     */
    private void moveToFront(Link link) {
        if(mostRecent.equals(link))
            return;
        // Get the links in front of and behind the specified link and attach them together
        final Link front = link.next;
        final Link behind = link.previous;
        front.previous = behind;
        if(behind != null)
            behind.next = front;
        else
            leastRecent = front;
        // Move the specified link to the front
        link.next = null;
        link.previous = mostRecent;
        mostRecent.next = link;
        mostRecent = link;
    }

    /**
     * Basis of a naive doubly-linked list containing a value of type {@code <U>} and key of type {@code <T>}.
     * This was used instead of the Java LinkedList class since it is less abstract, meaning that the list
     * operations can be simplified for our specific use case.
     */
    class Link {
        T key;
        U value;

        Link next;
        Link previous;

        private Link(T key, U value) {
            this.key = key;
            this.value = value;
        }

        // Unused
        private Link getRoot() {
            if (next != null)
                return next.getRoot();
            return this;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
}
