import com.sun.jdi.ArrayReference;

import java.util.*;

public class Node implements Comparable<Node>{

    public enum NodeType{INTERNAL, LEAF}


    private static int pageId;
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
     * array of nodes (only for internal nodes)
     */
    private ArrayList<Node> values;

    /**
     * the type of node
     */
    private NodeType type;

    /**
     * pages
     */
    private static HashMap<Integer, Page> pages = new HashMap<>();
    private static HashMap<Object, ArrayList<Integer>> affectedBySplit = new HashMap<>();

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
     * @param tableSchema the table used
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
        this.keys = new ArrayList<>();
        this.pointers = new ArrayList<>();
        this.values = new ArrayList<>();
        // make call to set min and max value
        setLimits();

        // call insert to insert the record into this node
        insert(record, 0);
    }


    /**
     * used to create internal node (no record)
     * @param N
     * @param parent
     * @param nodeType
     */
    public Node(int N, TableSchema tableSchema, Node parent, NodeType nodeType){
        this.primaryAttribute = tableSchema.getPrimaryAttribute();
        this.tableName = tableSchema.getName();
        this.parent = parent;
        this.isRoot = this.parent == null;
        this.type = nodeType;
        this.N = N;
        this.keys = new ArrayList<>();
        this.pointers = new ArrayList<>();
        this.values = new ArrayList<>();
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

    public boolean insert(Record record, int index) {
        if (this.isFull()) {
            return false;
        }

        // insert this record key
        this.insertKey(index, record.getPrimaryObject());
        this.saveRecordAndCreateRecordPointer(index, record);
        return true;
    }

    public void forceInsert(Record record, int index){
        // insert this record pointer
        this.insertKey(index, record.getPrimaryObject());
        this.saveRecordAndCreateRecordPointer(index, record);
    }

    /**
     * index where they key goes
     * @param index
     * @return
     */
    public void saveRecordAndCreateRecordPointer(int index, Record r){
        // try and determine the page number
        // case 1: key index is 0 and there are no pages
        if(index == 0 && pages.size() == 0)
        {
            // create the first page
            Page page = new Page(0, this.tableName);
            pages.put(0, page);
            page.insertRecordAt(r, 0);
            // both the page number and record index are 0
            this.pointers.add(new ArrayList<>(Arrays.asList(0, 0)));
        }

        // case 2: key index is 0 and there are some pages
        else if(index == 0 && pages.size() >= 1)
        {
            // Try to insert it in the page used by the same pointer
            int firstPageUsedInThisLeafNode = this.getPointers().get(0).get(0);
            boolean res = attemptToInsertToPage(r, firstPageUsedInThisLeafNode, index);
            boolean res2 = false;

            if(!res){ // if it could not be inserted in the first page
                // try to insert in to the last page used by the leaf node before this one(if any)
                if(this.prev != null){
                    int lastPageUsedByLeftSibling = prev.getPointerByIdx(prev.getNumOfPointers() - 1).get(0);
                    res2 = attemptToInsertToPage(r, lastPageUsedByLeftSibling, index);
                }
                if(!res2){ // if the previous is null, or it exists but record doesn't fit
                    // force insert the record into the page and then cause a split
                    Page initialPage = pages.get(pageId);
                    initialPage.addLast(r);
                    this.insertPointer(index, new ArrayList<>(Arrays.asList(pageId, initialPage.getIndexOf(r))));
                    handlePageSplitting(initialPage);
                }

            }
        }

        // case 3: key index is last index and there are some pages
        else if (index == this.keys.size() - 1 && pages.size() >= 1)
        {
            int lastPageUsedInThisLeafNode = this.getPointers().get(this.pointers.size() - 1).get(0);
            boolean res = attemptToInsertToPage(r, lastPageUsedInThisLeafNode, index);
            boolean res2 = false;

            if (!res)
            { // if it could not be inserted in the last page
              // try to insert in to the next page used by the leaf node before this one(if any)
                if (this.next != null)
                {
                    int firstPageUsedByLeftSibling = next.getPointerByIdx(0).get(0);
                    res2 = attemptToInsertToPage(r, firstPageUsedByLeftSibling, index);
                }
                if (!res2)
                { // if the next is null, or it exists but record doesn't fit
                  // force insert the record into the page and then cause a split
                    Page initialPage = pages.get(pageId);
                    initialPage.addLast(r);
                    this.insertPointer(index, new ArrayList<>(Arrays.asList(pageId, initialPage.getIndexOf(r))));
                    handlePageSplitting(initialPage);
                }
            }
        }

        // everything in between
        else
        {
            // attempting to insert at the given index
            int lastPageUsedInThisLeafNode = this.getPointers().get(index).get(0);
            boolean res = attemptToInsertToPage(r, lastPageUsedInThisLeafNode, index);

            // if insertion did not work, we try again and subtract from the index
            if (!res)
            {
                lastPageUsedInThisLeafNode = this.getPointers().get(index - 1).get(0);
                res = attemptToInsertToPage(r, lastPageUsedInThisLeafNode, index);

                // if it still doesn't insert, we must split
                if (!res)
                {
                    Page initialPage = pages.get(pageId);
                    initialPage.addLast(r);
                    this.insertPointer(index, new ArrayList<>(Arrays.asList(pageId, initialPage.getIndexOf(r))));
                    handlePageSplitting(initialPage);
                }
            }
        }
    }

    public void handlePageSplitting(Page initialPage){
        // split the initial page
        Page newPageFromSplit = initialPage.split(Node.getPageId());
        pages.put(newPageFromSplit.getId(), newPageFromSplit);

        // keep a log of the records affected by the pages spitting
        for (int i = 0; i < newPageFromSplit.records.size(); i++) {
            Object key = newPageFromSplit.getRecords().get(i).getPrimaryObject();
            affectedBySplit.put(key, new ArrayList<>(Arrays.asList(newPageFromSplit.getId(), i)));
        }

        // now that the page has been split, we have to update the pointers affected by split
        updatePointersAffectedByPageSplit();
    }

    public void updatePointersAffectedByPageSplit(){
        if(affectedBySplit.size() == 0){
            return;
        }
        for(int keyId = 0; keyId < this.getNumOfKeys(); keyId++) {
            Object key = this.getKey(keyId);
            // check if this key has been affected
            if(affectedBySplit.containsKey(key)){
                if(pointers.size() - 1 >= keyId){
                    this.pointers.set(keyId, new ArrayList<>(affectedBySplit.get(key)));
                }
                else{ // if it didn't exist create it
                    this.pointers.add(keyId, affectedBySplit.get(key));
                }

                affectedBySplit.remove(key);
            }
        }
    }

    public boolean attemptToInsertToPage(Record r, int pageId, int index){
        Page page = pages.get(pageId);
        // see if this record can fit in this page -before all existing records
        if((page.getSize() + r.getSize()) <= Page.getCapacity()){
            // if the record fits we insert it
            int recordIndex = page.getRecords().size();
            page.addLast(r);

            insertPointer(index, new ArrayList<>(Arrays.asList(pageId, recordIndex)));

            return true;
        }
        return false;
    }

    public void insertChildNode(Node node){

        // get the key reference of node
        Object key = node.getKey(0);
        boolean containsKey = false;

        // calculate the index where this node would go
        int keyIndex = -1;

        for(int i = 0; i < values.size(); i++){
            Node temp = values.get(i);
            if(Type.compareObjects(key, temp.getKey(0), primaryAttribute.getType() )== -1){
                keyIndex = i;
                break;
            }
        }
        if(keyIndex == -1){
            keyIndex = values.size();
        }

        // insert the new node
        int nodeIndex = values.size(); // this is the index where the node obj will be
        this.values.add(node);

        // create a pointer
        ArrayList<Integer> pointer = new ArrayList<>(Arrays.asList(nodeIndex, -1)); // -1 means its internal :)))
        this.insertPointer(keyIndex, pointer);

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
            this.max = N - 1;
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
//                    numOfPointers--;
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

    public void addNode(Node node){
        this.values.add(node);
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

//            n1.numOfPointers += n2.pointers.size();

            n1.next = n2.next;

            n2.parent = null;
            n2.prev = null;
            n2.next = null;
        }

        return null;
    }

    public boolean borrow(Node left, Node right) {
        // TODO: Borrow 
        
        if(left.getType() == NodeType.LEAF && right.getType() == NodeType.LEAF){
            if(left.getKeys().size() > left.min){
                Object nodeBorrow = left.getKey(left.keys.size() -1);
                left.deleteKey(left.keys.size() -1);
                this.insertKey(0, nodeBorrow);

                return true;
            }
            else if(right.getKeys().size() > right.min){
                Object nodeBorrow = right.getKey(0);
                right.deleteKey(0);
                this.insertKey(keys.size(), nodeBorrow);

                return true;
            }
        }

        return false;
    }

    /**
     * checks if the node is full
     * true if number of nodes exceeds max
     * @return boolean
     */
    public boolean isFull() {
        return this.getNumOfKeys() == max;
    }

    /**
     * checks if the node is overflown
     * true if number of nodes exceeds max
     * @return boolean
     */
    public boolean isOverfull() {
        return this.getNumOfKeys() > max;
    }

    public boolean canBorrowLeft() {
        return prev.getNumOfKeys() - 1 >= prev.min;
    }

    public boolean canBorrowRight() {
        return next.getNumOfKeys() - 1 >= next.min;
    }

    public boolean canMergeLeft() {
        return (prev.getNumOfKeys() + this.getNumOfKeys() - 1) <= this.max;
    }

    public boolean canMergeRight() {
        return (next.getNumOfKeys() + this.getNumOfKeys() - 1) <= this.max;
    }

    public ArrayList<Object> getKeys() {
        return keys;
    }

    public ArrayList<Node> getValues() {
        return values;
    }

    public static HashMap<Integer, Page> getPages() {
        return pages;
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
        updatePointersAffectedByPageSplit();
        return pointers;
    }

    public int getNumOfPointers(){
        return this.pointers.size();
    }

    public int getNumOfKeys(){
        return this.keys.size();
    }

    public ArrayList<Integer> getPointerByIdx(int index){
        return getPointers().get(index);
    }

    public ArrayList<Object> splitKeys(int midPoint){
        ArrayList<Object> newKeys = new ArrayList<>();

        for(int i = midPoint - 1; i < this.N; i++){
            Object key = this.keys.remove(midPoint - 1);
            newKeys.add(key);
        }

        return newKeys;
    }


    public ArrayList<ArrayList<Integer>> splitPointers(int midPoint){
        ArrayList<ArrayList<Integer>> newPointer = new ArrayList<>();

        for(int i = midPoint - 1; i < this.N; i++){
            ArrayList<Integer> pointer = this.pointers.remove(midPoint - 1);
            newPointer.add(pointer);
        }

        return newPointer;
    }

    public Node getNodebyPointer(ArrayList<Integer> pointer){
        int idx = pointer.get(0);
        return values.get(idx);
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


    public static void setPageId(int pageId) {
        Node.pageId = pageId;
    }

    public static int getPageId() {
        pageId++;
        return pageId;
    }

    @Override
    public int compareTo(Node o) {
        return 0;
    }

    public NodeType getType() {
        return type;
    }

    @Override
    public String toString() {
        String s = "";

        if(this.type == NodeType.LEAF){
            s += "[" + getKey(0) + "-" + getKey(keys.size() - 1)  + "]" + " Leaf {";
            int i = 0;
            for(ArrayList<Integer> pointer: getPointers()){
                s += "[" + pointer.get(0) + ", " + pointer.get(1) + "]";
            }
            s += "}\n";
        }
        else{
            s += "Internal {";
            String nodess = "";
            for(int i = 0; i < keys.size(); i++){
                Object key = getKey(i);
                s += (key.toString());

                if(i != keys.size() - 1){
                    s += ",";
                }
            }

            for(Node node: this.values){
                nodess += "\t" + node.toString();
            }
            s += "}\n" + nodess;
        }

        return s ;
    }
}
