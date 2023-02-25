import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Page {
    
    /***
     * So the page contains the list of records - need to discuss 
     * Page have fix size, use static
     * page have the length to see how many records in it
     * store the pages in the table with a data structure to get the page number
     * Have a split method to split the page to 2 pages. 
     * 
     * Methods needed:
     * - method that splits the current page, and returns a new one with the second half of the content
     * - method that takes a bunch of records and compacts them to be the page's content
     * - constructor that takes a string of the page's contents 
     * - method that reverts content into records
     * + method that returns true if another record can be added to the page
     * - we assume that the records are added in order of primary index.
     */
    
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
        this.size = 0; 
    }

        this.size = pageSize;
    }

    public Page(ArrayList<Record> records){
        this.records = records;
        setSize();
    }

    public void setSize() {
        for(Record r: records){
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
        return capacity - size;
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
        int idx = Math.ceilDiv(records.size(), 2); // the index to split at
        Page otherPage = new Page(new ArrayList<>(this.records.subList(idx + 1, records.size() + 1)));
        this.records = new ArrayList<>(this.records.subList(0, idx + 1));
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
        for (int i = 0; i < records.size(); i++) {
            if(record.compareTo(records.get(i)) == -1){
                return i;
            }
        }

        return -1;
    }

    
}