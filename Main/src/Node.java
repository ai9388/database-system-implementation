import java.util.ArrayList;
import java.util.List;

public class Node {
    private int pageIdx;
    protected int size;
    protected int N;
    private Node parent = null;
    private Node nextNode = null;
    private Object primKey;
    public boolean isRoot;

    //root 
    // internal node - other node that pages
    //leaf node point page

    public Node(int pageIdx, Object primKey, int size, int N) {
        this.pageIdx = pageIdx;
        this.primKey = primKey;
        this.size = size;
        this.N = N;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public int getPageIdx() {
        return pageIdx;
    }

    public Node getParent() {
        return parent;
    }
}
