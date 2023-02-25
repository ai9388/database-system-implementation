import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class Catalog {

    public static final String READ = "r";
    public static final String WRITE = "rw";
    
    private String path;
    private ArrayList<Attribute> attributes;
    private int pageSize;
    private int bufferSize;
    public RandomAccessFile raf;

    public Catalog(String path, ArrayList<Attribute> attributes, int pageSize, int bufferSize)
    {
        this.path = path;
        this.attributes = attributes;
        this.pageSize = pageSize;
        this.bufferSize = bufferSize;
    }

    /**
     * helper method for later use
     */
    public ArrayList<String> getStringAttributes()
    {
        ArrayList<String> strings = new ArrayList<String>();
        
        for (int i = 0; i < attributes.size(); i++) 
        {
            strings.add(attributes.get(i).getType().toString());
        }

        return strings;
    }
        

    /**
     * Catalog is formatted as
     * 4 Bytes - page size (taken from user)
     * 4 Bytes - number of pages that the catalog has
     * 4 Bytes - page number of most recently used page
     * 
     * @return
     */
    public byte[] createCatalog()
    {
        // allocate space for the page size and table number
        int catalog_size = 12;

        for (Attribute attr : attributes) 
        {
            switch (attr.getType()) 
            {
                case BOOLEAN:
                    catalog_size += 1;
                    break;
                case CHAR:
                    catalog_size += (attr.getN() * Integer.BYTES);
                    break;
                case DOUBLE:
                    catalog_size += Double.BYTES;
                    break;
                case INTEGER:
                    catalog_size += Integer.BYTES;
                    break;
                case VARCHAR:
                    catalog_size += (attr.getN() * Integer.BYTES);
                    break;
                default:
                    break;
            }
        }

        byte[] bytes = new byte[catalog_size];
        
        // getting the page size in the schema
        byte[] page = Type.convertIntToByteArray(this.pageSize);
        for (int i = 0; i < page.length; i++) 
        {
            bytes[i] = page[i];
        }

        // get num of pages, placeholder of 1 for now
        byte[] numOfPages = Type.convertIntToByteArray(1);
        for (int i = 0; i < numOfPages.length; i++) 
        {
            bytes[i + 4] = numOfPages[i];
        }

        // getting the last used page id
        byte[] lup = Type.convertIntToByteArray(1);
        for (int i = 0; i < page.length; i++) 
        {
            bytes[i + 8] = lup[i];
        }

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

    public void updateLastUsedPageNumber(int lupn) {
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
            System.out.println("oh no...");
            e.printStackTrace();
        }
    }

    public void displayCatalogInfo()
    {

    }
}
