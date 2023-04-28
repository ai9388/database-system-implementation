import java.util.ArrayList;
import java.util.List;

public class Node {
    public boolean isRoot;
    protected int size;
    protected int N;
    private List<String> keys = new ArrayList<>();
    private List<List<Integer>> values = new ArrayList<>();
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

    public void insert(String key, int index1, int index2) {
        if (values.size() == 0) {
            values.add(new ArrayList<>());
            List<Integer> pointerIndex = values.get(0);
            pointerIndex.add(index1, index2);
            size += 1;
        }
        else {
            if (values.get(0).get(0) == -1) {
                // its a parent node
                // Check size
            }
            else if (values.get(0).get(1) == -1) {
                // it's a internal node
                // Check size
            }
            else {
                // it's a leaf node
                // Check size

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
        return null;
    }

    public Node merge(Node n1, Node n2) {
        return null;
    }

    public Node borrow(Node n1, Node n2) {
        return null;
    }
}
