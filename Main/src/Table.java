import java.util.*;

public class Table{
    
    private String name;
    private int tableID;
    private ArrayList<Attribute> attributes;
    private ArrayList<Record> records;
    private Attribute primaryAttribute;
    private int primaryIndex;
    private HashMap<String, Record> recordsByPK;

    public Table(String name, int tid, ArrayList<Attribute> attr, ArrayList<Record> recs, Attribute primaryAttribute, int primaryIndex)
    {
        this.name = name;
        this.tableID = tid;
        this.attributes = attr;
        this.records = recs;
        this.primaryIndex = primaryIndex;
        this.recordsByPK = new HashMap<>();
        for (Record r: records) {
            recordsByPK.put(primaryAttribute.getName(), r);
        }
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

    /*
     * inserts a record into collection
     *
     */
    public boolean insertRecord(String[] values)
    {
        try {
            Record record = new Record(values, attributes);
            records.add(record);
            return true;
        } catch (InvalidDataTypeException e) {
            // creation of record failed
            System.out.println(e.getMessage());
            return false;
        }

    }

    public boolean removeRecordByPrimaryKey(String pkValue)
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

    public Record getRecord(String pkValue) {
        return this.recordsByPK.get(pkValue);
    }

    public boolean updateRecord(String pkValue, String column, String newValue)
    {
        // validate the primary key
        try {
            Record oldRecord;
            ArrayList<String> values = new ArrayList<>();
            // if primary key is valid, remove record from collection
            if(Type.validateType(pkValue, primaryAttribute)){
                oldRecord = recordsByPK.remove(pkValue);
                for (Attribute a : attributes) {
                    if (a.getName().equals(column)) {
                        values.add(newValue);
                    } else {
                        values.add(oldRecord.getvalueAtColumn(a.getName()));
                    }
                }
                Record r = new Record(values.toArray(new String[0]), attributes);
                this.records.add(r);
                this.recordsByPK.put(pkValue, r);
            }

            // validate column name
            if(isValidColumn(column)){
                // TODO: update the value at column
            }

        } catch (InvalidDataTypeException e) {
            System.out.println(e.getMessage());
            return false;
        }

        // TODO catch exception form isValidTableName

        return true;
    }

    /*
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