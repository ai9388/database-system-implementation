import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class StorageManager {

    private Table table;

    public static Table accessTable(String path, int pageSize, int bufferSize, String tableName){
        //look for the database from the path and get the table from the database
        // Path dire = Paths.get(path);
        File directory = new File(path);
        Database db = null;
        
        if(directory.exists()){
            if(directory.length() > 0){
                System.out.println("db exists");
            }
            else{
                System.out.println("No existing db found");
                System.out.println("Creating new db at" + path);
                directory.mkdir();
                db = new Database(path, null, null, path);
            }
        }
        else{
            System.out.println("No existing db found");
            System.out.println("Creating new db at" + path);
            directory.mkdir();
            db = new Database(path, null, null, path);
            //need to add a catalog into the folder.
        }

        return db.getSingleTable(path);
    }


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
    
    public static void main(String[] args) {
        lookForDatabase("/Users/hai-yennguyen/Desktop/RIT/database-system-implementation/Main/src/test", 4096, 10);   
    }



}
