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

    // empty constructor
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
                System.out.println("Pages: ");
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
     * converts int to a byte array
     * @param i integer we want to change
     * @return byte array
     */
    public byte[] convertIntToByteArray(int i) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(i).array();
    }

    /**
     * convert boolean to byte
     * @param bool boolean we wnat to change
     * @return byte
     */
    public byte[] convertBooleanToByteArray(boolean bool) {
        return ByteBuffer.allocate(1).put((byte) (bool ? 1 : 0)).array();
    }

    /**
     * converts double to a byte array
     * @param d double we want to change
     * @return byte array
     */
    public byte[] convertDoubleToByteArray(double d) {
        return ByteBuffer.allocate(Double.BYTES).putDouble(d).array();
    }

    /**
     * converts char to a byte array
     * @param c char we want to change
     * @return byte array
     */
    public byte[] convertCharToByteArray(char c) {
        return ByteBuffer.allocate(Character.BYTES).putChar(c).array();
    }

    /**
     * converts string to a byte array
     * 
     * @param st string we want to change
     * @return byte array
     */
    public byte[] convertStringToByteArray(String st) {
        byte[] bb = new byte[st.length()];

        char[] ch = st.toCharArray();

        for (char c : ch) {
            bb = concat(bb, convertCharToByteArray(c));
        }
        return bb;
    }

    /**
     * turning the bytes into a string that we can use for records later
     * @param attributes string version of all of the schema's attributes
     * @param bytes bytes we want to change
     * @return concatenated string seprated by spaces(?)
     */
    public ArrayList<Object> convertBytesToObjects(ArrayList<String> attributes, byte[] bytes)
    {
        ArrayList<Object> result = new ArrayList<Object>();
        for (int i = 0; i < attributes.size(); i++) {
            switch (attributes.get(i))
            {
                case "int" -> {
                    result.add(ByteBuffer.wrap(bytes).getInt());
                }
                case "bool" -> {
                    result.add(ByteBuffer.wrap(bytes).get());
                }
                case "double" -> {
                    result.add(ByteBuffer.wrap(bytes).getDouble());
                }
                case "char" -> {
                    result.add(ByteBuffer.wrap(bytes).getChar());
                }
                case "varchar" -> {
                    result.add(ByteBuffer.wrap(bytes).getChar());
                }
            }
        }

        return result;
    }

    /**
     * helper method to concatenate multiple byte arrays
     * @param arrays any N number of byte[]
     * @return concated cyte[]
     */
    public byte[] concat(byte[]... arrays) {
        // Determine the length of the result array
        int totalLength = 0;
        for (int i = 0; i < arrays.length; i++) {
            totalLength += arrays[i].length;
        }

        // create the result array
        byte[] result = new byte[totalLength];

        // copy the source arrays into the result array
        int currentIndex = 0;
        for (int i = 0; i < arrays.length; i++) {
            System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
            currentIndex += arrays[i].length;
        }

        return result;
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
                concat(bytes, convertIntToByteArray(fileID));
            }

            // writing the number of pages
            for (int i = Integer.BYTES; i < 2 * Integer.BYTES; i++) {
                concat(bytes, convertIntToByteArray(numOfPages));
            }

            // writing the number of records
            for (int i = 2 * Integer.BYTES; i < 3 * Integer.BYTES; i++) {
                concat(bytes, convertIntToByteArray(numOfRecords));
            }

            raf.write(bytes);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
