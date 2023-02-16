import java.util.*;

public class Table{
    
    private String name;
    private int tableID;
    private ArrayList<Attribute> attributes;
    private ArrayList<Record> records;
    private Attribute primaryAttribute;
    private HashMap<String, Record> recordsByPK;
    private HashMap<String, Attribute> attributesByCol;

    public Table(String name, int tid, ArrayList<Attribute> attr, ArrayList<Record> recs)
    {
        this.name = name;
        this.tableID = tid;
        this.attributes = attr;
        this.records = recs;
        setAttributesByCol();
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
            ArrayList<String> entries = new ArrayList<String>(Arrays.asList(values));
            Type.validateAll(entries, attributes); // if this fails exception is raised
            // assuming valid, create record
            Record record = new Record(entries, attributes);
            records.add(record);
            return true;
        } catch (InvalidDataTypeException e) {
            // creation of record failed
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * find a record based on primary 
     * @param pkValue value of primary key
     * @return the found record
     */
    public Record getRecordByPK(String pkValue){
        // TODO: implement
        return null;
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
        // validate the primary key
        try {
            Record oldRecord = null;
            // if primary key is valid, remove record from collections
            if(Type.validateType(pkValue, primaryAttribute)){
                oldRecord = recordsByPK.remove(pkValue);
                // TODO: there might be a pk exception here if pk does not exist
                records.remove(oldRecord);
            }

            // validate column name
            if(isValidColumn(column)){
                // validate the type of new value
                if(Type.validateType(newEntry, attributesByCol.get(column))){
                    // update the old record, copy and re-add
                    oldRecord.updateByColumn(column, newEntry);
                    Record newRecord = new Record(oldRecord.getEntries(), attributes);
                    records.add(newRecord);
                }
            }

        } catch (InvalidDataTypeException e) {
            System.out.println(e.getMessage());
            return false;
        }

        // TODO: catch exception form inValidColumnName
        
        return true;
    }

    /**
     * checks if the provided column name exists in this table
     * @param column the name of the column
     */
    public boolean isValidColumn(String column){
        for (Attribute attribute : attributes) {
            if(attribute.getName().equals(column)){
                return true;
            }
        }
        // TODO: raise a table exception of invalid column name
        return false;
    }

}