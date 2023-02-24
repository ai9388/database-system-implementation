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
            t.insertRecord(record);
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
    public byte convertBooleanToByte(boolean bool) {
        return (byte) (bool ? 1 : 0);
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
    public static ArrayList<Object> convertBytesToObjects(ArrayList<String> attributes, byte[] bytes)
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

}
