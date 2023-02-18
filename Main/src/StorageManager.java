import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


//TODO: need to implement a page buffer
//not write into the file until the user said quit


public class StorageManager {

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

}
