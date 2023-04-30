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
     * @param primaryAttribute
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
        this.saveRecordAndCreateRecordPointer(index, record);
        this.insertKey(index, record.getPrimaryObject());
        return true;
    }

    public void forceInsert(Record record, int index){
        // insert this record pointer
        this.saveRecordAndCreateRecordPointer(index, record);
        this.insertKey(index, record.getPrimaryObject());
    }

    /**
     * index where they key goes
     * @param key
     * @param index
     * @return
     */
    public boolean saveRecordAndCreateRecordPointer(int index, Record r){
        // try and determine the page number
        // case 1: key index is 0 and there are no pages
        if(index == 0 && pages.size() == 0){
            // create the first page
            Page page = new Page(0, this.tableName);
            pages.put(0, page);
            page.insertRecordAt(r, 0);
            // both the page number and record index are 0
            this.pointers.add(new ArrayList<>(Arrays.asList(0, 0)));
            return true;
        }

        // case 2: key index is 0 and there are some pages
        else if(index == 0 && pages.size() > 1){
            // Try to insert it in the page used by the same pointer
            int firstPageUsedInThisLeafNode = this.pointers.get(0).get(0);
            boolean res = attemptToInsertToPage(r, firstPageUsedInThisLeafNode);
            boolean res2 = false;

            if(!res){ // if it could not be inserted in the first page
                // try to insert in to the last page used by the leaf node before this one(if any)
                if(this.prev != null){
                    int lastPageUsedByLeftSibling = prev.getPointerByIdx(prev.numOfPointers - 1).get(0);
                    res2 = attemptToInsertToPage(r, lastPageUsedByLeftSibling);
                }
                if(res2){ // record was added
                    this.insertPointer(index, new ArrayList<>(Arrays.asList(pageId, index)));
                    return true;
                }
                else { // if the previous is null, or it exists but record doesn't fit
                    // force insert the record into the page and then cause a split
                    Page initialPage = pages.get(pageId);
                    initialPage.insertRecordAt(r, initialPage.getIndexOf(r));
                    this.insertPointer(index, new ArrayList<>(Arrays.asList(pageId, initialPage.getIndexOf(r))));
                    handlePageSplitting(initialPage);
                }

            }
            else{
                this.insertPointer(index, new ArrayList<>(Arrays.asList(pageId, index)));
                return true;
            }
        }

        // case 3: key index is last index and there are some pages
        else if (index == this.numOfPointers && pages.size() >= 1)
        {
            int lastPageUsedInThisLeafNode = this.pointers.get(this.pointers.size() - 1).get(0);
            boolean res = attemptToInsertToPage(r, lastPageUsedInThisLeafNode);
            boolean res2 = false;

            if (!res)
            { // if it could not be inserted in the last page
              // try to insert in to the next page used by the leaf node before this one(if any)
                if (this.next != null)
                {
                    int firstPageUsedByLeftSibling = next.getPointerByIdx(0).get(0);
                    res2 = attemptToInsertToPage(r, firstPageUsedByLeftSibling);
                }
                if (res2)
                { // record was added
                    this.insertPointer(index, new ArrayList<>(Arrays.asList(pageId, index)));
                    return true;
                } else
                { // if the next is null, or it exists but record doesn't fit
                  // force insert the record into the page and then cause a split
                    Page initialPage = pages.get(pageId);
                    initialPage.insertRecordAt(r, initialPage.getIndexOf(r));
                    this.insertPointer(index, new ArrayList<>(Arrays.asList(pageId, initialPage.getIndexOf(r))));
                    handlePageSplitting(initialPage);
                }
            } else
            {
                this.insertPointer(index, new ArrayList<>(Arrays.asList(pageId, index)));
                return true;
            }
        }

        // everything in between
        else
        {
            // attempting to insert at the given index
            int lastPageUsedInThisLeafNode = this.pointers.get(index).get(0);
            boolean res = attemptToInsertToPage(r, lastPageUsedInThisLeafNode);

            // if insertion did not work, we try again and subtract from the index
            if (!res)
            {
                lastPageUsedInThisLeafNode = this.pointers.get(index - 1).get(0);
                res = attemptToInsertToPage(r, lastPageUsedInThisLeafNode);

                // if it still doesnt insert, we must split
                if (!res)
                {
                    Page initialPage = pages.get(pageId);
                    initialPage.insertRecordAt(r, initialPage.getIndexOf(r));
                    this.insertPointer(index, new ArrayList<>(Arrays.asList(pageId, initialPage.getIndexOf(r))));
                    handlePageSplitting(initialPage);
                }
            } else
            {
                return true;
            }
        }
        // something failed along the way :(
        return false;
    }

    public void handlePageSplitting(Page initialPage){
        // split the initial page
        Page newPageFromSplit = initialPage.split(Node.getPageId());
        pages.put(newPageFromSplit.getId(), newPageFromSplit);

        // now that the page has been split, we have to update the pointers involving the two pages
        generateRecordPointersForPage(initialPage);
        generateRecordPointersForPage(newPageFromSplit);
    }

    public void generateRecordPointersForPage(Page page){
        for(int keyId = 0; keyId < this.numOfPointers; keyId++){
            Object key = this.getKey(keyId);

            for(int rId = 0; rId < page.getRecords().size(); rId++){
                Record r = page.getRecords().get(rId);
                if(Type.compareObjects(key, r.getPrimaryObject(), this.primaryAttribute.getType()) == 0){
                    // update the old pointer if it existed
                    if(pointers.size() - 1 >= keyId){
                        this.pointers.set(keyId, new ArrayList<>(Arrays.asList(page.getId(), rId)));
                    }
                    else{ // if it didn't exist create it
                        this.pointers.add(keyId, new ArrayList<>(Arrays.asList(page.getId(), rId)));
                    }
                }
            }
        }
    }

    public boolean attemptToInsertToPage(Record r, int pageId){
        Page page = pages.get(pageId);
        // see if this record can fit in this page -before all existing records
        if((page.getSize() + r.getSize()) <= Page.getCapacity()){
            // if the record fits we identify the index where it fits
            int recordIndex = page.getIndexOf(r);
            // add the record to the page
            page.insertRecordAt(r, recordIndex);
            return true;
        }
        return false;
    }

    public void insertChildNode(Node node){

        // get the key reference if node
        Object key = node.getKey(0);
        boolean containsKey = false;

        // callculate the index where this node woould go
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

        // insert this key at index
        if(!this.keys.contains(key)) {
            this.insertKey(keyIndex, key);
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
        this.numOfPointers++;
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
            s += "Leaf {";

            for(ArrayList<Integer> pointer: pointers){
                s += "[" + pointer.get(0) + ", " + pointer.get(1) + "]";
            }
            s += "}\n";
        }
        else{
            s += "Internal {";
            String nodess = "";
            for(int i = 0; i < keys.size(); i++){
                Object key = getKey(i);
                s += (key.toString() + ", ");
                nodess += (key.toString() + ": "  + values.get(pointers.get(i).get(0)).toString());
            }
            s += "}\n" + nodess;
        }

        return s ;
    }
}
