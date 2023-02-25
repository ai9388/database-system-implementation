import java.util.*;

public class Table{
    /**
     * the table name
     */
    private String name;
    /**
     * unique table id assigned to table
     */
    private int tableID;
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
    private HashMap<String, Record> recordsByPK;
    /**
     * maps the name of a column to its attribute object
     */
    private HashMap<String, Attribute> attributesByCol;

    ArrayList<Page> pages;

    public Table(String name, int tableID, ArrayList<Attribute> attributes, Attribute primaryAttribute, int primaryIndex) {
        this.name = name;
        this.tableID = tableID;
        this.attributes = attributes;
        this.records = new ArrayList<>();
        this.primaryAttribute = primaryAttribute;
        setAttributesByCol();
        this.primaryIndex = primaryIndex;
        this.recordsByPK = new HashMap<>();
        pages = new ArrayList<>();
    }

    /**
     * @return String return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return int return the tableID
     */
    public int getTableID() {
        return tableID;
    }

    /**
     * @param tableID the tableID to set
     */
    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    /**
     * sets the primary attribute
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
        for(Attribute attribute: this.attributes){
            attributesByCol.put(attribute.getName(), attribute);
        }
    }

    /**
     * @return ArrayList<Record> return the records
     */
    public ArrayList<Record> getRecords() {
        return records;
    }

    /**
     * @param records the records to set
     */
    public void setRecords(ArrayList<Record> records) {
        this.records = records;
    }

    /**
     * creates a record and inserts it into all table collections
     * @param values values of the record
     * @return true if record creation is successful
     */
    public boolean insertRecord(String[] values)
    {
        try {
            Type.validateAll(values, attributes);
            Record record = new Record(new ArrayList<String>(Arrays.asList(values)), attributes);
            this.insertRecord(record);
            return true;
        } catch (InvalidDataTypeException e) {
            // creation of record failed
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean insertRecord(Record record) {
        this.records.add(record);
        this.recordsByPK.put(primaryAttribute.getName(), record);
        // if there are no pages create one
        if(this.pages.size() == 0){
            Page page  = new Page(this.primaryIndex);
            addRecordToPage(record);
        }
        return true;
    }
    /**
     * finds record based on primary key from all table collections
     * @param pkValue value of primary key
     * @return true if removal successful
     */
    public boolean removeRecordByPK(String pkValue)
    {
        try {
            // if primary key is valid, remove from collection
            if(Type.validateType(pkValue, primaryAttribute)){
                recordsByPK.remove(pkValue);
            }
        } catch (InvalidDataTypeException e) {
            System.out.println(e.getMessage());
            return false;
        }
        
        return true;
    }

    /**
     * finds a record based on primary key and updates it
     * @param pkValue value of primary key
     * @param column column to update
     * @param newEntry new value to insert
     * @return true if update successful
     */
    public boolean updateRecordByPK(String pkValue, String column, String newEntry)
    {
        try {
            // TODO: primary key might not exist
            // if primary key is valid, update the record
            if(Type.validateType(pkValue, primaryAttribute) && isValidColumn(column)){
                 recordsByPK.get(pkValue).updateAtColumn(getColNum(column), newEntry);        
            }

        } catch (InvalidDataTypeException e) {
            System.out.println(e.getMessage());
            return false;
        }
        catch (TableException e){
            System.out.println(e.getMessage());
            return false;
        }

        // TODO: catch exception form inValidColumnName
        
        return true;
    }

    private int getColNum(String colName){
        int idx = 0;
        for (int i = 0; i < attributes.size(); i++) {
            Attribute a = attributes.get(i);
            if(a.getName().equalsIgnoreCase(colName)){
                idx = i;
                break;
            }
        }
        return idx;
    }

    /**
     * checks if the provided column name exists in this table
     * @param column the name of the column
     */
    public boolean isValidColumn(String column) throws TableException{
        boolean res = attributesByCol.keySet().contains(column);

        if(res){
            return res;
        }else{
            throw new TableException(1, column);
        }
    }

    public int getNumberOfRecords(){
        return records.size();
    }

    /**
     * returns the table as a string in a nice format
     * @return formatted table
     */
    public String formatResults(ArrayList<Attribute> columnAttr, ArrayList<Record> recordsToShow){
        String format = "|";
        String result = "";
        int len = 1;
        String dash;
        Object[] headers = new Object[columnAttr.size()];
        for(int i = 0; i < columnAttr.size(); i++){
            Attribute a = columnAttr.get(i);
            headers[i] = columnAttr.get(i).getName().toUpperCase();
            if(a.getType() == Type.VARCHAR || a.getType() == Type.CHAR){
                int temp = Math.max(a.getName().length() + 2, a.getN() + 2);
                format += "%-" + temp + "s|";
                len += temp + 1;
            }
            else{
                int temp = (a.getName().length() + 2) ;
                format += "%-" + temp + "s|";
                len += temp + 1;
            }
        }
        // create dashed line
        dash = String.format("%0" + len + "d", 0).replace("0", "-");
        // add the header to result
        result = dash + "\n" + String.format(format, headers) + "\n" + dash;

        // specific columns from all the records
        for(Record r: recordsToShow){
            ArrayList<Object> entries = new ArrayList<>();
            for(Attribute a: columnAttr){
                entries.add(r.getValueAtColumn(getColNum(a.getName())));
            }
            result += "\n" + String.format(format, entries.toArray());
        }

        // bottom line
        result += "\n" + dash;
        return result;
    }

    public String displayTable(){
        return formatResults(attributes, this.records);
    }

    public String select(String[] columns) throws TableException{
        ArrayList<Attribute> selectAttributes = new ArrayList<>();
        // validate all columns
        for(String c: columns){
            isValidColumn(c);
            selectAttributes.add(attributesByCol.get(c));
        }
        
        return formatResults(selectAttributes, this.records);   
    }


    // this might be redundant but lets keep it bc in later phases will be adding conditions
    public String selectAll(){
        return formatResults(this.attributes, this.records);   

    }

    public void addRecordToPage(Record r){
        Page page;
        for (int i = 0; i < pages.size(); i++) {
            page = pages.get(i);
            // does the record beolong in this page?
            if(page.addRecordInOrder(r) > -1){
                page.insertRecordAt(r, i);

                // if the addition overflows the page split
                if(page.overflow()){
                    pages.add(i, page.split());
                }
            }
        }
    }

    public void getPagebyPNum(int num){
        //return page by page number
    }

}