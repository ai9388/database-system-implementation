import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;

public class PageBuffer {

    Queue<Page> activePages = new LinkedList<>();

    private int bufferSize;

    private int totalPages;

    private String dbPath;


    /**
     * id used as reference when assigning pageID
     * TODO: maybe set from catalog
     */
    private static int LASTPAGEID;

    private HashSet<TableSchema> tables;
    public PageBuffer(String dbPath, int bufferSize, int pageSize){
        this.dbPath = dbPath;
        this.bufferSize = bufferSize;
        Page.setCapacity(pageSize);
        this.tables = new HashSet<>();
    }

    public static void setLASTTABLEID(int id)
    {
        PageBuffer.LASTPAGEID = id;
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
    public void addPage(Page page, TableSchema tschema){
        this.tables.add(tschema);
        this.activePages.add(page);
    }


    /**
     * attemps to find a table by its id
     * attempt 1: try to fins the page in the active pages queue
     * attempt 2: if not in the queue, then get it from memory
     * @param pageId the id of the page
     * @return Page
     */
    public Page getPage(TableSchema table, int pageId) throws TableException {
        // get the page from the active pages queue
        for(Page page: activePages){
            if(page.getId() == pageId && page.getTableName().equals((table.getName()))){
                Page temp = page;
                return temp;
            }
        }

        Page page = readPage(table, pageId);
        if(page == null){
            throw new TableException(12, "");
        }
        tables.add(table);
        page.setUsed();
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
    public void insertRecord(TableSchema tableSchema, Record record) throws TableException {
        boolean inserted = false;
        int tableNumOfPages = tableSchema.getNumberOfPages();
        boolean split = false;

        if(tableNumOfPages == 0) { // if there are no pages for this table
            // TODO: make a new file for the table
            Page page = new Page(getNextPageID(), tableSchema.getName());
            page.insertRecordAt(record, 0); // add this entry to a new page
            // TODO: insert the page into the table file
            updateBuffer(page); // add page to buffer
            tableSchema.addPageID(page.getId());
            return;
        }
        this.tables.add(tableSchema); // add this table to the local collection. used for writing to mem
        // read table page in order from table file
        for(int i = 0; i < tableSchema.getNumberOfPages(); i++) {
            // get the page id
            int id = tableSchema.getPageIds().get(i);
            // TODO: FIX: make sure/assume page has been loaded from db!
            // get the table based on the ID
            Page page = getPage(tableSchema, id);

            // initially not inserted into page
            if(inserted && !split){
                tables.add(tableSchema);
                break;
            }

            if(!inserted) {
                int pageNumOfRecords = page.getNumOfRecords();
                // iterate the records in the page if the record has not been inserted
                for (int x = 0; x < pageNumOfRecords; x++) {
                    if(inserted) break;
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
                newPage.setIsNew();
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
    public TableSchema getTableName(int id){
        for(TableSchema table: tables){
            if(table.getPageIds().contains(id)){
                return table;
            }
        }
        return null;
    }

    /**
     * writes a page to memory
     * @param page the page to write to mem
     * @return bool, true if successful
     */
    public boolean writePage(Page page){
        TableSchema table = getTableName(page.getId());
        String tableName = table.getName();

        File tableFile = new File(dbPath + "/" + tableName);
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(tableFile, "rw");

            byte[] bytes = new byte[Page.getCapacity()];
            bytes = page.getPageAsBytes().array();
            raf.writeInt(getTableName(page.getId()).getNumberOfPages());
            raf.seek(raf.length());
            raf.write(bytes);

            //need up to figure out the update
            raf.close();
        } catch (Exception e) {
            System.out.println("Unable to write to page");
        }
        return false;
    }

    public boolean updatePage(Page page){
        TableSchema table = getTableName(page.getId());

        if (table == null)
        {
            return false;
        }
        String tableName = table.getName();
        //update: have the order of pages in tableschema and then update
        //Future hai-yen: in the future, the page order will be diffrent bc we will delete
        File tableFile = new File(dbPath + "/" + tableName);
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(tableFile, "rw");

            byte[] bytes = new byte[Page.getCapacity()];
            bytes = page.getPageAsBytes().array();
            
            //skip to certain pointer for the page
            int numPages = table.getNumberOfPages();
            raf.writeInt(numPages);
            int order = table.getPageOrder(page.getId());
            int skip = ((order-1) * Page.getCapacity()) + (2 * Integer.BYTES);
            raf.seek(skip);
            raf.write(bytes);

            //need up to figure out the update
            raf.close();
        } catch (Exception e) {
            System.out.println("Unable to write to page");
        }
        return true;
    }

    /**
     * reads a page from memory based on its table name and page id
     * @param pageID the id of the page to get
     * @return the page object
     */
    public Page readPage(TableSchema table, int pageID){
        int order = table.getPageOrder(pageID);
        Page page = Catalog.readIndividualPageFromMemory(dbPath, table.getName(), order, pageID, Page.getCapacity(), table.getAttributes());
        tables.add(table);
        return page;
    }

    /**
     * iterates over all the pages remaining
     * in the buffer and writes them to hardware
     */
    public ArrayList<Page> getPages(){
        return new ArrayList<>(this.activePages);
    }

    /**
     * Gets all the pages of a specific table from memory
     * @param table the table object where pages are read from
     * @return an arraylist of records
     */
    public ArrayList<Record> getRecords(TableSchema table, ArrayList<Attribute> attributeSubset) throws TableException {
        ArrayList<Record> records = new ArrayList<>();

        // iterate all the page ID's to get the pages
        for(Integer pageID : table.getPageIds()){
            Page page = getPage(table, pageID);
            // every page you get
            if(attributeSubset == null || table.getAttributes().size() == attributeSubset.size()){
                records.addAll(page.getRecords());
            }
            else {
                records.addAll(page.getRecords(attributeSubset));
            }
        }

        return records;
    }

    public boolean dropTable(String TableName){
        File file = new File(dbPath + "/" + TableName);
        if(!file.exists()){
            return true;
        }
        return file.delete();
    }

    public String displayPages(){
        String str = "";
        for(Page page: activePages){
            str += page.toString();
        }

        return str;
    }

    public void purge()
    {
        for(Page page: activePages){
            if(page.isNew()){
                writePage(page);
            }
            else {
                updatePage(page);
            }
        }
    }

}
