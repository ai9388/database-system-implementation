public class Page {
    
    /***
     * So the page contains the list of records - need to discuss 
     * Page have fix size, use static
     * page have the length to see how many records in it
     * store the pages in the table with a datasturture to get the page number
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
     * string content, typically records in string form
     */
    private String content;

    /**
     * default size of a byte in java
     */
    private static final int BYTESIZE = 2;

    /**
     * the id of this page
     */
    private int id;

    StorageManager sm;

    /**
     * used to convert an existing page from memory into records
     * @param content
     */
    public Page(String content){
        this.content = content;
        this.size = content.length() * BYTESIZE;
        sm = new StorageManager();
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

    public boolean fit(Record record){
        return record.recordToBytes().length <= getSpace();
    }

    public byte[] getHeader(){
        return sm.concat(sm.convertIntToByteArray(this.id), sm.convertIntToByteArray(this.size));
    }
    
}