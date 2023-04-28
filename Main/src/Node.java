import java.util.ArrayList;
import java.util.List;

public class Node {
    public boolean isRoot;
    protected int size;
    protected int N;
    private List<String> keys = new ArrayList<>();
    private List<List<Integer>> pointers = new ArrayList<>();

    // TODO: If we end up getting serialization we can delete values list
    private List<String> values = new ArrayList<>();

    private Node parent;

    // I don't think we need these??
    private int pageIdx;
    // I think we can find this when getting record from page
    // or the primary column in the table
    private Object primKey;

    public Node(int pageIdx, Object primKey, int size, int N) {
        this.pageIdx = pageIdx;
        this.primKey = primKey;
        this.size = size;
        this.N = N;
    }

    public void insert(String key, String value, int index1, int index2) {
        if (pointers.size() == 0) {
            List<Integer> pointerIndex = new ArrayList<>();
            pointerIndex.add(index1, index2);
            pointers.add(pointerIndex);
            size++;
        }
        else {
            if (pointers.get(0).get(0) == -1) {
                // It's a parent node
                // TODO: Check size

                // Add pointer locations to pointers if size correct
                List<Integer> pointerIndex = new ArrayList<>();
                pointerIndex.add(index1, index2);
                pointers.add(pointerIndex);
                // Add key to keys if size correct
                keys.add(key);
                // Add value to values if size correct
                values.add(value);
                size++;

            }
            else if (pointers.get(0).get(1) == -1) {
                // It's a internal node
                // TODO: Check size

                // Add pointer locations to pointers if size correct
                List<Integer> pointerIndex = new ArrayList<>();
                pointerIndex.add(index1, index2);
                pointers.add(pointerIndex);
                // Add key to keys if size correct
                keys.add(key);
                // Add value to values if size correct
                values.add(value);
                size++;
            }
            else {
                // it's a leaf node
                // TODO: Check size

                // Add pointer locations to pointers if size correct
                List<Integer> pointerIndex = new ArrayList<>();
                pointerIndex.add(index1, index2);
                pointers.add(pointerIndex);
                // Add key to keys if size correct
                keys.add(key);
                // Add value to values if size correct
                values.add(value);
                size++;
            }
        }
    }

    public void delete(String key) {
        // TODO: Delete

        if (isRoot) {
            // Root
            // TODO: Check size

            for (String k : keys) {
                if (k.compareTo(key) == -1) {
                    // TODO: Go to child node and call delete
                    break;
                }
            }
        }
        else if (pointers.get(0).get(1) == -1) {
            // Internal Node
            // TODO: Check size

            for (String k : keys) {
                if (k.compareTo(key) == -1) {
                    // TODO: Go to child node and call delete
                    break;
                }
            }
        }
        else {
            // Leaf
            // TODO: Check size

            for (String k : keys) {
                if (k.equals(key)){
                    int index = keys.indexOf(k);
                    // TODO: Shift page/record pointers up
                    keys.remove(index);
                    pointers.remove(index);
                    values.remove(index);
                    size--;
                    break;
                }
            }
        }
    }

    public boolean getRoot(){
        return this.isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public int getPageIdx() {
        return pageIdx;
    }

    public Node getParent() {
        return parent;
    }

    public Node[] split(Node node) {
        // TODO: Split
        return null;
    }

    public Node merge(Node n1, Node n2) {
        // TODO: Merge
        return null;
    }

    public Node borrow(Node n1, Node n2) {
        // TODO: Borrow
        return null;
    }
}
