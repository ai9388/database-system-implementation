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

    public void insertRecord(Object pk, Record record){
        // TODO: @Newcarlis


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

    public void printTreeInfo(){
        String s = "N: " + N;
        System.out.println(s);
    }
}
