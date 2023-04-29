import java.util.ArrayList;
import java.util.Arrays;

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
        pages = 0;
    }

    /**
     * generates page ids for pages that have not been created
     * @return int id of a new page
     */
    public static int getId(){
        pages ++;
        return pages;
    }

    public void insert(Record record){
        // TODO: @Newcarlis

        // Case 1: the tree is empty
        // create a new leaf node that is also the root
        // insert the record into that node
        // add node to tree and increment pointer count
        if(isEmpty()){
            Node node = new Node(this.N, tableSchema. getPrimaryAttribute(), null, Node.NodeType.LEAF, record);
            this.root = node;
        }

        // Case 2: the tree is not empty
        // find the leaf node where this record would go
        // try to insert the record in there
        Object newKey = record.getPrimaryObject();
        Node leafNode = findLeafNode(newKey);
        boolean insertionRes = leafNode.insert(record);
        if(!insertionRes){ // could not be inserted because it is full
            // get the index where this record should go based on the primary key
            int index = getInsertIndex(leafNode, newKey);

            // get the new Record pointer
            ArrayList<Integer> recordPointer = getPageAndIndex();

            // insert the new key and the new record pointer
            leafNode.insertKey(index, newKey);
            leafNode.insertPointer(index, recordPointer);

            // split the node
            int midPoint = Math.ceilDiv(N, 2);

            ArrayList<Object> newKeys = leafNode.splitKeys(midPoint);
            ArrayList<ArrayList<Integer>> newPointers = leafNode.splitPointers(midPoint);

            // make a new node with the remaining values - P
            Node newNode = new Node(N, tableSchema.getPrimaryAttribute(), leafNode.parent, Node.NodeType.LEAF, record);
            // let k be the least key value on the new node
            Object k = new newNode.getKey(0);


            // make a pointer in the parent node that points to this
            leafNode.parent.insertChildNode(newNode);
        }

    }

    public int getInsertIndex(Node leafNode, Object newKey){
        // get the index where this would go - assume keys are in order
        int location = -1;
        for(int i = 0; i < leafNode.numOfPointers; i++){
            Object currentKey = leafNode.getKey(i);
            // if new key is less than current, insert at location
            if(compareKeys(newKey, currentKey) < 0){
                location = i;
                break;
            }
        }

        // if no location found, append to the end
        location = leafNode.numOfPointers;

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

    public Node findLeafNode(Object key){
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
            else if(compareKeys(key, currentKey) == 0){
                ArrayList<Integer> pointer = C.getPointerByIdx(i+1);
                C = C.getNodebyPointer(pointer);
            }
            else{
                ArrayList<Integer> pointer = C.getPointerByIdx(i);
                C = C.getNodebyPointer(pointer);
            }
            i++;
        }
        if(compareKeys(key, currentKey) == 0){
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
            else if(compareKeys(key, currentKey) == 0){
                ArrayList<Integer> pointer = C.getPointerByIdx(i+1);
                C = C.getNodebyPointer(pointer);
            }
            else{
                ArrayList<Integer> pointer = C.getPointerByIdx(i);
                C = C.getNodebyPointer(pointer);
            }
            i++;
        }
        if(compareKeys(key, currentKey) == 0){
            return C.getPointerByIdx(i);
        }


        return null; 
    }

    public ArrayList<Integer> getPageAndIndex(){
        return null;
    }

    public int compareKeys(Object val1, Object val2) {
        Type type = tableSchema.getPrimaryAttribute().getType();
        if(type == Type.BOOLEAN){
            Boolean b1 = (Boolean) val1;
            Boolean b2 = (Boolean) val2;
            return Boolean.compare(b1, b2);
        }
        else if(type == Type.INTEGER){
            Integer int1 = (Integer) val1;
            Integer int2 = (Integer) val2;
            return Integer.compare(int1, int2);
        }
        else if((type == Type.VARCHAR) || type == Type.CHAR){
            String str1 = val1.toString();
            String str2 = val2.toString();
            return str1.compareTo(str2);
        }
        else if(type == Type.DOUBLE){
            Double do1 = (Double) val1;
            Double do2 = (Double) val2;
            return Double.compare(do1, do2);
        }

        return 0;
    }

    public void printTreeInfo(){
        String s = "N: " + N;
        System.out.println(s);
    }

    public static void main(String[] args) {
        Attribute a1 = new Attribute("num", Type.INTEGER, true, true, true, 0);
        Attribute a2 = new Attribute("valid", Type.BOOLEAN, false, true, false, 0);
        ArrayList<Attribute> attributes = new ArrayList<>(Arrays.asList(new Attribute[]{a1, a2}));
        TableSchema table = new TableSchema("foo", attributes);
        Record r1 = new Record(new ArrayList<>(Arrays.asList(new String[]{"1", "false"})), attributes);
        BplusTree tree = new BplusTree(50, table);
        tree.insert(r1);
        tree.printTreeInfo();

    }
}
