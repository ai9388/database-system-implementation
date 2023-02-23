import java.util.*;

public class Table{
    
    private String name;
    private int tableID;
    private ArrayList<Attribute> attributes;
    private ArrayList<Record> records;
    private Attribute primaryAttribute;
    private int primaryIndex;
    private HashMap<String, Record> recordsByPK;
    private HashMap<String, Attribute> attributesByCol;

    public Table(String name, int tableID, ArrayList<Attribute> attributes, Attribute primaryAttribute, int primaryIndex) {
        this.name = name;
        this.tableID = tableID;
        this.attributes = attributes;
        this.records = new ArrayList<>();
        this.primaryAttribute = primaryAttribute;
        setAttributesByCol();
        this.primaryIndex = primaryIndex;
        this.recordsByPK = new HashMap<>();
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
            Record record = new Record(values, attributes);
            this.insertRecord(record);
            return true;
        } catch (InvalidDataTypeException e) {
            // creation of record failed
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Table insertRecord(Record record) {
        this.records.add(record);
        this.recordsByPK.put(primaryAttribute.getName(), record);
        return this;
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
        for (Attribute attribute : attributes) {
            if(attribute.getName().equals(column)){
                return true;
            }
        }
        throw new TableException(1);
    }

    public int getNumberOfRecords(){
        return records.size();
    }

    /**
     * returns the table as a string in a nice format
     * @return formatted table
     */
    public String displayTable(){
        String format = "|";
        String result = "";
        int len = 1;
        String dash;
        Object[] headers = new Object[attributes.size()];
        for(int i = 0; i < attributes.size(); i++){
            Attribute a = attributes.get(i);
            headers[i] = attributes.get(i).getName().toUpperCase();
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

        // add all the records
        for(Record r: records){
            result += "\n" + String.format(format, r.getEntries().toArray());
        }

        // bottom line
        result += "\n" + dash;
        return result;
    }

    /***
     * TODO
     * getting a record by primary key
    • getting a page by table and page number
     *
     */

}