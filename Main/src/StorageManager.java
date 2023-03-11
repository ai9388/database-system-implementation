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

        if (this.catalog.checkExistance())
        {
            this.catalog.readCatalog();
            this.db.setTables(this.catalog.getTables());
        }

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
        catalog.createTableFile(tableName);
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
        System.out.println("Page Size: " + this.pageSize);
        System.out.println("Buffer Size: " + this.bufferSize);
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
     * get all records for a given table number
     * @param tableName the table name
     * @return an arraylist of records
     */
    private ArrayList<Record> loadRecords(TableSchema table){

        ArrayList<Record> records = null;

        records = pageBuffer.getRecords(table);

        return records;
    }


    public void select(String tableName) throws TableException {
        TableSchema tableSchema = db.getTable(tableName);
        ArrayList<Record> records = loadRecords(tableSchema);
        if(records == null){
            System.out.println("No Records to show");
        }
        System.out.println(formatResults(tableSchema.getAttributes(), records));
    }

    /**
     * inserts a record into the named table pages
     * @param tableName the name of the pages
     * @param recordInfo information for the records
     * @throws TableException if the table name is invalid
     * @throws InvalidDataTypeException if the types provided in the record info are invalid
     * @throws PrimaryKeyException if the primary key isn't valid or if repeated
     */
    public void insertRecord(String tableName, String[] recordInfo) throws TableException, InvalidDataTypeException, PrimaryKeyException, UniqueException {
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

    /**
     * Writing the data to the catalog
     */
    public void writeToCatalog()
    {
        byte[] bb = this.catalog.createBytesForCatalog();
        this.catalog.writeBytesToCatalogFile(bb);
    }

    public void shutDown(){
        // purge all the pages
        pageBuffer.purge();

        // write changes to the catalog
        writeToCatalog();
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
        db.dropTable(table_name);
        pageBuffer.dropTable(table_name);
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

    public String formatResults(ArrayList<Attribute> tableAttributes, ArrayList<Record> records) {
        String format = "|";
        String result = "";
        int len = 1;
        String dash;
        Object[] headers = new Object[tableAttributes.size()];
        for (int i = 0; i < tableAttributes.size(); i++) {
            Attribute a = tableAttributes.get(i);
            headers[i] = tableAttributes.get(i).getName().toUpperCase();
            if (a.getType() == Type.VARCHAR || a.getType() == Type.CHAR) {
                int temp = Math.max(a.getName().length() + 2, a.getN() + 2);
                format += "%-" + temp + "s|";
                len += temp + 1;
            } else {
                int temp = (a.getName().length() + 2);
                format += "%-" + temp + "s|";
                len += temp + 1;
            }
        }
        // create dashed line
        dash = String.format("%0" + len + "d", 0).replace("0", "-");
        // add the header to result
        result = dash + "\n" + String.format(format, headers) + "\n" + dash;

        // specific columns from all the records
        for (Record r : records) {
            ArrayList<Object> entries = new ArrayList<>();
            for (int x = 0; x < tableAttributes.size(); x++) {
                entries.add(r.getValueAtColumn(x));
            }
            result += "\n" + String.format(format, entries.toArray());
        }

        // bottom line
        result += "\n" + dash;
        return result;
    }
}
