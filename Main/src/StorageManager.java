import java.util.*;

public class StorageManager {
    public Database db;
    private int bufferSize;
    public ArrayList<Page> pageBuffer = new ArrayList<>();
    public String dbPath;
    public int pageSize;
    public Catalog catalog;

    //call the get the most recent page from table for insert, update function
    //then call the LRU(on the page)

    public StorageManager(String dbName, String dbPath, int bufferSize, int pageSize){
        this.catalog = new Catalog(dbPath);
        this.db = new Database(dbName, new HashMap<String, Table>(), catalog, dbPath, new HashMap<Integer, Table>());
        this.bufferSize = bufferSize;
        this.dbPath = dbPath;
        this.pageSize = pageSize;
    }

    public Database getDb() {
        return this.db;
    }

    /***
     * get table using table name from database
     * for parser be able to get the table with the given table name
     * @param table_name
     * @return
     */
    public Table getTable(String table_name) {
        return db.getSingleTable(table_name);
    }

    public ArrayList<Table> getAllTables(){
        return db.getAllTables();
    }

    public void createTable(String tableName, ArrayList<Attribute> attributes) throws TableException{
        db.createTable(tableName, attributes);
    }

    /***
     * Insert records into given table from parser
     * @param tableName
     * @param records
     * @throws PrimaryKeyException
     */
    public void insertRecords(String tableName, ArrayList<Record> records) throws PrimaryKeyException {
        Table t = null;
        for (Record record: records) {
            t = getTable(tableName);
            t.insertRecord(record);
            Page page = t.getMostRecentPage();
            LRU(page);
        }
    }

    /***
     * adding table into database
     * @param table
     */
    public void addTable(Table table) {
        db.addTable(table);
    }

    /***
     * displaying table schema
     * @throws TableException
     */
    public void displaySchema() throws TableException {
        System.out.println("DB location: " + dbPath);
        System.out.println("Page Size: " + String.valueOf(pageSize));
        System.out.println("Buffer Size: " + String.valueOf(bufferSize));
        System.out.println();
        
        ArrayList<Table> tables = db.getAllTables();
        if(tables.size() == 0){
            System.out.println("No tables to display");
        }
        else{
            System.out.println("Tables: ");
        }
        for (Table table : tables) {
            System.out.println(table.displayTableInfo());
        }
    }

    
    public void displayTableInfo(String tableName) throws TableException {
        Table table = db.getTableByName(tableName);
        if (table == null) {
            System.out.println("From storage manager");
            throw new TableException(2, tableName);
        } else {
            System.out.println(table.displayTableInfo());
        }
    }

    /***
     * insert records into given table from parser
     * @return true if there are tables
     */
    public boolean hasTable() {
        int num = db.getAllTables().size();
        return num > 0;
    }
     
    

    public String selectFromTable(String tableName, String[] columns) throws TableException{
        if (db.getTableByName(tableName) == null) {
            throw new TableException(2, tableName);
        } else {
            return db.selectFromTable(tableName, columns);
        }
    }

    /***
     * Get record from the table using primary key
     * @param table
     * @param key
     * @return record
     * @throws PrimaryKeyException
     */
    public Record getRecordFromPrimaryKey(Table table, String key) throws PrimaryKeyException{
        return table.getRecordByPK(key);
    }
    
    public Page getPageByTablePNumber(Table table, int pageNumber){
        return table.getPageByPNum(pageNumber);
    }

    /***
     * get all records for a given table number
     * @param tableNumber
     * @return
     */
    public ArrayList<Record> getAllRecords(int tableNumber){
        return null;
    }

    public void insertOneRecordIntoTable(String tableName, String[] record) throws TableException, InvalidDataTypeException, PrimaryKeyException{
        Table table = db.getTableByName(tableName);
        if (table == null) {
            throw new TableException(2, tableName);
        }
        table.insertRecord(record);
        Page mostRecentPage = table.getMostRecentPage();
        LRU(mostRecentPage);
    }

    /***
     * insert a Record into a table
     * @param table
     * @param record
     * @throws InvalidDataTypeException
     * @throws PrimaryKeyException
     */
    public void insertARecord(Table table, String[] record) throws InvalidDataTypeException, PrimaryKeyException, TableException{
        table.insertRecord(record);
        Page mostRecentPage = table.getMostRecentPage();
        LRU(mostRecentPage);
    }

    /***
     * delete record by primary key from a given table
     * @param primaryKey
     * @param table
     * @throws InvalidDataTypeException
     * @throws PrimaryKeyException
     */
    public void deleteRecord(String primaryKey, Table table) throws PrimaryKeyException, InvalidDataTypeException{
        table.removeRecordByPK(primaryKey);
        Page mostRecentPage = table.getMostRecentPage();
        LRU(mostRecentPage);
    }

    /***
     * update record by primary key from a given table
     * @param primaryKey
     * @param table
     * @throws InvalidDataTypeException
     * @throws PrimaryKeyException
     * @throws TableException
     */
    public void updateRecord(String primaryKey, Table table,  String column, String newEntry) throws TableException, PrimaryKeyException, InvalidDataTypeException{
        table.updateRecordByPK(primaryKey, column, newEntry);
        Page mostRecentPage = table.getMostRecentPage();
        LRU(mostRecentPage);
    }
    
    /***
     * Least recently used
     * If the buffer is full, then remove the least used one(in the front) 
     * and adding the new one(in the back)
     * @param page
     */
    public void LRU(Page page){
        if(pageBuffer.size() > bufferSize){
            pageBuffer.remove(0);
            pageBuffer.add(page);
        }
        else{
            pageBuffer.add(page);
        }
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * Writing the data to the catalog
     */
    public void writeToCatalog()
    {
        this.catalog.setTables(this.getAllTables());
        byte[] bb = this.catalog.createCatalog();
        this.catalog.writeToFile(bb);
    }

}
