public class Page {
    
    /***
     * So the page contains the list of records - need to discuss 
     * Page have fix size, use static
     * page have the length to see how many records in it
     * store the pages in the table with a datasturture to get the page number
     * Have a split method to split the page to 2 pages. 
     */
    
    int numOfRecords;

    private static int capacity;

    private int size;

    
    
    public Page(int size){
        this.size = size;
    }

    public static void setCapacity(int capacity) {
        Page.capacity = capacity;
    }

    public int getSpace(){
        return capacity - size;
    }

    public static int getCapacity() {
        return capacity;
    }
    
}
