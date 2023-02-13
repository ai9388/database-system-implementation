import java.util.*;

public class Table{
    
    private String name;
    private int tableID;
    private ArrayList<Attribute> attributes;
    private ArrayList<Record> records;

    public Table(String name, int tid, ArrayList<Attribute> attr, ArrayList<Record> recs)
    {
        this.name = name;
        this.tableID = tid;
        this.attributes = attr;
        this.records = recs;
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

    public boolean insertRecord(Record rec)
    {
        return false;
    }

    public boolean removeRecord(String pk, String pkValue)
    {
        return false;
    }

    public boolean updateRecord(String pk, String pkValue)
    {
        return false;
    }

}