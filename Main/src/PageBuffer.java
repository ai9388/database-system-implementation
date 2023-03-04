import java.util.*;

public class PageBuffer {

    Queue<Page> activePages = new LinkedList<>();

    private static int bufferSize;

    private int totalPages;


    /**
     * id used as reference when assigning pageID
     * TODO: maybe set from catalog
     */
    private static int LASTPAGEID;
    private HashSet<TableSchema> tables;
    public PageBuffer(int bufferSize, int pageSize, int totalPages){
        this.bufferSize = PageBuffer.bufferSize;
        Page.setCapacity(pageSize);
        this.totalPages = totalPages;
    }

    public int getNextPageID(){
        PageBuffer.LASTPAGEID++;
        return PageBuffer.LASTPAGEID;
    }

    /**
     * checks if the active pages queue is full
     * @return bool
     */
    public boolean isFull(){
        return activePages.size() == bufferSize;
    }

    /**
     * add a page to the buffer of active pages
     * only used when pages are loaded from and
     * created from hardware
     * @param page
     */
    public void addPage(Page page){
        this.activePages.add(page);
    }


    /**
     * attemps to find a table by its id
     * attempt 1: try to fins the page in the active pages queue
     * attempt 2: if not in the queue, then get it from memory
     * @param pageId the id of the page
     * @return Page
     */
    public Page getPage(String tableName, int pageId){
        // get the page from the active pages queue
        for(Page page: activePages){
            if(page.getId() == pageId){
                Page temp = page;
                return temp;
            }
        }

        Page page = readPage(tableName, pageId);
        // update active pages
        updateBuffer(page);

        return page;
    }

    public void updateBuffer(Page page){
        if(isFull()) {
            // remove the top from the queue if full
            Page oldPage = activePages.poll();
            // write the old page to storage
            writePage(oldPage);
        }

        // add the new page to active pages
        activePages.add(page);
    }

    /**
     * inserts a record into a page.
     * used the table schema to figure out the table page numbers
     * see: Phase1 - 4.3.1 Inserting a record
     * @param tableSchema schema that defines the table
     * @param record the record to be inserted
     * @return true if inserted
     */
    public void insertRecord(TableSchema tableSchema, Record record){
        boolean inserted = false;
        int tableNumOfPages = tableSchema.getNumberOfPages();

        if(tableNumOfPages == 0) { // if there are no pages for this table
            // TODO: make a new file for the table
            Page page = new Page(getNextPageID());
            page.insertRecordAt(record, 0); // add this entry to a new page
            // TODO: insert the page into the table file
            updateBuffer(page); // add page to buffer
            return;
        }
        // read table page in order from table file
        for(int i = 0; i < tableSchema.getNumberOfPages(); i++) {
            // get the page id
            int id = tableSchema.getPageIds().get(i);
            // TODO: FIX: make sure/assume page has been loaded from db!
            // get the table based on the ID
            Page page = getPage(tableSchema.getName(), id);

            // initially not inserted into page
            if(inserted){
                break;
            }

            // iterate the records in the page
            for (int x = 0; x < page.numOfRecords; x++) {
                Record currentRecord = page.getRecords().get(i);
                // if record belongs before the current record
                if(record.compareTo(currentRecord) < 0){
                    // insert record before it
                    page.insertRecordAt(record, x);
                    updateBuffer(page);
                    inserted = true;
                }
            }
            // if the record does not get inserted
            // if this is the last page visited and record not inserted
            if(i == tableNumOfPages - 1 && !inserted ){
                // insert it to the last page of the table file
                page.addLast(record);
                inserted = true;
            }

            // if table becomes overfull, split the page; end
            if (page.overflow()) {
                // TODO: add the next page id here
                Page newPage = page.split(getNextPageID());
                updateBuffer(newPage); // update the buffer with new Page
                inserted = true;
            }
        }
    }

    /**
     * gets the name of the table containing this page id
     * @param id the page id
     * @return the name of the table
     */
    public String getTableName(int id){
        for(TableSchema table: tables){
            if(table.getPageIds().contains(id)){
                return table.getName();
            }
        }
        return "";
    }

    /**
     * writes a page to memory
     * @param page the page to write to mem
     * @return bool, true if successful
     */
    public boolean writePage(Page page){
        String tableName = getTableName(page.getId());
        // TODO: serialize @ hai-yen
        return false;
    }

    /**
     * reads a page from memory based on its table name and page id
     * @param pageId the id of the page to get
     * @return the page object
     */
    public Page readPage(String tableName, int pageId){
        //TODO: deserialize @ AlexI
        return null;
    }

    /**
     * iterates over all the pages remaining
     * in the buffer and writes them to hardware
     */
    public void purge(){
        // TODO: serialize @ hai-yen
        // iterate pages
    }
}
