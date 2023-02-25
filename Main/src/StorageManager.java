import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.*;

import javax.swing.text.DefaultStyledDocument.ElementSpec;
import javax.xml.crypto.Data;


//TODO: need to implement a page buffer
//not write into the file until the user said quit

//creating new database in the main and pass it in the storage manager


public class StorageManager {
    private Database db;
    private int bufferSize;
    public ArrayList<Page> pageBuffer = new ArrayList<>();

    public StorageManager(Database database, int bufferSize){
        this.db = database;
        this.bufferSize = bufferSize;
    }

    public Database getDb() {
        return db;
    }


    public Table getTable(String table_name) {
        // TODO: for parser be able to get the table with the given table name
        return db.getSingleTable(table_name);
    }

    public void insertRecords(String tableName, ArrayList<Record> records) {
        // TODO: insert records into given table from parser
        Table t = null;
        for (Record record: records) {
            t = getTable(tableName);
            t.insertRecord(record);
        }
        // TODO: write table out to storage?
    }

    public void addTable(Table table) {
        // This is used in Parser
        // TODO: write table to the database?
        db.addTable(table);
    }

    public void displaySchema() {
        ArrayList<Table> tables = db.getAllTables();
        for (Table t : tables)
        {
            if (t != null) {
                System.out.println("Table name: " + t.getName());
                System.out.println("Table Schema: ");
                for (Attribute a : t.getAttributes())
                {
                    System.out.println(a.toString());
                }
                //System.out.println("Pages: " + t.getNumberOfPages());
                System.out.println("Records: " + t.getNumberOfRecords());
            }
        }   
    }

    public void displayInfo(String table_name) {
        //displaySchema(table_name);
        System.out.println("Number of pages: "); // Print # of pages
        System.out.println("Number of records: "); // Print # of records
    }

    public boolean hasTable() {
        // TODO: check if there are any tables in the database for parser
        return true;
    }

    public Record getRecordFromPrimaryKey(Table table, String key){
        //get the record from the table
        // return table.getRecordsPK(key);
        return null;
    }
    
    public void getPageByTablePNumber(Table table, int pageNumber){
        //TODO: change void back to Page
        // return table.getPagebyPNum(pageNumber);
    }

    public ArrayList<Record> getAllRecords(int tableNumber){
        return null;
    }

    public void insertOneRecordIntoTable(Table table, String[] record){
        table.insertRecord(record);
    }

    public void deleteRecord(String primaryKey, Table table){
        table.removeRecordByPK(primaryKey);
    }

    public void updateRecord(String primaryKey, Table table,  String column, String newEntry){
        table.updateRecordByPK(primaryKey, column, newEntry);
    }

    public String byteToString(){
        return null;
    }
    
    public void LRU(Page page){
        //old -> new
        //adding into the linkedlist and if it full, pop the first one (the oldest one)
        if(pageBuffer.size() > bufferSize){
            pageBuffer.remove(0);
            pageBuffer.add(page);
        }
        else{
            pageBuffer.add(page);
        }
    }

    public void rewrite(){

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
