import java.util.ArrayList;
import java.util.Arrays;
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
     * array of values
     */
    private ArrayList<Object> values;

    /**
     * the type of node
     */
    private NodeType type;

    /**
     * pages
     */
    private ArrayList<Page> pages;

    /**
     * name of table
     */
    private String tableName;

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
    public Node(int N, TableSchema tableSchema, Node parent, NodeType nodeType, Record record) {
        this.primaryAttribute = tableSchema.getPrimaryAttribute();
        this.tableName = tableSchema.getName();
        this.parent = parent;
        this.isRoot = this.parent == null;
        this.type = nodeType;
        this.N = N;
        // make call to set min and max value
        setLimits();

        // call insert to insert the record into this node
        insert(record, 0, 0);
    }


    /**
     * used to create internal node (no record)
     * @param N
     * @param primaryAttribute
     * @param parent
     * @param nodeType
     */
    public Node(int N, Attribute primaryAttribute, Node parent, NodeType nodeType){
        this.primaryAttribute = primaryAttribute;
        this.parent = parent;
        this.isRoot = this.parent == null;
        this.type = nodeType;
        this.N = N;
        // make call to set min and max value
        setLimits();
    }

    // TODO: fix this constructor later
//    public Node(int pageIdx, Object primKey, int size, int N) {
//        this.pageIdx = pageIdx;
//        this.primKey = primKey;
//        this.size = size;
//        this.N = N;
//    }

    public boolean insert(Record record, int index, int pageNumber) {
        if (this.isFull()) {
            return false;
        }

        this.insertKey(index, record.getPrimaryObject());

        // create the page initially
        // get the page number
        // iterate over the page records to find the record index

        int b = -1;
        for (int i = 0; i < pages.size(); i++) {
            if (pages.get(i).getId() == pageNumber)
            {
                b = i;
                break;
            }
        }

        //check if we do not have page
        if (b == -1)
        {
            // we do NOT have the page
            // make the page
            Page p = new Page(pageNumber, this.tableName);
            this.pages.add(p);
            p.addRecord(record);
        }
        else
        {
            // we have an existing page
            Page p = pages.get(b);
            p.addRecord(record);
            this.insertPointer(index, new ArrayList<Integer>(Arrays.asList(new Integer[]{pageNumber, p.getIndexOf(record)})));
        }

        return true;
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

    public boolean delete(String key) {
        // TODO: Delete

        if (isRoot) {
            // Root
            // TODO: Check size

            for (Object o : keys) {
                //if (o.compareTo(key) == -1) {
                    // TODO: Go to child node and call delete
                //    break;
                //}
            }
        }
        else if (pointers.get(0).get(1) == -1) {
            // Internal Node
            // TODO: Check size

            switch(primaryAttribute.getType()){
                case INTEGER:
                    keys.remove(Integer.parseInt(key));
                    break;
                case DOUBLE:
                    keys.remove(Double.parseDouble(key));
                    break;
                case BOOLEAN:
                    keys.remove(Boolean.parseBoolean(key));
                    break;
                case VARCHAR:
                    keys.remove(key);
                    break;
                case CHAR:
                    keys.remove(key);
                    break;
                }
            }
        else {
            // Leaf
            // TODO: Check size

            for (Object k : keys) {
                if (k.equals(k)){
                    int index = keys.indexOf(k);
                    // TODO: Shift page/record pointers up
                    keys.remove(index);
                    pointers.remove(index);
                    values.remove(index);
                    numOfPointers--;
                    break;
                }
            }
        }
        return false;
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

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Node getParent() {
        return parent;
    }

    public Node[] split(Node node) {
        // TODO: Split
        return null;
    }

    public Node merge(Node n1, Node n2) {
        // preconditions: merging is able to happen
        // if the parents are the same, then we can just combine the two nodes
        if (n1.parent.equals(n2.parent))
        {
            for (int i = 0; i < n1.keys.size(); i++) 
            {
                n1.keys.add(n2.keys.get(i));    
            }

            for (int i = 0; i < n1.pointers.size(); i++) 
            {
                n1.pointers.add(n2.pointers.get(i));
            }

            n1.numOfPointers += n2.pointers.size();

            n1.next = n2.next;

            n2.parent = null;
            n2.prev = null;
            n2.next = null;
        }

        return null;
    }

    public Node borrow(Node n1, Node n2) {
        // TODO: Borrow
        return null;
    }

    /**
     * checks if the node is full
     * true if number of nodes exceeds max
     * @return boolean
     */
    public boolean isFull() {
        return this.numOfPointers == max;
    }

    /**
     * checks if the node is overflown
     * true if number of nodes exceeds max
     * @return boolean
     */
    public boolean isOverfull() {
        return this.numOfPointers > max;
    }

    public boolean canBorrowLeft() {
        return prev.numOfPointers - 1 >= prev.min;
    }

    public boolean canBorrowRight() {
        return next.numOfPointers - 1 >= next.min;
    }

    public boolean canMergeLeft() {
        return (prev.numOfPointers + this.numOfPointers - 1) <= this.max;
    }

    public boolean canMergeRight() {
        return (next.numOfPointers + this.numOfPointers - 1) <= this.max;
    }

    public ArrayList<Object> getKeys() {
        return keys;
    }

    public ArrayList<Object> getValues() {
        return values;
    }

    public void setKeys(ArrayList<Object> keys) {
        this.keys = keys;
    }

    public void setPointers(ArrayList<ArrayList<Integer>> pointers) {
        this.pointers = pointers;
    }

    public Object getKey(int i){
        return keys.get(i);
    }

    public void insertKey(int index, Object key){
        this.keys.add(index, key);
    }

    public void insertPointer(int index, ArrayList<Integer> pointer){
        this.pointers.add(index, pointer);
    }

    public void deleteKey(Object key){
        this.keys.remove(key);
    }

    public void deletePointer(int index){
        this.pointers.remove(index);
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
        return (Node) values.get(idx);
    }

    public Object getValue(String key) {
        for (int i = 0; i < keys.size(); i++) {
            if (String.valueOf(keys.get(i)).equals(key)) {
                return values.get(i);
            }
        }
        return null;
    }

    public int getIndexValue(String key) {
        for (int i = 0; i < keys.size(); i++) {
            if (String.valueOf(keys.get(i)).equals(key)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int compareTo(Node o) {
        return 0;
    }

    public NodeType getType() {
        return type;
    }
}
