import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;

public class StorageManager {
    public Database db;
    private int bufferSize;
    public PageBuffer pageBuffer;
    public String dbPath;
    public int pageSize;
    public Catalog catalog;

    //call the get the most recent page from table for insert, update function
    //then call the LRU(on the page)

    public StorageManager(String dbName, String dbPath, int bufferSize, int pageSize){
        this.catalog = new Catalog(dbPath, pageSize);
        this.db = new Database(dbName, dbPath);
        this.bufferSize = bufferSize;
        this.dbPath = dbPath;
        this.pageSize = pageSize;
        this.pageBuffer = new PageBuffer(dbPath, bufferSize, pageSize);
    }

    /**
     * accessor for the database object used in this session
     * @return
     */
    public Database getDb() {
        return this.db;
    }

    /***
     * get table using table name from database
     * for parser be able to get the table with the given table name
     * @param table_name the name of the table
     * @return
     */
    public TableSchema getTable(String table_name) throws TableException{
        return db.getTable(table_name);
    }

    /**
     * returns all tables from the DB
     * @return table collection
     */
    public ArrayList<TableSchema> getAllTables(){
        return db.getTables();
    }

    /**
     * creates a new table
     * used by parser
     * @param tableName the name of the table
     * @param attributes the attributes of this table
     * @throws TableException
     */
    public void createTable(String tableName, ArrayList<Attribute> attributes) throws TableException{
        db.createTable(tableName, attributes);
    }

    /***
     * Insert records into given table. Only for records that already exist
     * @param tableName table schema name
     * @param recordsInfo the information about all the records to be inserted
     * @throws PrimaryKeyException
     */
    public void insertRecords(String tableName, ArrayList<String[]> recordsInfo) throws TableException, PrimaryKeyException, InvalidDataTypeException, UniqueException {
        for(String[] recordInfo: recordsInfo){
            insertRecord(tableName, recordInfo);
        }
    }

    /***
     * displaying database schema
     */
    public void displaySchema(){
        System.out.println("DB location: " + dbPath);
        System.out.println("Page Size: " + pageSize);
        System.out.println("Buffer Size: " + bufferSize);
        System.out.println();
        
        ArrayList<TableSchema> tables = db.getTables();
        if(tables.size() == 0){
            System.out.println("No tables to display");
        }
        else{
            System.out.println("Tables: ");
        }
        for (TableSchema table : tables) {
            System.out.println(table.displayTableSchema());
        }
    }

    /**
     * display the schema for a specific table
     * @param tableName the name of the table
     * @throws TableException if the table name is not valid
     */
    public void displayTableInfo(String tableName) throws TableException {
        TableSchema table = db.getTable(tableName);
        System.out.println(table.displayTableSchema());
    }

    public String selectFromTable(String tableName, String[] columns) throws TableException{
        if (db.getTable(tableName) == null) {
            throw new TableException(2, tableName);
        } else {
            return db.selectFromTable(tableName, columns);
        }
    }

    /***
     * Get record from the table using primary key
     * @param table the table we're getting the record from
     * @param key the value of the primary key
     * @return record
     */
    public Record getRecordByPrimaryKey(TableSchema table, String key){
        // TODO
        // validate the primary key attribute and that the value matches the type
        // validate the primary key uniqueness

        return null;
    }

    /***
     * TODO this is Kind of like select. could be renamed
     * get all records for a given table number
     * @param tableNumber the table name
     * @return an arraylist of records
     */
    public ArrayList<Record> getAllRecords(int tableNumber){
        return null;
    }

    /**
     * inserts a record into the named table pages
     * @param tableName the name of the pages
     * @param recordInfo information for the records
     * @throws TableException if the table name is invalid
     * @throws InvalidDataTypeException if the types provided in the record info are invalid
     * @throws PrimaryKeyException if the primary key isn't valid or if repeated
     */
    public void insertRecord(String tableName, String[] recordInfo) throws TableException, InvalidDataTypeException, PrimaryKeyException, UniqueException{
        TableSchema table = db.getTable(tableName);
        Record record = null;
        if (table == null) {
            throw new TableException(2, tableName);
        }
        record = db.validateRecord(table, recordInfo);
        // TODO: validate the primary key uniqueness
        if(record != null){
            pageBuffer.insertRecord(table, record);
        }

    }

    /***
     * delete record by primary key from a given table
     * @param primaryKey the primary key
     * @param tablename the table to delete record from
     * @throws PrimaryKeyException if the primary key is invalid in some way
     */
    public void deleteRecord(String primaryKey, String tablename) throws PrimaryKeyException, InvalidDataTypeException{
        // not due until phase 3
    }

    /***
     * update record by primary key from a given table
     * @param primaryKey
     * @param table
     * @throws InvalidDataTypeException
     * @throws PrimaryKeyException
     * @throws TableException
     */
    public void updateRecord(String primaryKey, TableSchema table,  String column, String newEntry){
        // not due until phase 3
    }

    /**
     * Writing the data to the catalog
     */
    public void writeToCatalog()
    {
        this.catalog.setTables(db.getTables());
        byte[] bb = this.catalog.createCatalog();
        this.catalog.writeToCatalogFile(bb);
        this.catalog.readCatalog();
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

            byte[] bytes = new byte[0];

        //   bytes= Type.concat(bytes, Type.convertIntToByteArray(fileID));
            bytes=Type.concat(bytes, Type.convertIntToByteArray(numOfPages));
        //   bytes=Type.concat(bytes, Type.convertIntToByteArray(numOfRecords));
        

            
            raf.write(bytes);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * drops the table with given name
     * @param table_name
     */
    public void dropTable(String table_name) throws TableException {
        if (!db.dropTable(table_name)) {
            throw new TableException(2, table_name);
        }
    }

    /**
     * Drop the given attribute from given table
     * @param attribute_name
     * @param table_name
     */
    public void dropAttributeFromTable(String attribute_name, String table_name) throws TableException {
        db.dropAttribute(attribute_name, table_name);
    }

    public void addAttributeToTable(Attribute attribute, String value, String table_name) throws TableException {
        db.addAttribute(attribute, value, table_name);
    }
}
