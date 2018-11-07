import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

/**
 * Code to test an <tt>LRUCache</tt> implementation.
 */
public class GuptaTest {
    private DataSource _server;
    private class DataSource<T, U> implements DataProvider<T, U> {
        private HashMap<T, U> _data;
        public String log = "";
        public DataSource() {
            _data = new HashMap<T, U>();
        }

        public void insert(T key, U value) {
            _data.put(key, value);
        }
        public void clearLog(){log = "";}
        public U get(T key) {
            log+= key.toString();
            return _data.get(key);
        }
    }

    public void initServer() {
        this._server = new DataSource<Integer,Integer>();
        for(int i = 0; i <200; i++)
            _server._data.put(i,dataCreator(i));
    }

    public Integer dataCreator(int i) { return ((i * 69 - 420) ^2); }

    /*
    This test verifies that the cache actually returns the correct values, in addition to verifying that the cache
    actually cache's values and only calls the data provider when necessary.
     */
    @Test
    public void testCacheWorksProperly () {

        //Basic tests
        initServer();
        LRUCache<Integer,Integer> testcache = new LRUCache<Integer, Integer>(_server,5);
        assertEquals(testcache.get(1),dataCreator(1));
        assertEquals(_server.log, "1");
        assertEquals(testcache.get(2),dataCreator(2));
        assertEquals(_server.log , "12");
        assertEquals(testcache.get(3),dataCreator(3));
        assertEquals(_server.log , "123");
        assertEquals(testcache.get(4),dataCreator(4));
        assertEquals(_server.log , "1234");
        assertEquals(testcache.get(5),dataCreator(5));
        assertEquals(_server.log , "12345");
        assertEquals(testcache.get(1),dataCreator(1));
        assertEquals(_server.log , "12345");
        assertEquals(testcache.get(6),dataCreator(6));
        assertEquals(_server.log , "123456");
        assertEquals(testcache.get(2),dataCreator(2));
        assertEquals(_server.log , "1234562");
        assertEquals(testcache.getNumMisses(), 7);

        //Testing a large cache
        initServer();
        testcache = new LRUCache<Integer, Integer>(_server, 80);
        for (int i = 0; i < 80; i++) {
            assertEquals(testcache.get(i), dataCreator(i));
        }
        _server.clearLog();
        assertEquals(testcache.get(3),dataCreator(3));
        assertEquals(testcache.get(51),dataCreator(51));
        assertEquals(testcache.get(81),dataCreator(81));
        assertEquals(_server.log, "81");
        assertEquals(testcache.getNumMisses(), 81);

    }

    Cache<Integer, Integer> _testCache;

    @Before
    public void init(){
        initServer();
        _testCache = new LRUCache<Integer, Integer>(_server,5);
    }

    @Test
    public void testCacheSize (){
        _testCache.get(1);
        _testCache.get(2);
        _testCache.get(3);
        _testCache.get(4);
        _testCache.get(5);

        _testCache.get(1);
        assertEquals(_testCache.getNumMisses(), 5);

        _testCache.get(6);
        assertEquals(_testCache.getNumMisses(), 6);

        _testCache.get(2);
        assertEquals(_testCache.getNumMisses(), 7);

        _testCache.get(1);
        assertEquals(_testCache.getNumMisses(), 7);
    }

    @Test
    public void testCacheCorrectness(){
        assertEquals(_testCache.get(1),_server.get(1));
        assertEquals(_testCache.get(1),_server.get(1));
        assertEquals(_testCache.get(6),_server.get(6));
    }

}