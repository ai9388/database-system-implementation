import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.*;

public class Catalog {

    public static final String READ = "r";
    public static final String WRITE = "rw";
    
    private String path;
    private ArrayList<Attribute> attributes;
    private int pageSize;
    private int bufferSize;

    public Catalog(String path, ArrayList<Attribute> attributes, int pageSize, int bufferSize)
    {
        this.path = path;
        this.attributes = attributes;
        this.pageSize = pageSize;
        this.bufferSize = bufferSize;
    }

    public void writeToFile()
    {
        // allocate space for the page size and table number
        int catalog_size = 8;
        System.out.println(attributes.size());

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

        StorageManager sm = new StorageManager();

        // get num of pages, placeholder of 1 for now
        byte[] pageNum = sm.convertIntToByteArray(1);
        for (int i = 0; i < pageNum.length; i++) {
            bytes[i] = pageNum[i];
        }

        // getting the page size in the schema
        byte[] page = sm.convertIntToByteArray(this.pageSize);
        for (int i = 0; i < page.length; i++) {
            bytes[i + 4] = page[i];
        }

        try {
            File file = new File(this.path + "Catalog");
            RandomAccessFile raf = new RandomAccessFile(file, WRITE);

            raf.write(bytes);

            System.out.println(Arrays.toString(bytes));

            raf.close();
        } catch (IOException e) {
            System.out.println("oh no...");
            e.printStackTrace();
        }
    }
}
