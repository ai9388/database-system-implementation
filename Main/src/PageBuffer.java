import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;

public class PageBuffer {

    Queue<Page> activePages = new LinkedList<>();

    private int bufferSize;

    private int totalPages;

    private static String dbPath;


    /**
     * id used as reference when assigning pageID
     * TODO: maybe set from catalog
     */
    private static int LASTPAGEID;
    private HashSet<TableSchema> tables;
    public PageBuffer(String dbPath, int bufferSize, int pageSize){
        PageBuffer.dbPath = dbPath;
        this.bufferSize = bufferSize;
        Page.setCapacity(pageSize);
        this.tables = new HashSet<>();
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
            writePage(dbPath, oldPage);
        }

        // add the new page to active pages
        // if the page is already in the buffer, remove and read
        if(activePages.remove(page)){
            activePages.add(page);
        }
        else{
            activePages.add(page);
        }
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
        boolean split = false;

        if(tableNumOfPages == 0) { // if there are no pages for this table
            // TODO: make a new file for the table
            Page page = new Page(getNextPageID(), tableSchema.getTableID());
            page.insertRecordAt(record, 0); // add this entry to a new page
            // TODO: insert the page into the table file
            updateBuffer(page); // add page to buffer
            tableSchema.addPageID(page.getId());
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
            if(inserted && !split){
                tables.add(tableSchema);
                break;
            }

            if(!inserted) {
                int pageNumOfRecords = page.getNumOfRecords();
                // iterate the records in the page if the record has not been inserted
                for (int x = 0; x < pageNumOfRecords; x++) {
                    Record currentRecord = page.getRecords().get(x);
                    // if record belongs before the current record
                    if (record.compareTo(currentRecord) < 0) {
                        // insert record before it
                        page.insertRecordAt(record, x);
                        updateBuffer(page);
                        inserted = true;
                    }
                }
                // if last page visited and record still not inserted
                if(i == tableNumOfPages - 1 && !inserted ){
                    // insert it to the last page of the table file
                    page.addLast(record);
                    inserted = true;
                }
            }

            // if table becomes overfull, split the page; end
            if(page.overflow()) {
                Page newPage = page.split(getNextPageID());
                tableSchema.addPageID(newPage.getId());
                updateBuffer(newPage); // update the buffer with new Page
                inserted = true;
                split = true;
            }
            else{
                split = false;
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
        //get the table path, then call the page to byte from Pages
        //get the order of the pages from the table
        //add new page: get# of pages to skip there #pages X page size
        //update: have the order of pages in tableschema and then update
        File tableFile = new File(dbPath + "/" + tableName);
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(tableFile, "rw");

            byte[] bytes = new byte[Page.getCapacity()];
            bytes = page.getPageAsBytes().array();
            
            raf.seek(raf.length());
            raf.write(bytes);

            //need up to figure out the update
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePage(Page page){
        String tableName = getTableName(page.getId());
        // TODO: serialize @ hai-yen
        //update: have the order of pages in tableschema and then update
        //Future hai-yen: in the future, the page order will be diffrent bc we will delete page
        File tableFile = new File(dbPath + "/" + tableName);
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(tableFile, "rw");

            byte[] bytes = new byte[Page.getCapacity()];
            bytes = page.getPageAsBytes().array();
            
            int skip = page.getId() * Page.getCapacity();
            raf.seek(skip);
            raf.write(bytes);

            //need up to figure out the update
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public ArrayList<Page> getPages(){
        return new ArrayList<>(this.activePages);
    }

    public String displayPages(){
        String str = "";
        for(Page page: activePages){
            str += page.toString();
        }

        return str;
    }

    //TODO: Hai-Yen: writing pages to table fully
    // public void writeToTableFile(File new_table, int fileID, int numOfPages, int numOfRecords)
    // {
    //     RandomAccessFile raf;
    //     try {
    //         raf = new RandomAccessFile(new_table, "rw");

    //         byte[] bytes = new byte[0];

    //     //   bytes= Type.concat(bytes, Type.convertIntToByteArray(fileID));
    //         bytes=Type.concat(bytes, Type.convertIntToByteArray(numOfPages));
    //     //   bytes=Type.concat(bytes, Type.convertIntToByteArray(numOfRecords));
        

            
    //         raf.write(bytes);
    //         raf.close();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

}
