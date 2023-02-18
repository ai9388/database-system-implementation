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

    public byte[] convertIntToByteArray(int i) 
    {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    public int convertByteArrayToInt(byte[] bytes) 
    {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public byte convertBooleanToByte(boolean bool) 
    {
        return (byte) (bool ? 1 : 0);
    }

    public boolean convertByteToBoolean(byte b) 
    {
        return b != 0;
    }

    public byte[] convertDoubleToByteArray(double d) 
    {
        return ByteBuffer.allocate(8).putDouble(d).array();
    }

    public byte[] convertCharToByteArray(char c) 
    {
        return ByteBuffer.allocate(8).putChar(c).array();
    }

    public byte[] convertStringToByteArray(String st)
    {
        byte[] bb = new byte[st.length()];

        char[] ch = st.toCharArray();

        for (char c : ch) {
            bb = concat(bb, convertCharToByteArray(c));
        }
        return bb;
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

        // get num of pages, placeholder of 1 for now
        byte[] pageNum = convertIntToByteArray(1);
        for (int i = 0; i < pageNum.length; i++) {
            bytes[i] = pageNum[i];
        }

        // getting 
        byte[] page = convertIntToByteArray(this.pageSize);
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

    public byte[] concat(byte[]... arrays) {
        // Determine the length of the result array
        int totalLength = 0;
        for (int i = 0; i < arrays.length; i++) 
        {
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
