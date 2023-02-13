import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Catalog {
    
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

        File file = new File(this.path + "Catalog.txt");
        String content = "";

        content += "DB Location: " + this.path + "\n";
        content += "Page size: " + this.pageSize + "\n";
        content += "Buffer size: " + this.bufferSize + "\n";

        try {
            FileWriter myWriter = new FileWriter(file.getPath());
            myWriter.write(content);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("oh no...");
            System.out.println(file.getAbsolutePath().toString());
            e.printStackTrace();
        }
        
        //byte[] bytes = new byte[size];

    }
        
}
