import java.util.ArrayList;
import java.util.List;

public class LeafNode extends Node {
    private List<Object> keys = new ArrayList<>();
    private List<Record> values = new ArrayList<>();
    private LeafNode next;

    public LeafNode(int pageIdx, Object primKey, int size, int N) {
        super(pageIdx, primKey, size, N);
    }

    public Record get(Object key) {
        int i = 0;
        for (Object k : keys) {
            if (k.equals(key)) {
                return values.get(keys.indexOf(k));
            }
        }
        return null;
    }

    public void put(Object key, Record value) {
        if (size + 1 > this.N) {
            // Split node into 2 leaf nodes
            // Middle key of split leaf is moved up to parent
            // If parent more than max # of keys, split parent
        }
        else {
            keys.add(key);
            values.add(value);
        }
    }

    public boolean isOverflow() {
        return (size + 1 > N) || (size + 1 < (N / 2));
    }

    public Node[] split() {
        Node[] children = new Node[2];

        return null;
    }

    public Node merge(Node n1, Node n2) {
        return null;
    }

    public Node borrow(Node n1, Node n2) {
        return null;
    }
}
