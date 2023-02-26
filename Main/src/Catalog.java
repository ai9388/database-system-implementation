import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Catalog {

    public static final int INTEGER = 0;
    public static final int DOUBLE = 1;
    public static final int BOOLEAN = 2;
    public static final int CHAR = 3;
    public static final int VARCHAR = 4;

    public static final String READ = "r";
    public static final String WRITE = "rw";
    
    private String path;
    public RandomAccessFile raf;

    public Catalog(String path)
    {
        this.path = path;
    }
        

    /**
     * Catalog is formatted as
     * 4 Bytes - number of tables
     * 4 Bytes - number of pages that the catalog has
     * 4 Bytes - page number of most recently used page
     * 
     * @return
     */
    public byte[] createCatalog()
    {   
        // adding in the header for the file
        byte[] bytes = new byte[0];
        //byte[] pageSize = Type.convertIntToByteArray(this.pageSize);
        byte[] numOfPages = Type.convertIntToByteArray(1);
        byte[] lup = Type.convertIntToByteArray(1);


        return bytes;
    }

    /**
     * updating the number of pages in the catalog
     */
    public void updateNumberOfPages(int newNumOfPages)
    {
        try {
            raf.seek(4);
            raf.writeInt(newNumOfPages);
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLastUsedPageNumber(int lupn) 
    {
        try {
            raf.seek(8);
            raf.writeInt(lupn);
            raf.close();
        } catch (IOException e) {
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
