import java.util.*;

public class Table {
    /**
     * the table name
     */
    private String name;
    /**
     * unique table id assigned to table
     */
    private int tableID;
    /*
     * general id that serves as the offset for the table id
     */
    private static int GENERALTABLEID;
    /**
     * the attributes for all the columns in this table
     */
    private ArrayList<Attribute> attributes;
    /**
     * collection of all the records in this table
     */
    private ArrayList<Record> records;
    /**
     * object attribute for the primary key of this table
     */
    private Attribute primaryAttribute;
    /**
     * index of the primary attribute in the columns
     */
    private int primaryIndex;
    /**
     * maps the primary key of a record to a record object
     */
    private HashMap<Object, Record> recordsByPK;
    /**
     * maps the name of a column to its attribute object
     */
    private HashMap<String, Attribute> attributesByCol;
    /**
     * all the pages containing the records for this table
     */
    private ArrayList<Page> pages;

    private Page mostRecentPage;

    public Table(String name, ArrayList<Attribute> attributes) {
        this.name = name;
        this.tableID = GENERALTABLEID + 1;
        this.attributes = attributes;
        this.records = new ArrayList<>();
        this.recordsByPK = new HashMap<>();
        pages = new ArrayList<>();

        // set the primary attribute and primary index
        for (int i = 0; i < attributes.size(); i++) {
            Attribute a = attributes.get(i);
            if (a.isIsPrimaryKey()) {
                primaryAttribute = a;
                primaryIndex = i;
            }
        }
        setAttributesByCol();
    }

    /**
     * sets the static offset for table id's
     * table id's start at 1000
     * 
     * @param id the table number which indicates how many tables exists
     */
    public static void setGeneralTableID(int id) {
        GENERALTABLEID = 1000 + id;
    }

    /**
     * @return String return the table name
     */
    public String getName() {
        return name;
    }

    /**
     * @return int return the tableID
     */
    public int getTableID() {
        return tableID;
    }

    public Page getMostRecentPage(){
        return mostRecentPage;
    }

    /**
     * sets the primary attribute
     * 
     * @param primaryAttribute Attribute object that represents primary col
     */
    public void setPrimaryAttribute(Attribute primaryAttribute) {
        this.primaryAttribute = primaryAttribute;
    }

    /**
     * @return ArrayList<Attribute> return the attributes
     */
    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
        setAttributesByCol();
    }

    /**
     * returns the attribute object that corresponds to a
     * specific column name
     */
    public void setAttributesByCol() {
        this.attributesByCol = new HashMap<>();
        for (Attribute attribute : this.attributes) {
            attributesByCol.put(attribute.getName(), attribute);
        }
    }

    /**
     * @return ArrayList<Record> return the records
     */
    public ArrayList<Record> getRecords() {
        return records;
    }

    /*
     * returns a re
     */
    public Record getRecordByPK(String pkValue) throws PrimaryKeyException {
        if (Type.validateType(pkValue, primaryAttribute)) {
            if (recordsByPK.containsKey(pkValue)) {
                return recordsByPK.get(pkValue);
            } else {
                // invalid pk value
                throw new PrimaryKeyException(4, pkValue);
            }
        } else {
            throw new PrimaryKeyException(5, new InvalidDataTypeException(pkValue, primaryAttribute).getMessage());
        }
    }

    /**
     * creates a record and inserts it into all table collections
     * as well as pages
     * 
     * @param values values of the record
     * @return true if record creation is successful
     * @throws InvalidDataTypeException
     * @throws PrimaryKeyException
     */
    public boolean insertRecord(String[] values) throws InvalidDataTypeException, PrimaryKeyException {
        if (Type.validateAll(values, attributes)) {
            Record record = new Record(new ArrayList<String>(Arrays.asList(values)), attributes);
            this.insertRecord(record);
            return true;
        } else {
            // creation of record failed
            throw new InvalidDataTypeException(values, attributes);
        }
    }

    /**
     * returns the index of a given record
     * the index is based on the order of insertion
     * @param record record to get the index
     * @return
     */
    public int getRecordIndex(Record record){
        for (int i = 0; i < records.size(); i++) {
            if(records.get(i).equals(record)){
                return i;
            }
        }
        // this should not happen
        return -1;
    }

    public boolean insertRecord(Record record) throws PrimaryKeyException {
                    
        // check if this primary key exists
        if(this.recordsByPK.containsKey(record.getPrimaryObject())){
            String rowOccupied = String.valueOf(getRecordIndex(record));
            throw new PrimaryKeyException(2, rowOccupied);
        }
        this.records.add(record);
        this.recordsByPK.put(record.getValueAtColumn(primaryIndex), record);
        // if there are no pages create one
        if (this.pages.size() == 0) {
            Page page = new Page(this.primaryIndex);
            this.pages.add(page);
        }
        addRecordToPage(record);
        return true;
    }

    /**
     * finds record based on primary key from all table collections
     * 
     * @param pkValue value of primary key
     * @return true if removal successful
     * @throws PrimaryKeyException
     * @throws InvalidDataTypeException
     */
    public boolean removeRecordByPK(String pkValue) throws PrimaryKeyException, InvalidDataTypeException {
        if (Type.validateType(pkValue, primaryAttribute)) {
            // convert the value to an object
            Object pkObject = Type.getObjFromType(pkValue, primaryAttribute.getType());

            // validate key value
            if (recordsByPK.containsKey(pkObject)) {

                recordsByPK.remove(pkObject);
                return true;
            } else {
                throw new PrimaryKeyException(4, pkValue);
            }
        } else {
            throw new PrimaryKeyException(5, new InvalidDataTypeException(pkValue, primaryAttribute).getMessage());
        }
    }

    /**
     * finds a record based on primary key and updates it
     * 
     * @param pkValue  value of primary key
     * @param column   column to update
     * @param newEntry new value to insert
     * @return true if update successful
     * @throws TableException
     * @throws PrimaryKeyException
     * @throws InvalidDataTypeException
     */
    public boolean updateRecordByPK(String pkValue, String column, String newEntry)
            throws TableException, PrimaryKeyException, InvalidDataTypeException {
        if (Type.validateType(pkValue, primaryAttribute)) {
            if (isValidColumn(column)) {
                Record r = getRecordByPK(pkValue);
                Attribute a = attributesByCol.get(column);
                // if both the column and the pk are valid, then validate data type
                if (Type.validateType(pkValue, a)) {
                    Object newEntryObject = Type.getObjFromType(pkValue, a.getType());
                    r.updateAtColumn(getColumnIndex(column), newEntryObject);
                } else {
                    throw new InvalidDataTypeException(newEntry, a);
                }
            } else {
                throw new TableException(1, column);
            }
        } else {
            throw new PrimaryKeyException(5, new InvalidDataTypeException(pkValue, primaryAttribute).getMessage());
        }
        return true;
    }

    /**
     * returns the index of a specific attribute
     * 
     * @param columnName column name of the attribute
     * @return the index of that attribute in table
     */
    private int getColumnIndex(String columnName) {
        int idx = 0;
        for (int i = 0; i < attributes.size(); i++) {
            Attribute a = attributes.get(i);
            if (a.getName().equalsIgnoreCase(columnName)) {
                idx = i;
                break;
            }
        }
        return idx;
    }

    /**
     * checks if the provided column name exists in this table
     * 
     * @param column the name of the column
     */
    public boolean isValidColumn(String column) {
        return attributesByCol.keySet().contains(column);
    }

    /**
     * counts how many records this table has
     * 
     * @return
     */
    public int getNumberOfRecords() {
        return records.size();
    }

    /**
     * returns the table as a string in a nice format
     * 
     * @return formatted table
     */
    public String formatResults(ArrayList<Attribute> columnAttr, ArrayList<Record> recordsToShow) {
        String format = "|";
        String result = "";
        int len = 1;
        String dash;
        Object[] headers = new Object[columnAttr.size()];
        for (int i = 0; i < columnAttr.size(); i++) {
            Attribute a = columnAttr.get(i);
            headers[i] = columnAttr.get(i).getName().toUpperCase();
            if (a.getType() == Type.VARCHAR || a.getType() == Type.CHAR) {
                int temp = Math.max(a.getName().length() + 2, a.getN() + 2);
                format += "%-" + temp + "s|";
                len += temp + 1;
            } else {
                int temp = (a.getName().length() + 2);
                format += "%-" + temp + "s|";
                len += temp + 1;
            }
        }
        // create dashed line
        dash = String.format("%0" + len + "d", 0).replace("0", "-");
        // add the header to result
        result = dash + "\n" + String.format(format, headers) + "\n" + dash;

        // specific columns from all the records
        for (Record r : recordsToShow) {
            ArrayList<Object> entries = new ArrayList<>();
            for (Attribute a : columnAttr) {
                entries.add(r.getValueAtColumn(getColumnIndex(a.getName())));
            }
            result += "\n" + String.format(format, entries.toArray());
        }

        // bottom line
        result += "\n" + dash;
        return result;
    }

    /**
     * returns a view of the table (schema)
     * 
     * @return string view
     */
    public String displayTableSchema() {
        String str = "Table Name: " + this.getName() + "\n" + "Table Schema: \n";
        for (Attribute a : attributes) {
            str += "\t" + a + "\n";
        }
        return str;
    }

    /**
     * returns table information
     * 
     * @return table info as string
     */
    public String displayTableInfo() {
        String str = displayTableSchema() +
                "Pages: " + this.pages.size() + "\n" +
                "Record: " + this.records.size();
        return str;
    }

    /**
     * selects infomation from the current table by columns
     * 
     * @param columns the column names
     * @return string formatted with the table info
     * @throws TableException if columns are invalid
     */
    public String select(String[] columns) throws TableException {
        ArrayList<Attribute> selectAttributes = new ArrayList<>();
        // validate all columns
        for (String c : columns) {
            isValidColumn(c);
            selectAttributes.add(attributesByCol.get(c));
        }

        return formatResults(selectAttributes, this.records);
    }

    /**
     * selects and returns all the records from this table
     * 
     * @return string records formatted
     */
    public String selectAll() {
        return formatResults(this.attributes, this.records);

    }

    /**
     * adds a given record to the pages of this table
     * 
     * @param r
     */
    public void addRecordToPage(Record r) {
        Page page;
        boolean inserted = false;

        for (int i = 0; i < pages.size(); i++) {
            page = pages.get(i);

            if (inserted) {
                break;
            }
            // does the record belong in this page?
            int index = page.addRecordInOrder(r);
            if (index > -1) {
                page.insertRecordAt(r, index);
                inserted = true;
            } else { // if this is the last page
                     // and the record was not found to be less than any item
                     // then it must be greater than all
                if (i == pages.size() - 1 && !inserted) {
                    page.addLast(r);
                    inserted = true;
                }

            }
            // if the addition overflows the page split
            if (page.overflow()) {
                pages.add(i + 1, page.split());
                inserted = true;
            }
            if(inserted){
                mostRecentPage = pages.get(i);
            }
        }
    }

    /**
     * remove a record from the page
     * @param r
     */
    public void removeRecordFromPage(Record r){
        for(Page page: this.pages){
            if(page.containsRecord(r)){
                page.removeRecord(r);
                mostRecentPage = page;
            }
        }
    }

    /*
     * returns a page based on its number
     */
    public Page getPageByPNum(int num) {
        return pages.get(num);
    }

    /*
     * returns all pages
     */
    public ArrayList<Page> getPages() {
        return this.pages;
    }

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

        int len = bb.length;
        
        bb = Type.concat(Type.convertIntToByteArray(len), bb);
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
    public byte[] convertAttributeToBytes(Attribute attr) {
        byte[] bb = new byte[0];

        int attributeNameLength = attr.getName().length();
        String attributeName = attr.getName();
        int attributeType;

        switch (attr.getType()) {
            case BOOLEAN:
                attributeType = Catalog.BOOLEAN;
                break;
            case CHAR:
                attributeType = Catalog.CHAR;
                break;
            case DOUBLE:
                attributeType = Catalog.DOUBLE;
                break;
            case INTEGER:
                attributeType = Catalog.INTEGER;
                break;
            case VARCHAR:
                attributeType = Catalog.VARCHAR;
                break;
            default:
                attributeType = 0;
                break;
        }

        boolean isPrimaryKey = attr.isIsPrimaryKey();

        bb = Type.concat(bb, Type.convertIntToByteArray(attributeNameLength));
        bb = Type.concat(bb, Type.convertStringToByteArray(attributeName));
        bb = Type.concat(bb, Type.convertIntToByteArray(attributeType));
        bb = Type.concat(bb, Type.convertBooleanToByteArray(isPrimaryKey));

        return bb;
    }

    /**
     * Gets the length of the table name, the table name, and the number of attributes
     * associated with the table
     * @param tableName - name of associated table
     * @return
     */
    public byte[] getTableHeaderInfoForCatalog() {
        byte[] bb = new byte[0];

        int tableNameLength = this.name.length();
        int numOfAttributes = this.attributes.size();

        bb = Type.concat(bb, Type.convertIntToByteArray(tableNameLength));
        bb = Type.concat(bb, Type.convertStringToByteArray(this.name));
        bb = Type.concat(bb, Type.convertIntToByteArray(numOfAttributes));

        return bb;
    }

}