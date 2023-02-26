import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Catalog {

    public static final int INTEGER = 0;
    public static final int DOUBLE = 1;
    public static final int BOOLEAN = 2;
    public static final int CHAR = 3;
    public static final int VARCHAR = 4;

    public static final String READ = "r";
    public static final String WRITE = "rw";
    
    private String path;
    private ArrayList<Table> tables;
    public RandomAccessFile raf;

    public Catalog(String path)
    {
        this.path = path;
        System.out.println(this.path);
    }

    public void createTableObjects(byte[] bb)
    {
        try {
            RandomAccessFile raFile = new RandomAccessFile(new File(this.path), READ);
            raFile.seek(4);

            int lengthOfTable = raFile.readInt();

            
            
        } catch (IOException e) {
            System.out.println("File doesnt exist.");
            e.printStackTrace();
        }
    }

    public void createTableFromBytes(byte[] bb)
    {
        
    }

    /**
     * setting the tables for the catalog
     * @param tables
     */
    public void setTables(ArrayList<Table> tables)
    {
        this.tables = tables;
    }

    /**
     * reading an existing catalog
     * @return
     */
    public byte[] readCatalog()
    {
        byte[] bb = new byte[0];
        try {
            bb = Files.readAllBytes(Paths.get(this.path));
            System.out.println(bb);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bb;
    }

    /**
     * Catalog is formatted as
     * 4 Bytes - number of tables in the database
     * 
     * @return
     */
    public byte[] createCatalog()
    {   
        // adding in the header for the file
        byte[] bytes = new byte[0];
        byte[] numOfTables = Type.convertIntToByteArray(this.tables.size());

        bytes = Type.concat(bytes, numOfTables);

        for (Table t : this.tables)
        {
            bytes = Type.concat(bytes, t.convertTableObjectToBytes());
        }

        return bytes;
    }

    /**
     * updating the number of pages in the catalog
     */
    public void updateNumberOfTables(int newNumOfTables)
    {
        try {
            raf.seek(0);
            raf.writeInt(newNumOfTables);
            raf.close();
        } catch (IOException e) {
            System.out.println("Cannot update number of tables.");
            e.printStackTrace();
        }
    }

    public void writeToFile(byte[] bytes)
    {  
        try {
            File file = new File(this.path + "Catalog");
            raf = new RandomAccessFile(file, WRITE);

            raf.write(bytes);

            raf.close();
        } catch (IOException e) 
        {
            System.out.println("Couldn't write catalog to file.");
            e.printStackTrace();
        }
    }
}
