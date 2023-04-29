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
    private ArrayList<Object> keys;

    /**
     * array of children nodes
     */
    private ArrayList<Node> children;

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
    private ArrayList<ArrayList<Integer>> pointers;

    /**
     * boolean to make a node as the root
     */
    public boolean isRoot;

    /**
     * The primary column of the table
     */
    private Attribute primaryAttribute;

    /**
     * Constructor used to create a new node with one record pointer on it
     * @param N the tree's branching factor. used to calculate min and max
     * @param primaryAttribute the attribute used for indexing
     * @param parent the parent node (could be null if root)
     * @param nodeType the type of Node (LEAF)
     * @param record the record that will be inserted
     */
    public Node(int N, Attribute primaryAttribute, Node parent, NodeType nodeType, Record record) {
        this.primaryAttribute = primaryAttribute;
        this.parent = parent;
        this.isRoot = this.parent == null;
        this.type = nodeType;
        this.N = N;
        // make call to set min and max value
        setLimits();

        // call insert to insert the record into this node
        insert(record);
    }

    // TODO: fix this constructor later
//    public Node(int pageIdx, Object primKey, int size, int N) {
//        this.pageIdx = pageIdx;
//        this.primKey = primKey;
//        this.size = size;
//        this.N = N;
//    }

    public boolean insert(Record record) {
        if (this.isFull()) {
            return false;
        } else {
        }

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

        return false;
    }


    public void insertChildNode(Node node){

    }


    /**
     * sets the min and max values based on N and the node type
     */
    public void setLimits(){

        if(type == NodeType.LEAF){
            this.min = Math.ceilDiv(N-1,2);
            this.max = N - 1;
        }
        else if(type == NodeType.INTERNAL){
            this.min = Math.ceilDiv(N, 2);
            this.max = N;
        }
        if(isRoot){
            this.min = 2;
        }
    }

    /**
     * method to change the nodeType if needed
     * also makes sure to change the min and max values
     * @param type
     */
    public void setType(NodeType type) {
        this.type = type;
        setLimits();
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
    public boolean isFull() {
        return this.numOfPointers == max;
    }

    public ArrayList<Object> getKeys() {
        return keys;
    }


    public Object getKey(int i){
        return keys.get(i);
    }

    public void insertKey(int index, Object key){
        this.keys.add(index, key);
    }

    public void insertPointer(int index, ArrayList<Integer> pointer){
        this.keys.add(index, pointer);
    }

    public ArrayList<ArrayList<Integer>> getPointers() {
        return pointers;
    }

    public ArrayList<Integer> getPointerByIdx(int index){
        return pointers.get(index);
    }

    public ArrayList<Object> splitKeys(int midPoint){
        ArrayList<Object> newKeys = new ArrayList<>();

        for(int i = midPoint + 1; i < this.N; i++){
            Object key = this.keys.remove(i);
            newKeys.add(key);
        }

        return newKeys;
    }


    public ArrayList<ArrayList<Integer>> splitPointers(int midPoint){
        ArrayList<ArrayList<Integer>> newPointer = new ArrayList<>();

        for(int i = midPoint + 1; i < this.N; i++){
            ArrayList<Integer> pointer = this.pointers.remove(i);
            newPointer.add(pointer);
        }

        return newPointer;
    }

    public Node getNodebyPointer(ArrayList<Integer> pointer){
        int idx = pointer.get(0);
        return children.get(idx);
    }

    @Override
    public int compareTo(Node o) {
        return 0;
    }

    public NodeType getType() {
        return type;
    }
}
