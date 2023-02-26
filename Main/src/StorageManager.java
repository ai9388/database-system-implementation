import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;

public class StorageManager {
    private Database db;
    private int bufferSize;
    public ArrayList<Page> pageBuffer = new ArrayList<>();
    public String dbPath;
    public int pageSize;
    public Catalog catalog;

    //create catalog 

    //creating catalog, check if there is one 

    //check of if the database exist, if not, create new one, else, get the dabase.

    public StorageManager(String dbName, String dbPath, int bufferSize, int pageSize)
    {
        this.db = new Database(dbName, new HashMap<String, Table>(), null, dbPath, new HashMap<Integer, Table>());
        this.catalog = new Catalog(dbPath);
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
        System.out.println("Tables: ");

        ArrayList<Table> tables = db.getAllTables();
        for (Table table : tables) {
            table.displayTableInfo();
        }
    }

    
    public void displayTableInfo(String tableName) throws TableException {
        Table table = db.getTableByName(tableName);
        table.displayTableInfo();
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
        return db.selectFromTable(tableName, columns);
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
        table.insertRecord(record);
    }

    /***
     * insert a Record into a table
     * @param table
     * @param record
     * @throws InvalidDataTypeException
     * @throws PrimaryKeyException
     */
    public void insertARecord(Table table, String[] record) throws InvalidDataTypeException, PrimaryKeyException{
        table.insertRecord(record);
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

}
