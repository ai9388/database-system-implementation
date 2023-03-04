import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Catalog {

    public static final int INTEGER = 0;
    public static final int DOUBLE = 1;
    public static final int BOOLEAN = 2;
    public static final int CHAR = 3;
    public static final int VARCHAR = 4;

    public static final String READ = "r";
    public static final String WRITE = "rw";
    
    private String path;
    private int pageSize;
    private ArrayList<Table> tables;
    public RandomAccessFile raf;

    public Catalog(String path, int pageSize)
    {
        this.path = path;
        this.pageSize = pageSize;
    }

    public void readCatalog()
    {
        try {
            String catalogPath = this.path;
            if (path.contains("\\")) {
                catalogPath += "\\Catalog";
            } else {
                catalogPath += "/Catalog";
            }

            File file = new File(catalogPath);

            RandomAccessFile raFile = new RandomAccessFile(file, READ);
            raFile.seek(0);
            int numOfTables = raFile.readInt();

            for (int i = 0; i < numOfTables; i++) {
                Table t = createTableFromBytes(raFile);
                //readTableFromBytes(t.getName());
            }

        } catch (IOException e) {
            System.out.println("File doesnt exist.");
            e.printStackTrace();
        }
    }

    // public Table readTableFromBytes(String tableName)
    // {
    //     try {
    //         String tablePath = this.path;
    //         if (path.contains("\\")) {
    //             tablePath += "\\" + tableName;
    //         } else {
    //             tablePath += "/" + tableName;
    //         }

    //         File file = new File(tablePath);

    //         RandomAccessFile raFile = new RandomAccessFile(file, READ);
    //         raFile.seek(0);
    //         int numOfTables = raFile.readInt();

    //         for (int i = 0; i < numOfTables; i++) {
    //             createTableFromBytes(raFile);
    //         }

    //     } catch (IOException e) {
    //         System.out.println("File doesnt exist.");
    //         e.printStackTrace();
    //     }
    // }

    public ArrayList<Page> readPagesFromTableFile(RandomAccessFile raFile, ArrayList<Attribute> attributes)
    {
        ArrayList<Page> pages = new ArrayList<>();

        // first getting the number of pages from table file
        try {
            int numOfPages = raFile.readInt();

            // iterating over all the pages in the file
            for (int i = 0; i < numOfPages; i++) 
            {
                int traversedBytes = 8;
                int pageID = raFile.readInt();
                int numberOfRecords = raFile.readInt();
                ArrayList<Record> records = new ArrayList<>();

                // iterating over the individual records
                for (int j = 0; j < numberOfRecords; j++) 
                {
                    ArrayList<Object> recordData = new ArrayList<>();
                    
                    for (int k = 0; k < attributes.size(); k++) 
                    {
                        switch(attributes.get(k).getType())
                        {
                            case BOOLEAN:
                                boolean b = raFile.readBoolean();
                                traversedBytes += 1;
                                recordData.add(b);
                                break;
                            case CHAR:
                                int n = attributes.get(k).getN();
                                char[] ch = new char[n];

                                for (int l = 0; l < n; l++) {
                                    char c = raFile.readChar();
                                    ch[l] = c;
                                }
                                traversedBytes += (Character.BYTES * n);
                                recordData.add(ch.toString());
                                break;
                            case DOUBLE:
                                double d = raFile.readDouble();
                                traversedBytes += Double.BYTES;
                                recordData.add(d);
                                break;
                            case INTEGER:
                                int in = raFile.readInt();
                                traversedBytes += Integer.BYTES;
                                recordData.add(in);
                                break;
                            case VARCHAR:
                                int vn = attributes.get(k).getN();
                                char[] vch = new char[vn];

                                for (int l = 0; l < vn; l++) {
                                    char c = raFile.readChar();
                                    vch[l] = c;
                                }
                                traversedBytes += (Character.BYTES * vn);
                                recordData.add(vch.toString());    
                            break;
                            default:
                            // program kills itself
                                break;
                        }
                    }
                    records.add(new Record(recordData));
                }
                Page page = new Page(pageID, records);
                pages.add(page);

                raFile.seek(pageSize - traversedBytes);
            }
        } catch (IOException e) {
            System.out.println();
            e.printStackTrace();
        }
        return pages;
    }

    public Table createTableFromBytes(RandomAccessFile f)
    {        
        try {
            // getting the table name
            int tableNameLength = f.readInt();

            char[] tableNameChars = new char[tableNameLength];

            for (int i = 0; i < tableNameLength; i++) 
            {   
                tableNameChars[i] = f.readChar();
            }
            String tableName = new String(tableNameChars);

            // getting the attributes from the bytes
            int numberOfAttributes = f.readInt();

            ArrayList<Attribute> attributes = new ArrayList<Attribute>();

            for (int i = 0; i < numberOfAttributes; i++) 
            {
                int attributeNameLength = f.readInt();

                char[] attributeNameChars = new char[attributeNameLength];

                for (int j = 0; j < attributeNameLength; j++) 
                {
                    attributeNameChars[j] = f.readChar();
                }

                String attributeName = new String(attributeNameChars);

                int attributeTypeInt = f.readInt();
                
                Type attributeType;

                switch(attributeTypeInt)
                {
                    case Catalog.INTEGER ->
                    {
                        attributeType = Type.INTEGER;
                        break;
                    }
                    case Catalog.DOUBLE ->
                    {
                        attributeType = Type.DOUBLE;
                        break;
                    }
                    case Catalog.BOOLEAN ->
                    {
                        attributeType = Type.BOOLEAN;
                        break;
                    }
                    case Catalog.CHAR ->
                    {
                        attributeType = Type.CHAR;
                        break;
                    }
                    case Catalog.VARCHAR ->
                    {
                        attributeType = Type.VARCHAR;
                        break;
                    }
                    default ->
                    {
                        attributeType = Type.INTEGER;
                        break;
                    }
                }

                int attributeN = f.readInt();
                
                boolean attributeIsPrimaryKey = f.readBoolean();

                Attribute attr = new Attribute(attributeName, attributeType, attributeIsPrimaryKey, attributeN);

                attributes.add(attr);
            }
            return new Table(tableName, attributes);
        } catch (IOException e) 
        {
            e.printStackTrace();
        }
    
        return new Table(null, null);
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
     * Catalog is formatted as
     * 4 Bytes - number of tables in the database
     * 
     * @return
     */
    public byte[] createCatalog()
    {   
        // adding in the header for the file
        byte[] bytes = new byte[0];
        byte[] numOfTables = Type.convertIntToByteArray(this.tables == null ? 0 : tables.size());

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
            String catalogPath = path;
            if(path.contains("\\")){
                catalogPath += "\\Catalog";
            }
            else{
                catalogPath += "/Catalog";
            }

            File file = new File(catalogPath);
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
