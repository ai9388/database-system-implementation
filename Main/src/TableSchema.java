import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class that stores the schema of a database
 * also contains the page numbers of the pages
 * that store this tables data
 */
public class TableSchema {
    /**
     * constant used to Calculate tableID
     * // TODO: set from the catalog
     */
    private static int LASTTABLEID;

    /**
     * This table's name
     */
    private String name;

    /**
     * table's id
     */
    private int tableID;

    /**
     * Attributes used in table in order of creation
     */
    private ArrayList<Attribute> attributes = new ArrayList<>();

    /**
     * List of page numbers that contain records for this page
     */
    private ArrayList<Integer> pageIds;

    /**
     * The primary index for this table
     */
    private int primaryIndex;

    /**
     * constructor used to create a table that has no pages yet
     * For example, when a table is created for the first time
     * @param name table name
     * @param attributes the attributes that make up this table
     */

    public TableSchema(int id, String name, ArrayList<Attribute> attributes){
        this.tableID = id;
        this.name = name;
        this.attributes = attributes;
        setPrimaryIndex();
    }

    /**
     * constructor used to create a tableSchema for a table that already has pages
     * For example: when loading pages from memory
     * @param name
     * @param attributes
     * @param pageIds
     */
    public TableSchema(int id, String name, ArrayList<Attribute> attributes, Integer[] pageIds){
        this.tableID = id;
        this.name = name;
        this.attributes = attributes;
        this.pageIds = new ArrayList<>(Arrays.asList(pageIds));
        setPrimaryIndex();
    }

    /**
     * returns
     * @return
     */
    public int getNumberOfPages(){
        return this.pageIds.size();
    }

    public String getName(){
        return this.name;
    }

    /**
     * sets the last used ids for table
     * @param id the last id used (int)
     */
    public static void setLASTTABLEID(int id) {
        TableSchema.LASTTABLEID = id;
    }

    /**
     * sets the primary index of the table schema by reading
     * all the attributes
     */
    private void setPrimaryIndex() {
        for (int i = 0; i < attributes.size(); i++) {
            Attribute a = attributes.get(i);
            if(a.isIsPrimaryKey()){
                primaryIndex = i;
            }
        }
    }

    /**
     * returns the primary index of this table
     * @return
     */
    public int getPrimaryIndex() {
        return primaryIndex;
    }

    /**
     * returns all the page id's containing records from this table
     * @return
     */
    public ArrayList<Integer> getPageIds() {
        return getPageIds();
    }
}
