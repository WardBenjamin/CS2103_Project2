/**
 * CS 2103 2018 B-term (Whitehill)
 * A cache that associates keys with values.
 */
interface Cache<T, U> extends DataProvider<T, U> {
	/**
	 * Returns the number of cache misses since the object's instantiation.
	 * @return the number of cache misses since the object's instantiation.
	 */
	int getNumMisses ();
}
