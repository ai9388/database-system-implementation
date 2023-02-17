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

    public byte[] convertIntToByteArray(int v) 
    {
        return ByteBuffer.allocate(4).putInt(v).array();
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
        String content = "";

        content += "DB Location: " + this.path + "\n";
        content += "Page size: " + this.pageSize + "\n";
        content += "Buffer size: " + this.bufferSize + "\n";

        try {
            File file = new File(this.path + "Catalog");
            RandomAccessFile raf = new RandomAccessFile(file, WRITE);

            raf.write(convertStringToByteArray("something"));

            raf.close();
        } catch (IOException e) {
            System.out.println("oh no...");
            e.printStackTrace();
        }
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
