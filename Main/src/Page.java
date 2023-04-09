import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Page {

    /**
     * page capacity
     * TODO: set capacity from storage manager
     * call: Page.setCapacity()
     */
    private static int capacity;
    /**
     * actual size of the page based on contents
     * size = num of bytes
     */
    private int size;

    /**
     * the id of this page
     */
    private int id;

    ArrayList<Record> records;

    private String tableName;

    private boolean isNew;
    
    public Page(int id, String tableName){
        this.size = 0;
        this.records = new ArrayList<>();
        this.id = id;
        this.tableName = tableName;
        this.isNew = true;
    }

    /**
     * creates a new table from memory
     * @param id the table ID the id of the table
     * @param tableName
     * @param records
     */
    public Page(int id, String tableName, ArrayList<Record> records){
        this.id = id;
        this.records = records;
        this.size = 0;
        this.isNew = false;
        this.tableName = tableName;
        setSize();
    }

    public void setSize() {
        // reset the size
        this.size = 0;
        for(Record r: this.records){
            this.size += r.getSize();
        }
    }

    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(){
        this.isNew = true;
    }
    public void setUsed(){
        this.isNew = false;
    }
    public void updateRecord(int index, Record record) {
        this.records.remove(index);
        this.records.add(record);
    }
    public void addRecord (Record record) {
        this.records.add(record);
    }

    public void setTableId(String tableName) {
        this.tableName = tableName;
    }

    public int getNumOfRecords() {
        return this.records.size();
    }

    /**
     * sets the capacity for all pages
     * @param capacity
     */
    public static void setCapacity(int capacity) {
        Page.capacity = capacity;
    }

    /**
     * calculates space available
     * @return int
     */
    public int getSpace(){
        return Page.capacity - size;
    }

    /**
     * return the current capacity
     * @return int
     */
    public static int getCapacity() {
        return capacity;
    }

    public boolean overflow(){
        return this.size > Page.capacity;
    }

    public byte[] getHeader(){
        return Type.concat(Type.convertIntToByteArray(this.id), Type.convertIntToByteArray(this.size));
    }

    public String getTableName() {
        return tableName;
    }

    public byte[] recordsAsBytes()
    {
        byte[] bb = new byte[0];

        for (Record record : this.records)
        {
            Type.concat(bb, record.recordToBytes());
        }
        return bb;
    }
 
    public Page split(int newID){
        int idx = (int)Math.ceil(records.size()/ 2); // the index to split at
        Page otherPage = new Page(newID, this.tableName, new ArrayList<>(this.records.subList(idx, records.size())));
        this.records = new ArrayList<>(this.records.subList(0, idx));
        this.setSize();

        return otherPage;
    }

//    public byte[] getPageAsBytes(){
//        return Type.concat(getHeader(), recordsAsBytes());
//    }

    public void insertRecordAt(Record record, int index){
        this.records.add(index, record);
        this.size += record.getSize();
    }

    /**
     * removes a record from the list of records
     * @param r the record
     */
    public void removeRecord(Record r){
        this.records.remove(r);
        this.size -= r.getSize();
    }

    /**
     * checks if a specific record is contained here
     * @param r
     */
    public boolean containsRecord(Record r){
        return this.records.contains(r);
    }

    /**
     * adds a record to the very end of the current page
     * @param r the record to add
     */
    public void addLast(Record r){
        this.records.add(r);
        this.size += r.getSize();
    }

    /**
     * 
     * @return
     */
    public int getId() {
        return id;
    }

    public ArrayList<Record> getRecords() {
        return records;
    }

    @Override
    public boolean equals(Object obj) {
        boolean res = false;

        if(obj instanceof Page){
            Page other = (Page)obj;
            res = this.id == other.id && this.tableName == other.tableName;
        }

        return res;
    }

    @Override
    public String toString() {
        String str0 = "--------------------------------------------------------------------\n";
        String str = String.format("Page: %d\tCapacity: %d\tSize: %d\tRecords: %d\n", id, capacity, size, records.size());
        String str2 = "";
        for(Record r: records){
            str2 += r + "\n";
        }
        return str0 + str + str2 + str0;
    }

    public ByteBuffer getPageAsBytes(){
        // allocate space based on the page size
        ByteBuffer bb = ByteBuffer.allocate(capacity + (Integer.BYTES * 2));

        // put the size
        bb.putInt(this.id);
        // put the number of records
        bb.putInt(getNumOfRecords());

        // put all the record
        for(Record r : records){
            ByteBuffer bbf = r.getRecordAsBytes();
            bbf.position(0);
            bb.put(bbf);
        }
        return bb;
    }
}