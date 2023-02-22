import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


//TODO: need to implement a page buffer
//not write into the file until the user said quit


public class StorageManager {

    // empty constructor
    public StorageManager(){}

    public static Table getTable(String table_name) {
        // TODO: for parser be able to get the table with the given table name
        return null;
    }

    public static void insertRecords(String tableName, ArrayList<Record> records) {
        // TODO: insert records into given table from parser
        Table t = null;
        for (Record record: records) {
            t = getTable(tableName);
            t = t != null ? t.insertRecord(record): null;
        }
        // TODO: write table out to storage?
    }

    public static void addTable(Table table) {
        // This is used in Parser
        // TODO: write table to the database?
    }

    public static void displaySchema(String table_name) {
        // Used in parser...
        // TODO: get the schema from the table?
        Table t = getTable(table_name);
        if (t!= null) {
            System.out.println("Table name: " + table_name);
            System.out.println("Table Schema: ");
        }
    }

    public static void displayInfo(String table_name) {
        displaySchema(table_name);
        System.out.println("Number of pages: "); // Print # of pages
        System.out.println("Number of records: "); // Print # of records
    }

    public static boolean hasTable() {
        // TODO: check if there are any tables in the database for parser
        return true;
    }

    public Record getRecordFromPrimaryKey(Table table, String key){
        //get the record from the table
        // return table.getRecordsPK(key);
        return null;
    }
    
    public Page getPageByPNumber(Database db, Table table, int pageNumber){

        return null;
    }

    public ArrayList<Record> getAllRecords(int tableNumber){
        return null;
    }

    public void insertRecordIntoTable(Table table, String[] record){
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
    
    public void LRU(){

    }

    public void rewrite(){

    }

    public byte[] convertIntToByteArray(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    public int convertByteArrayToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public byte convertBooleanToByte(boolean bool) {
        return (byte) (bool ? 1 : 0);
    }

    public boolean convertByteToBoolean(byte b) {
        return b != 0;
    }

    public byte[] convertDoubleToByteArray(double d) {
        return ByteBuffer.allocate(8).putDouble(d).array();
    }

    public byte[] convertCharToByteArray(char c) {
        return ByteBuffer.allocate(8).putChar(c).array();
    }

    public byte[] convertStringToByteArray(String st) {
        byte[] bb = new byte[st.length()];

        char[] ch = st.toCharArray();

        for (char c : ch) {
            bb = concat(bb, convertCharToByteArray(c));
        }
        return bb;
    }

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

}
