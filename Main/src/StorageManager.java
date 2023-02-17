import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


//TODO: need to implement a page buffer
//not write into the file until the user said quit


public class StorageManager {

    public Record getRecordFromPrimaryKey(String key){
        //get the record from the table
        return null;
    }
    
    public Page getPage(Table table, int pageNumber){

        return null;
    }

    public ArrayList<Record> getAllRecords(int tableNumber){
        return null;
    }

    public void insertRecordIntoTable(Table table){

    }

    public void deleteRecord(String primaryKey, Table table){

    }

    public void updateRecord(String primaryKey, Table table){

    }

    public String byteToString(){
        return null;
    }
    
    public void LRU(){

    }

    public void rewrite(){

    }

    public static void main(String[] args) {
        lookForDatabase("/Users/hai-yennguyen/Desktop/RIT/database-system-implementation/Main/src/test", 4096, 10);   
    }



}
