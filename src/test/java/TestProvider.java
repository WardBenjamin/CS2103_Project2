public class TestProvider implements DataProvider<Integer, String> {
    @Override
    public String get(Integer key) {
        return key.toString();
    }
}
