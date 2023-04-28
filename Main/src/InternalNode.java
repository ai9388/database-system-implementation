import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InternalNode extends Node {
    private List<Object> keys = new ArrayList<>();
    private List<Node> children = new ArrayList<>();

    public InternalNode(int pageIdx, Object primKey, int size, int N) {
        super(pageIdx, primKey, size, N);
    }

    public Node get(Object key) {
        for (int i = 0; i < keys.size(); i++) {
            if (key.getClass().equals(keys.get(i).getClass())) {
                
            }
            // int compare = keys.get(i).compareTo(key);
            if (key.equals(keys.get(i))) {
                return children.get(i - 1);
            }
        }
        return null;
    }

    public void put(Object key, Object value) {
        if (value instanceof  Record) {
            this.put(key, (Record) value);
        }
    }

    public void put(Object key, Record value) {

    }

    public boolean isOverflow() {
        return false;
    }

    public Node split() {
        return null;
    }
}