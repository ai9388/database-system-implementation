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
