import java.util.ArrayList;
import java.util.List;

public class Node {
    private int pageIdx;
    private Node parent = null;
    private Node nextNode = null;

    //root 
    // internal node - other node that pages
    //leaf node point page

    public Node(int pageIdx){
        this.pageIdx = pageIdx;

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
