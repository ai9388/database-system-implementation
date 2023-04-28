import java.util.ArrayList;
import java.util.List;

public class Node implements Comparable<Node>{

    public enum NodeType{INTERNAL, LEAF}

    /**
     * N: the branching factor
     */
    protected int N;

    /**
     * min number of pointers this node can have
     */
    int min;

    /**
     * the max number of pointers this node can have
     */
    int max;

    /**
     * the number of pointers in this node
     * */
    int numOfPointers;
    /**
     * parent node above this one
     */
    protected Node parent;

    /**
     * leaf node before this one
     */
    private Node prev;

    /**
     * leaf node after
     */
    private Node next;

    /**
     * array of keys
     */
    protected ArrayList<Object> keys;

    /**
     * the type of node
     */
    private NodeType type;

    /**
     * This serves as both a node pointer and a record pointer
     * Node pointer:
     *      applies to root and internal nodes
     *      [pageId, -1]: -1 means that it only points to a page, not a record.
     * Record pointer:
     *      applies to records
     *      [pageId, record index]
     */
    protected ArrayList<ArrayList<Integer>> pointers;

    /**
     * boolean to make a node as the root
     */
    public boolean isRoot;

    /**
     * The primary column of the table
     */
    private Attribute primaryAttribute;

    public Node(int N, Attribute primaryAttribute, NodeType nodeType) {
        this.N = N;
    }

    // TODO: fix this constructor later
//    public Node(int pageIdx, Object primKey, int size, int N) {
//        this.pageIdx = pageIdx;
//        this.primKey = primKey;
//        this.size = size;
//        this.N = N;
//    }

    public void insert(String key, int index1, int index2) {
//        if (values.size() == 0) {
//            values.add(new ArrayList<>());
//            List<Integer> pointerIndex = values.get(0);
//            pointerIndex.add(index1, index2);
//            size += 1;
//        }
//        else {
//            if (values.get(0).get(0) == -1) {
//                // its a parent node
//                // Check size
//            }
//            else if (values.get(0).get(1) == -1) {
//                // it's a internal node
//                // Check size
//            }
//            else {
//                // it's a leaf node
//                // Check size
//
//            }
//        }

        //TODO: @Newcarlis
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

    public Node getNextNode() {
        return next;
    }

    public Node getPrev() {
        return prev;
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

    /**
     * checks if the node is overflown
     * true if number of nodes exceeds max
     * @return boolean
     */
    public boolean isOverflow() {
        return this.numOfPointers > max;
    }


    @Override
    public int compareTo(Node o) {
        return 0;
    }
}
