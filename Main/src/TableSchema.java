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

    public TableSchema(String name, ArrayList<Attribute> attributes){
        LASTTABLEID++;
        this.tableID = LASTTABLEID;
        this.name = name;
        this.attributes = attributes;
        this.pageIds = new ArrayList<>();
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

    public int getTableID() {
        return tableID;
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


    public void addPageID(int ID){
        this.pageIds.add(ID);
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
        return pageIds;
    }


    /**
     * returns index of attribute based on the name
     * @param name the name of the attribute
     * @return the attribute if exists; null otherwise
     */
    public int getAttributeIndex(String name) throws TableException {
        for(int i = 0; i < attributes.size(); i++){
            Attribute a = attributes.get(i);
            if(a.getName().equals(name)){
                return i;
            }
        }

        // attribute not found, invalid
        throw new TableException(1, name);
    }

    public int getPageOrder(int pageId){
        for (int i = 0; i < this.pageIds.size(); i++) {
            if(this.pageIds.get(i) == pageId){
                return i + 1;
            }
        }
        return 1;
    }

    /**
     * returns an attribute based on the name
     * @param name the name of the attribute
     * @return the attribute if exists; null otherwise
     */
    public Attribute getAttribute(String name) throws TableException {
        for(Attribute a: attributes){
            if(a.getName().equals(name)){
                return a;
            }
        }
        throw new TableException(1, "name");
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * builds the table schema as a string
     * * @return schema as a string
     */
    public String displayTableSchema(){
        String str = "Table Name: " + this.name + "\n" + "Table Schema: \n";
        for (Attribute a : attributes) {
            str += "\t" + a + "\n";
        }

        str += "Pages: " + pageIds.size();
        return str;
    }


    //////// CATALOG SERIALIZATION CODE ////////////
    /**
     * converts the entire table into Bytes for the catalog to use
     *
     * @return byte[]
     */
    public byte[] convertTableObjectToBytes()
    {
        byte[] bb = new byte[0];

        bb = Type.concat(bb, getTableHeaderInfoForCatalog());
        bb = Type.concat(bb, convertAllAttributestoBytes());

        return bb;
    }

    /**
     * Turns all of the table's attributes into a byte array
     * @return byte[]
     */
    public byte[] convertAllAttributestoBytes()
    {
        byte[] bb = new byte[0];

        for (Attribute attr : this.attributes)
        {
            bb = Type.concat(bb, convertAttributeToBytes(attr));
        }

        return bb;
    }

    /**
     * Turns a single attribute into bytes
     *
     * @param attr - the attribute we are converting
     * @return byte[]
     */
    public byte[] convertAttributeToBytes(Attribute attr)
    {
        byte[] bb = new byte[0];

        int attributeNameLength = attr.getName().length();
        String attributeName = attr.getName();
        int attributeType;
        int attributeN = 0;

        switch (attr.getType()) {
            case BOOLEAN:
                attributeType = Catalog.BOOLEAN;
                break;
            case CHAR:
                attributeType = Catalog.CHAR;
                attributeN = attr.getN();
                break;
            case DOUBLE:
                attributeType = Catalog.DOUBLE;
                break;
            case INTEGER:
                attributeType = Catalog.INTEGER;
                break;
            case VARCHAR:
                attributeType = Catalog.VARCHAR;
                attributeN = attr.getN();
                break;
            default:
                attributeType = 0;
                break;
        }

        boolean isPrimaryKey = attr.isIsPrimaryKey();
        boolean isNotNull = attr.getNotNull();
        boolean isUnique = attr.getUnique();
        

        bb = Type.concat(bb, Type.convertIntToByteArray(attributeNameLength));
        bb = Type.concat(bb, Type.convertStringToByteArray(attributeName));
        bb = Type.concat(bb, Type.convertIntToByteArray(attributeType));
        bb = Type.concat(bb, Type.convertIntToByteArray(attributeN));
        bb = Type.concat(bb, Type.convertBooleanToByteArray(isPrimaryKey));
        bb = Type.concat(bb, Type.convertBooleanToByteArray(isNotNull));
        bb = Type.concat(bb, Type.convertBooleanToByteArray(isUnique));

        return bb;
    }

    /**
     * Gets the length of the table name, the table name, and the number of attributes
     * associated with the table
     * @return
     */
    public byte[] getTableHeaderInfoForCatalog()
    {
        byte[] bb = new byte[0];

        int tableNameLength = this.name.length();
        int numOfAttributes = this.attributes.size();

        bb = Type.concat(bb, Type.convertIntToByteArray(this.tableID));
        bb = Type.concat(bb, Type.convertIntToByteArray(tableNameLength));
        bb = Type.concat(bb, Type.convertStringToByteArray(this.name));
        bb = Type.concat(bb, Type.convertIntToByteArray(pageIds.size()));

        for (Integer i : pageIds)
        {
            bb = Type.concat(bb, Type.convertIntToByteArray(i));
        }

        bb = Type.concat(bb, Type.convertIntToByteArray(numOfAttributes));

        return bb;
    }

    @Override
    public boolean equals(Object obj) {
        boolean res = false;
        if(obj instanceof TableSchema){
            TableSchema other = (TableSchema) obj;
            res = other.getName().equals(this.name) && other.tableID == this.tableID;
        }

        return res;
    }

    @Override
    public String toString() {
        return tableID + ": " + name;
    }
}
