import java.beans.VetoableChangeSupport;
import java.io.File;
import java.io.RandomAccessFile;
import java.security.cert.CertPath;
import java.util.*;

public class Parser {
     enum commands {
        CREATE_TABLE, DISPLAY_SCHEMA, DISPLAY_INFO, SELECT, INSERT, HELP, QUIT, EMPTY
    }

    private String user_input;
    private commands command;
    private String dbLocation;
    private int pageSize;
    private int bufferSize;
    public Database database;
    public StorageManager storageManager;
    public String dbName;

    public Parser(String dbName){
        // TODO: hai-yen
        this.dbName = dbName;
        storageManager = new StorageManager(dbName, dbLocation, bufferSize);
    }

    public void clasifyInput(String str_input) {

        this.user_input = str_input;
        this.user_input = str_input.toLowerCase();
        if (user_input.startsWith("create table")) {
            command = commands.CREATE_TABLE;
        } else if (user_input.startsWith("display schema")) {
            command = commands.DISPLAY_SCHEMA;
        } else if (user_input.startsWith("display info")) {
            command = commands.DISPLAY_INFO;
        } else if (user_input.startsWith("select")) {
            command = commands.SELECT;
        } else if (user_input.startsWith("insert into")) {
            command = commands.INSERT;
        } else if (user_input.startsWith("help")) {
            command = commands.HELP;
        } else if (user_input.startsWith("quit")) {
            command = commands.QUIT;
        } else {
            // System.out.println("Invalid Command.");
            command = commands.EMPTY;
        }
    }

    /**
     * Args need to be saved to pass into catalog
     */
    public void saveArgs(String[] args)
    {
        this.dbLocation = args[0];
        
        this.pageSize = Integer.parseInt(args[1]);
        // set the page size
        Page.setCapacity(pageSize);
        this.bufferSize = Integer.parseInt(args[2]);
        // TODO: set the buffer size
    }

    /**
     * Assume user passes in database
     * @throws PrimaryKeyException
     */
    public void parse() throws PrimaryKeyException {
        try{
            switch (command) {
            case CREATE_TABLE -> {
                String input = user_input.replaceFirst("create table", "").strip();
                int start_index = input.indexOf("(");
                int end_index = input.indexOf(");");
                String table_name = input.substring(0, start_index);
                String columns = input.substring(start_index + 1, end_index).strip();
                String[] attr = columns.split(",");
                ArrayList<Attribute> attributes = new ArrayList<>();
                Attribute primaryAttribute = null;
                int primaryIndex = 0;
    
                boolean hasOnePK = false;
                for (String attribute : attr) {
                    String[] components = attribute.strip().replaceAll("\\(", " ").split(" ");
                    String attr_name = components[0];
                    boolean primarykey = components.length > 2 && components[2].equals("primarykey");
                    switch (components[1]) {
                        case "char" -> {
                            // originally had A XOR B, should be !(A XOR B)
                            hasOnePK = ((components.length > 3 && !hasOnePK) || (components.length <= 3 && hasOnePK));
                            Attribute a = new Attribute(attr_name, Type.CHAR, components.length > 3, Integer.parseInt(components[2].replace(')', ' ').strip()));
                            primaryAttribute = a.isIsPrimaryKey() ? a : primaryAttribute;
                            primaryIndex = a.isIsPrimaryKey() ? primaryIndex : primaryIndex + 1;
                            attributes.add(a);
                            //check component after char to know length
                        }
                        case "varchar" -> {
                            hasOnePK = ((components.length > 3 && !hasOnePK) || (components.length <= 3 && hasOnePK));
                            Attribute a = new Attribute(components[1], Type.VARCHAR, components.length > 3, Integer.parseInt(components[2].replace(')', ' ').strip()));
                            primaryAttribute = a.isIsPrimaryKey() ? a : primaryAttribute;
                            primaryIndex = a.isIsPrimaryKey() ? primaryIndex : primaryIndex + 1;
                            attributes.add(a);
                        }
                        case "bool" -> {
                            hasOnePK = !((primarykey && !hasOnePK) || (!primarykey && hasOnePK));
                            Attribute a = new Attribute(attr_name, Type.BOOLEAN, primarykey, 0);
                            primaryAttribute = a.isIsPrimaryKey() ? a : primaryAttribute;
                            primaryIndex = a.isIsPrimaryKey() ? primaryIndex : primaryIndex + 1;
                            attributes.add(a);
                        }
                        case "integer" -> {
                            hasOnePK = !((primarykey && !hasOnePK) || (!primarykey && hasOnePK));
                            Attribute a = new Attribute(attr_name, Type.INTEGER, primarykey, 0);
                            primaryAttribute = a.isIsPrimaryKey() ? a : primaryAttribute;
                            primaryIndex = a.isIsPrimaryKey() ? primaryIndex : primaryIndex + 1;
                            attributes.add(a);
                        }
                        case "double" -> {
                            hasOnePK = !((primarykey && !hasOnePK) || (!primarykey && hasOnePK));
                            Attribute a = new Attribute(attr_name, Type.DOUBLE, primarykey, 0);
                            primaryAttribute = a.isIsPrimaryKey() ? a : primaryAttribute;
                            primaryIndex = a.isIsPrimaryKey() ? primaryIndex : primaryIndex + 1;
                            attributes.add(a);
                        }
                        default -> {
                            System.out.println("ERROR!");
                            System.out.println("Invalid data type: " + components[1]);
                        }
                    }
                }
                if (!hasOnePK) {
                    System.out.println("ERROR!");
                    System.out.println("No primary key defined.");
                } else {
                    System.out.println(attributes.toString());
                    try{

                        //TODO: the parser should call createTable from the storage manager and it will create the table and add table
                        // Table table = new Table(table_name, attributes);
                        storageManager.createTable(table_name, attributes);
                        // storageManager.addTable(table);
                        // File new_table = new File(dbLocation + table_name);
                        // storageManager.addIntialInfoToTable(new_table, 0, 0, 0);

                        System.out.println("SUCCESS! You've created " + table_name);
                    }
                    catch(Exception pke){
                        System.out.println(pke.getMessage());
                    }
                    // testing byte array stuff
                    Catalog c = new Catalog(this.dbLocation, attributes, this.pageSize, this.bufferSize);
                    byte[] catalogAsBytes = c.createCatalog();
                    c.writeToFile(catalogAsBytes);
                }
            }
            case DISPLAY_SCHEMA -> storageManager.displaySchema();
            case DISPLAY_INFO -> {
                String table_name = user_input.replaceFirst("display info", "").strip();
                storageManager.displayInfo(table_name);
            }
            case SELECT -> {
                String input = user_input.replaceFirst("select", "").strip();
                int start_index = input.indexOf("from");
                int end_index = input.indexOf("m", start_index);
                String table_name = input.substring(end_index + 1).replaceAll(";", "").strip();
                select("*", table_name); // This is good for now because we are not selecting by columns just yet
            }
            case INSERT -> {
                String input = user_input.replaceFirst("insert into", "").strip();
                String table_name = input.split(" ")[0];
                // ArrayList<Record> table_values = new ArrayList<>();
                int start_index = input.indexOf("(");
                input = input.substring(start_index - 1);
                String[] vals = input.split(",");
                // Table table = storageManager.getTable(table_name);
                // if(table == null) {
                //     System.out.println("ERROR! Table " + table_name + "does not exist.");
                //     command = commands.QUIT;
                //     break;
                // }
                for (String value : vals) {
                    String[] values = value.replaceAll("[();]", "").strip().split(" ");
                    storageManager.insertOneRecordIntoTable(table_name, values);
                }
            }
            case HELP -> displayHelp();
            case QUIT -> {
                System.out.println("Shutting down 11QL...");
                System.out.println("Shutting down the database...");
                System.out.println("Purging the page buffer...");
                System.out.println("Saving catalog...");
                System.out.println("\nExiting the database...");
            }
        }
        }
        catch(TableException e){
            System.out.println(e.getMessage());
        }
        catch(InvalidDataTypeException e){
            System.out.println(e.getMessage());
        }
        catch(PrimaryKeyException e){
            System.out.println(e.getMessage());
        }
    }

    private void select(String attr, String tableName) throws TableException {
        if (attr.equals("*")) {
            System.out.println(storageManager.selectFromTable(tableName, null));
        }
        else {
            String[] columns = attr.strip().split(",");
            System.out.println(storageManager.selectFromTable(tableName, columns));
            }
    }

    public void displayHelp() {
        System.out.println("\n To run 11QL, use");
        System.out.println("java Main <db loc> <page size> <buffer size>");
        System.out.println("Available functions are:");
        System.out.println("\tdisplay schema");
        System.out.println("\tdisplay table <table name>");
        System.out.println("\tselect * from <table name>");
        System.out.println("\tinsert into <table name> values");
        System.out.println("\tcreate table <table name> (<values>)");
    }
}
