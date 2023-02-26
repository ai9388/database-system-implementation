import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;

public class StorageManager {
    private Database db;
    private int bufferSize;
    public ArrayList<Page> pageBuffer = new ArrayList<>();

    //create catalog 

    //creating catalog, check if there is one 

    //check of if the database exist, if not, create new one, else, get the dabase.

    public StorageManager(String dbName, String dbPath, int bufferSize){
        this.db = new Database(dbName, new HashMap<String, Table>(), null, dbPath, new HashMap<Integer, Table>());
        this.bufferSize = bufferSize;
    }

    public Database getDb() {
        return db;
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
     */
    public void insertRecords(String tableName, ArrayList<Record> records) {
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
        // TODO: write table to the database?
        db.addTable(table);
    }

    /***
     * displaying table schema
     * @throws TableException
     */
    public void displaySchema(String tableName) throws TableException {
        Table table = db.getTableByName(tableName);
        table.displayTableSchema();
    }

    
    public void displayInfo(String tableName) throws TableException {
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


    /**
     * adding the initial information to the file
     * this includes the file id, number of pages, and number of records
     */
    public void addIntialInfoToTable(File new_table, int fileID, int numOfPages, int numOfRecords)
    {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(new_table, "rw");

            byte[] bytes = new byte[3 * Integer.BYTES];
            // writing the file id
            for (int i = 0; i < Integer.BYTES; i++) {
                Type.concat(bytes, Type.convertIntToByteArray(fileID));
            }

            // writing the number of pages
            for (int i = Integer.BYTES; i < 2 * Integer.BYTES; i++) {
                Type.concat(bytes, Type.convertIntToByteArray(numOfPages));
            }

            // writing the number of records
            for (int i = 2 * Integer.BYTES; i < 3 * Integer.BYTES; i++) {
                Type.concat(bytes, Type.convertIntToByteArray(numOfRecords));
            }

            raf.write(bytes);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
