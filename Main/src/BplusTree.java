import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.DoubleAdder;

public class BplusTree {

    /**
     * the nodes branching factor
     * calculated based on the page size
     */
    private int N;

    /**
     * the page size
     */
    private int pageSize;

    /**
     * the root of this tree
     */
    private Node root;

    /**
     * The number of total pages in this tree
     */
    private static int pages;

    /**
     * The total number of records in this tree
     */

    /**
     * the table schema that this tree is indexing
     */
    private final TableSchema tableSchema;

    /**
     * constructs a b+ tree based on the data provided
     * @param pageSize the size of the page in bytes
     * @param tableSchema the table schema for which this tree is indexing pages
     */
    public BplusTree(int pageSize, TableSchema tableSchema){
        this.pageSize = pageSize;
        this.tableSchema = tableSchema;
        this.N = calculateN();
        Page.setCapacity(pageSize);
        pages = 0;
        Node.setPageId(0);
    }

    /**
     * generates page ids for pages that have not been created
     * @return int id of a new page
     */
    public static int getId(){
        pages ++;
        return pages;
    }

    public int getPageNumber(Node leafNode, int index)
    {
        //TODO: get it working
        return 0;
    }

    public void insert(Record record){
        // Case 1: the tree is empty
        // create a new leaf node that is also the root
        // insert the record into that node
        // add node to tree and increment pointer count
        if(isEmpty()){
            Node node = new Node(this.N, tableSchema, null, Node.NodeType.LEAF, record);
            this.root = node;
            return;
        }

        // Case 2: the tree is not empty
        // find the leaf node where this record would go
        // try to insert the record in there
        Object newKey = record.getPrimaryObject();
        Node leafNode = findLeafNode(root, newKey);

        // get the index where this record should go based on the primary key
        int index = getInsertIndex(leafNode, newKey);
        boolean insertionRes = leafNode.insert(record, index);

        if(!insertionRes){ // could not be inserted because it is full

            // insert the new key and the new record pointer
            leafNode.forceInsert(record, index);

            // split the node
            int midPoint = Math.ceilDiv(N, 2);

            ArrayList<Object> newKeys = leafNode.splitKeys(midPoint); // include midpoint
            ArrayList<ArrayList<Integer>> newPointers = leafNode.splitPointers(midPoint);

            // TODO set the pointers for the new node
            // let k be the least key value on the new nodes keys
            // this is the new parent key
            Object k = newKeys.get(0);

            // validate parent node
            if(leafNode.parent == null){// there is no parent bc this node was the root
                // if the parent does not exist create it
                Node parent = new Node(N, tableSchema, null, Node.NodeType.INTERNAL);
                parent.insertKey(0, k);
                leafNode.parent = parent;
                this.root = parent;

                // current node goes at index 0
                parent.insertChildNode(leafNode);

                // remove the root status
                leafNode.setRoot(false);
            }
            else{
                // get the index where the new key would go in the parent
                int newKeyIndex = getInsertIndex(leafNode.parent, k);
                // insert the new key into the parent
                leafNode.parent.insertKey(newKeyIndex, k);
            }

            // make a new node with the second half of the values
            Node newNode = new Node(N, tableSchema, leafNode.parent, Node.NodeType.LEAF);
            // set the keys of the new node
            newNode.setKeys(newKeys);
            newNode.setPointers(newPointers);

            // for setting the nodes: a leaf does not have nodes (children)

            // make the parent point to this node
            leafNode.parent.insertChildNode(newNode);

            // update the next pointer of the new Node, to what the og node's next
            newNode.setNext(leafNode.getNextNode());
            // if the next node exists, set it's previous to this new node
            if(newNode.getNextNode() != null){
                newNode.getNextNode().setPrev(newNode);
            }

            // now the next pointer of the og node is the new node
            leafNode.setNext(newNode);
            // set the left pointer of the new node to the og
            newNode.setPrev(leafNode);

            // check if after splitting the parent (internal node) overflows
            Node internal = leafNode.parent;
            while(internal != null){
                if(internal.isOverfull()){
                    splitInternalNode(internal);
                }
                else{
                    break;
                }
                internal = internal.parent;
            }
        }

    }

    /**
     * splits an internal node
     * pre: assume that the node that caused the split is already inserted
     *
     * @param internal
     */
    public void splitInternalNode(Node internal){
        Node parent = internal.parent;

        int midPoint = Math.ceilDiv(this.N, 2);

        // split and include the midpoint but then remove it
        ArrayList<Object> newKeys = internal.splitKeys(midPoint);
        ArrayList<ArrayList<Integer>> newPointers = internal.splitPointers(midPoint);
        ArrayList<Node> nodes = internal.getValues();

        // rmeove the midKey and mid pointer
        Object newParentKey = newKeys.remove(0);
        ArrayList<Integer> pointer = newPointers.remove(0);

        // create the sibling node
        Node sibling = new Node(N, tableSchema, internal.parent, Node.NodeType.INTERNAL);

        sibling.setKeys(newKeys);
        sibling.setPointers(newPointers);

        // reposition the next node's previous pointer
        for(ArrayList<Integer> p: newPointers){
            Node node = nodes.get(p.get(0));
            node.parent = sibling;
            sibling.addNode(node);
        }

        // reposition the right pointer
        sibling.setNext(internal.getNextNode());

        // reposition the next node's previous pointer
        if(sibling.getNextNode() != null){
            sibling.getNextNode().setPrev(sibling);
        }

        // make old node point to this new one
        internal.setNext(sibling);

        // set old one as previous node of siblinc
        sibling.setPrev(internal);

        // is this the roo?
        if(parent == null || internal.isRoot){
            // if the parent does not exist create it
            Node newRoot = new Node(N, tableSchema, null, Node.NodeType.INTERNAL);
            newRoot.insertKey(0, newParentKey);
            internal.parent = parent;
            sibling.parent = parent;

            // set new node as root
            this.root = newRoot;

            // current node goes at index 0
            newRoot.insertChildNode(internal);
            newRoot.insertChildNode(sibling);

            // remove the root status
            internal.setRoot(false);
        }
        else{
            // get the index where the new key would go in the parent
            int newKeyIndex = getInsertIndex(parent, newParentKey);
            parent.insertKey(newKeyIndex, newKeyIndex);
            parent.insertChildNode(sibling);
            sibling.parent = parent;
        }

    }

    public int getInsertIndex(Node node, Object newKey){
        // get the index where this would go - assume keys are in order
        for(int i = 0; i < node.getKeys().size(); i++){
            Object currentKey = node.getKey(i);
            // if new key is less than current, insert at location
            if(Type.compareObjects(newKey, currentKey, tableSchema.getPrimaryAttribute().getType()) < 0){
                return i;
            }
        }

        // if no location found, append to the end
        return node.getKeys().size();
    }

    public void delete(String key) throws TableException {
        // Case 1: There are no records in the tree.
        // Throw error
        if (isEmpty()) {
            throw new TableException(13,"");
        }
        // Case 2: Find where key is in tree
        // Delete it.
        Node leafNode = findLeafNode(key);
        boolean deletionRex = leafNode.delete(key);
        if(!deletionRex){ // could not be deleted because it is underful
            // get the index where this record should go based on the primary key
            int index = getDeleteIndex(leafNode, key);

            // get the new Record pointer
            ArrayList<Integer> recordPointer = new ArrayList<>(Arrays.asList(0,0));

            // insert the new key and the new record pointer
            leafNode.deleteKey(key);
            leafNode.deletePointer(index);

            // check if you can borrow
            if (leafNode.canBorrowLeft()) {
                Node left = leafNode.getPrev();
                Object leftKey = left.getKey(-1);
                Object leftValue = left.getValue(String.valueOf(leftKey));
                Page page = (Page) leftValue;
                int recordIndex = left.getIndexValue(String.valueOf(leftKey));
                Record record = page.getRecords().get(recordIndex);
                ArrayList<Integer> leftPointer = left.getPointers().get(-1);
                left.delete(String.valueOf(left.getKey(-1)));
                leafNode.insert(record, recordIndex);
            }
            else if (leafNode.canBorrowRight()) {
                Node right = leafNode.getNextNode();
                Object rightKey = right.getKey(0);
                Object rightValue = right.getValue(String.valueOf(rightKey));
                Page page = (Page) rightValue;
                int recordIndex = right.getIndexValue(String.valueOf(rightKey));
                Record record = page.getRecords().get(recordIndex);
                ArrayList<Integer> rightPointer = right.getPointers().get(0);
                right.delete(String.valueOf(right.getKey(0)));
                leafNode.insert(record, recordIndex);
            }

            // if you cant borrow merge
            else if (leafNode.canMergeLeft()){
                Node left = leafNode.getPrev();
                ArrayList<Object> leftKeys = left.getKeys();
                ArrayList<Node> leftValues = left.getValues();
                ArrayList<ArrayList<Integer>> leftPointers = left.getPointers();

                leafNode.setPrev(left.getPrev());
                // Can't think right now :P
            }
            else if (leafNode.canMergeRight()) {
                Node right = leafNode.getNextNode();

            }
        }
    }

    private int getDeleteIndex(Node leafNode, String key) {
        // TODO: I have no idea if this works but its
        //  finneeeee we're just stubbing this out for now

        // get the index where this would go - assume keys are in order
        int location = -1;
        for(int i = 0; i < leafNode.getKeys().size(); i++){
            Object currentKey = leafNode.getKey(i);
            // if new key is less than current, insert at location
            if(Type.compareObjects(key, currentKey, tableSchema.getPrimaryAttribute().getType()) < 0){
                location = i;
                break;
            }
        }

        // if no location found, append to the end
        location = leafNode.getKeys().size();

        return location;
    }

    public boolean isEmpty(){
        return root == null;
    }

    /**
     * Calculates the N value using the algorithm from the write-up
     * @return N value
     */
    private int calculateN(){
        Attribute attribute = tableSchema.getPrimaryAttribute();
        int size = 0;

        switch (attribute.getType()){
            case INTEGER -> size = Integer.BYTES;
            case DOUBLE -> size = Double.BYTES;
            case BOOLEAN -> size = 1;
            case VARCHAR, CHAR -> size = Character.BYTES * attribute.getN();
        }

        int pairSize = (size + 4);
        int pairs = Math.floorDiv(pageSize, pairSize);
        return pairs - 1;
    }

    private Node findLeafNode(Node node, Object key) {

        if(node.getType() == Node.NodeType.LEAF){
            return node;
        }

        int i;
        for (i = 0; i < node.getKeys().size(); i++) {
            if (Type.compareObjects(key, node.getKey(i), tableSchema.getPrimaryAttribute().getType()) < 0) {
                break;
            }
        }
        ArrayList<Integer> pointer;
        // if we're at the end, return the last pointer
        if(i == node.getKeys().size()){
            pointer = node.getPointerByIdx(node.getPointers().size() - 1);
        }
        else{
            // get the pointer at index i
            pointer = node.getPointerByIdx(i);
        }


        Node childNode = node.getNodebyPointer(pointer);
        if (childNode.getType() == Node.NodeType.LEAF) {
            return childNode;
        } else {
            return findLeafNode(node, key);
        }
    }

    public Node findLeafNode(Object key){
         // TODO @ Hai-Yen
        Node C = root;
        Object currentKey = root.getKey(0);
        int i = 0;
        while(C.getType() != Node.NodeType.LEAF){
            currentKey = root.getKey(i);

            if(i >= C.getKeys().size()){ //reach to the end of the keys
                ArrayList<Integer> pointer = C.getPointerByIdx(C.getKeys().size());
                C = C.getNodebyPointer(pointer);
                break;
            }
            else if(Type.compareObjects(key, currentKey, tableSchema.getPrimaryAttribute().getType()) == 0){
                ArrayList<Integer> pointer = C.getPointerByIdx(i+1);
                C = C.getNodebyPointer(pointer);
            }
            else{
                ArrayList<Integer> pointer = C.getPointerByIdx(i);
                C = C.getNodebyPointer(pointer);
            }
            i++;
        }
        if(Type.compareObjects(key, currentKey, tableSchema.getPrimaryAttribute().getType()) == 0){
            ArrayList<Integer> pointer = C.getPointerByIdx(i);
            C = C.getNodebyPointer(pointer);
        }

        return C;
    }

    public ArrayList<Integer> findNodePointer(Object key){
        // TODO @ Hai-Yen
        Node C = root;
        Object currentKey = null;
        int i = 0;
        while(C.getType() != Node.NodeType.LEAF){
            currentKey = root.getKey(i);

            if(i >= C.getKeys().size()){ //reach to the end of the keys
                ArrayList<Integer> pointer = C.getPointerByIdx(C.getKeys().size());
                C = C.getNodebyPointer(pointer);
                break;
            }
            else if(Type.compareObjects(key, currentKey, tableSchema.getPrimaryAttribute().getType()) == 0){
                ArrayList<Integer> pointer = C.getPointerByIdx(i+1);
                C = C.getNodebyPointer(pointer);
            }
            else{
                ArrayList<Integer> pointer = C.getPointerByIdx(i);
                C = C.getNodebyPointer(pointer);
            }
            i++;
        }
        if(Type.compareObjects(key, currentKey, tableSchema.getPrimaryAttribute().getType()) == 0){
            return C.getPointerByIdx(i);
        }


        return null;
    }

    @Override
    public String toString() {
        String s = "";
        s += ("N: " + this.N);
        s += ("\nroot: " + this.root.toString());

        for(int id: Node.getPages().keySet()){
            s += Node.getPages().get(id);
        }
        return s;
    }

    public static void main(String[] args) {
        Attribute a1 = new Attribute("num", Type.INTEGER, true, true, true, 0);
        Attribute a2 = new Attribute("valid", Type.BOOLEAN, false, true, false, 0);
        ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(new Attribute[]{a1, a2}));
        TableSchema table = new TableSchema("foo", attributes);
        BplusTree tree = new BplusTree(70, table);
        int[] nums = new int[]{21,42,11,44,1,13,27,52,81,9,7,10,17,25,45};
        for (int i = 0; i < nums.length; i++) {
            Record r1 = new Record(new ArrayList<>(Arrays.asList(""+nums[i], "false")), attributes);
            if(nums[i] == 45){
                System.out.println();

            }
            tree.insert(r1);
        }

        System.out.println(tree.toString());

    }

    public Record getRecord(String key) throws TableException {
        Node node = findLeafNode(root, key);
        Object object = node.getValue(key);
        if (object == null) {
            throw new TableException(14, "");
        }
        Page p = (Page) object;
        ArrayList<Record> records = p.getRecords();
        int index = node.getIndexValue(key);
        Record record = records.get(node.getPointers().get(index).get(1));
        return record;
    }
}
