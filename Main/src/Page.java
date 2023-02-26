import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Page {
    
    int numOfRecords;

    /**
     * page capacity
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

    StorageManager sm;

    ArrayList<Record> records;

    int pkIdx;
    
    public Page(int pkIdx){
        this.size = 0;
        this.pkIdx = pkIdx;
        this.records = new ArrayList<>();
    }

    public Page(ArrayList<Record> records){
        this.records = records;
        this.size = 0;
        setSize();
    }

    public void setSize() {
        // reset the size
        this.size = 0;
        for(Record r: this.records){
            this.size += r.getSize();
        }
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

    public byte[] recordsAsBytes()
    {
        byte[] bb = new byte[0];

        for (Record record : this.records)
        {
            Type.concat(bb, record.recordToBytes());
        }
        return bb;
    }
 
    public Page split(){
        int idx = (int)Math.ceil(records.size()/ 2); // the index to split at
        Page otherPage = new Page(new ArrayList<>(this.records.subList(idx, records.size())));
        this.records = new ArrayList<>(this.records.subList(0, idx));
        this.setSize();

        return otherPage;
    }

    public byte[] getPageAsBytes(){
        return Type.concat(getHeader(), recordsAsBytes());
    }
    

    public void setRecordsOrder() {
        Collections.sort(records, new Comparator<Record>() {
            @Override
            public int compare(Record r1, Record r2) {
                Object pkValue1 = r1.getValueAtColumn(pkIdx);
                Object pkValue2 = r2.getValueAtColumn(pkIdx);

                // INT
                if (pkValue1 instanceof Integer) {
                    return Integer.compare((int) pkValue1, (int) pkValue2);
                }

                // DOUBLE
                else if (pkValue1 instanceof Double) {
                    return Double.compare((double) pkValue1, (double) pkValue2);
                }

                // BOOLEAN
                else if (pkValue1 instanceof Boolean) {
                    return Boolean.compare((boolean) pkValue1, (boolean) pkValue2);
                }

                // String
                else {
                    return String.valueOf(pkValue1).compareTo(String.valueOf(pkValue2));
                }

            }
        });
    }

    public void insertRecordAt(Record record, int index){
        this.records.add(index, record);
        this.size += record.getSize();
    }

    public int addRecordInOrder(Record record){
        if(records.size() == 0){
            return 0;
        }
        for (int i = 0; i < records.size(); i++) {
            if(record.compareTo(records.get(i)) < 0){
                return i;
            }
        }

        return -1;
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

}