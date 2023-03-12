import java.io.File;
import java.util.*;

import javax.sound.midi.Soundbank;

public class Parser {
    enum commands {
        CREATE_TABLE, DISPLAY_SCHEMA, DISPLAY_INFO, SELECT, INSERT, HELP, QUIT, DROP, ALTER, DELETE, UPDATE, EMPTY
    }

    private String user_input;
    private commands command;
    private String dbLocation;
    private int pageSize;
    private int bufferSize;
    public Database database;
    public StorageManager storageManager;
    public String dbName;

    public Parser(String dbName, String dbLocation, int pageSize, int bufferSize) {
        // TODO: hai-yen
        this.dbName = dbName;
        this.dbLocation = dbLocation;
        storageManager = new StorageManager(dbName, dbLocation, bufferSize, pageSize);
    }

    public void classifyInput(String str_input) {

        this.user_input = str_input.toLowerCase();
        if((user_input.charAt(user_input.length() -1 ) != ';') && !user_input.startsWith("quit") && !user_input.startsWith("help") ){
            command = commands.EMPTY;
        }
        else if (user_input.startsWith("create table")) {
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
        } else if (user_input.startsWith("drop table")) {
            command = commands.DROP;
        } else if (user_input.startsWith("alter table")) {
            command = commands.ALTER;
        } else if (user_input.startsWith("update")) {
            command = commands.UPDATE;
        } else if (user_input.startsWith("delete from")) {
            command = commands.DELETE;
        } else {
            // System.out.println("Invalid Command.");
            command = commands.EMPTY;
        }
    }

    /**
     * Assume user passes in database
     */
    public boolean parse() {
        try {
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
                        boolean primarykey = false;
                        boolean notNull = false;
                        boolean unique = false;
                        
                        try {
                            switch (components[1]) {
                                case "char" -> {
                                    for (int i = 3; i < components.length; i++) {
                                        switch (components[i]) {
                                            case "primarykey" -> {
                                                primarykey = true;
                                                notNull = true;
                                                unique = true;
                                            }
                                            case "notnull" -> notNull = true;
                                            case "unique" -> unique = true;
                                        }
                                    }
                                    Attribute a = new Attribute(attr_name, Type.CHAR, primarykey, notNull, unique,
                                            Integer.parseInt(components[2].replace(')', ' ').strip()));
                                    primaryAttribute = a.isIsPrimaryKey() ? a : primaryAttribute;
                                    primaryIndex = a.isIsPrimaryKey() ? primaryIndex : primaryIndex + 1;
                                    attributes.add(a);
                                }
                                case "varchar" -> {
                                    for (int i = 3; i < components.length; i++) {
                                        switch (components[i]) {
                                            case "primarykey" -> {
                                                primarykey = true;
                                                notNull = true;
                                                unique = true;
                                            }
                                            case "notnull" -> notNull = true;
                                            case "unique" -> unique = true;
                                        }
                                    }
                                    Attribute a = new Attribute(attr_name, Type.VARCHAR, primarykey, notNull, unique,
                                            Integer.parseInt(components[2].replace(')', ' ').strip()));
                                    primaryAttribute = a.isIsPrimaryKey() ? a : primaryAttribute;
                                    primaryIndex = a.isIsPrimaryKey() ? primaryIndex : primaryIndex + 1;
                                    attributes.add(a);
                                }
                                case "bool" -> {
                                    for (int i = 2; i < components.length; i++) {
                                        switch (components[i]) {
                                            case "primarykey" -> {
                                                primarykey = true;
                                                notNull = true;
                                                unique = true;
                                            }
                                            case "notnull" -> notNull = true;
                                            case "unique" -> unique = true;
                                        }
                                    }
                                    Attribute a = new Attribute(attr_name, Type.BOOLEAN, primarykey, notNull, unique,
                                            0);
                                    primaryAttribute = a.isIsPrimaryKey() ? a : primaryAttribute;
                                    primaryIndex = a.isIsPrimaryKey() ? primaryIndex : primaryIndex + 1;
                                    attributes.add(a);
                                }
                                case "integer" -> {
                                    for (int i = 2; i < components.length; i++) {
                                        switch (components[i]) {
                                            case "primarykey" -> {
                                                primarykey = true;
                                                notNull = true;
                                                unique = true;
                                            }
                                            case "notnull" -> notNull = true;
                                            case "unique" -> unique = true;
                                        }
                                    }
                                    Attribute a = new Attribute(attr_name, Type.INTEGER, primarykey, notNull, unique,
                                            0);
                                    primaryAttribute = a.isIsPrimaryKey() ? a : primaryAttribute;
                                    primaryIndex = a.isIsPrimaryKey() ? primaryIndex : primaryIndex + 1;
                                    attributes.add(a);
                                }
                                case "double" -> {
                                    for (int i = 2; i < components.length; i++) {
                                        switch (components[i]) {
                                            case "primarykey" -> {
                                                primarykey = true;
                                                notNull = true;
                                                unique = true;
                                            }
                                            case "notnull" -> notNull = true;
                                            case "unique" -> unique = true;
                                        }
                                    }
                                    Attribute a = new Attribute(attr_name, Type.DOUBLE, primarykey, notNull, unique, 0);
                                    primaryAttribute = a.isIsPrimaryKey() ? a : primaryAttribute;
                                    primaryIndex = a.isIsPrimaryKey() ? primaryIndex : primaryIndex + 1;
                                    attributes.add(a);
                                }
                                default -> {
                                    System.out.println("ERROR!");
                                    System.out.println("Invalid data type: " + components[1]);
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("Empty table");
                        }
                    }
                    hasOnePK = false;
                    for (Attribute attribute: attributes) {
                        if (hasOnePK && attribute.isIsPrimaryKey()) {
                            hasOnePK = !hasOnePK;
                            throw new PrimaryKeyException(3, "");
                        } else if (attribute.isIsPrimaryKey()) {
                            hasOnePK = true;
                        }
                    }
                    if (!hasOnePK) {
                        throw new PrimaryKeyException(1, " ");
                    } else {
                        try {
                            storageManager.createTable(table_name.strip(), attributes);
                            System.out.println("SUCCESS! You've created " + table_name);
                        } catch (Exception pke) {
                            System.out.println(pke.getMessage());
                        }
                    }
                }
                case DISPLAY_SCHEMA -> storageManager.displaySchema();           
                case DISPLAY_INFO -> {
                    String table_name = user_input.replaceFirst("display info", "").strip();
                    storageManager.displayTableInfo(table_name.substring(0, table_name.length() - 1));
                }
                case SELECT -> {
                    String input = user_input.replaceFirst("select", "").strip();
                    int start_index = input.indexOf("from");
                    int end_index = input.indexOf("m", start_index);
                    String table_name = input.substring(end_index + 1).replaceAll(";", "").strip();
                    select("*", table_name); // This is good for now because we are not selecting by columns just yet
                }
                case INSERT -> {
                    try {
                        ArrayList<String[]> arr = new ArrayList<>();
                        
                        String t_name = user_input.split("into")[1].split("values")[0].strip();
                        String[] values = user_input.split("values");
                        String[] tuples = values[1].split("\\(");

                        for (int i = 1; i < tuples.length; i++) {
                            if(tuples[i].strip().equals("")){
                                continue;
                            }
                            String[] temp = tuples[i].split("\\)")[0].split(",");
                            for(int j = 0; j < temp.length; j++){
                                temp[j] = temp[j].strip();
                            }

                            arr.add(temp);
                        }

                        storageManager.insertRecords(t_name, arr);
                    } catch (PrimaryKeyException e) {
                        System.out.println(e.getMessage());
                    } catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("invalid query");
                    }
                    System.out.println("Record inserted successfully");
                }
                case HELP -> displayHelp();
                case QUIT -> {
                    storageManager.shutDown();
                    System.out.println("Shutting down 11QL...");
                    System.out.println("Shutting down the database...");
                    System.out.println("Purging the page buffer...");
                    System.out.println("Saving catalog...");
                    System.out.println("\nExiting the database...");

                    return false;
                }
                case DROP -> {
                    String input = user_input.replaceFirst("drop table", "").strip();
                    String table_name = input.split(";")[0];
                    if(storageManager.dropTable(table_name)){
                        System.out.println("Successfully dropped table " + table_name);
                    }
                    else{
                        System.out.println("Table " + table_name + " could not be removed");
                    }
                }
                case ALTER -> {
                    String input = user_input.replaceFirst("alter table", "").strip();
                    String table_name = input.split(" ")[0];
                    boolean drop = input.split(" ")[1].strip().equals("drop");
                    String attribute_name = input.split(" ")[2];
                    if (drop) {
                        attribute_name = attribute_name.split(";")[0];
                        storageManager.dropAttributeFromTable(attribute_name, table_name);
                    } else {
                        String attribute_type = input.split(" ")[3];
                        Attribute a;
                        if (attribute_type.startsWith("char")) {
                            a = new Attribute(attribute_name, Type.CHAR, false, false, false, Integer.parseInt(attribute_type.replaceAll("[char\\(\\);]", "")));
                        } else if (attribute_type.startsWith("varchar")) {
                            a = new Attribute(attribute_name, Type.VARCHAR, false, false, false, Integer.parseInt(attribute_type.replaceAll("[varchar\\(\\);]", "")));
                        } else if (attribute_type.startsWith("integer")){
                            a = new Attribute(attribute_name, Type.INTEGER, false,false, false, 0);
                        } else if (attribute_type.startsWith("bool")) {
                            a = new Attribute(attribute_name, Type.BOOLEAN, false, false, false, 0);
                        } else if (attribute_type.startsWith("double")) {
                            a = new Attribute(attribute_name, Type.DOUBLE, false, false, false, 0);
                        } else {
                            a = null;
                        }
                        if (input.split(" ").length > 5) {
                            String value = input.split("default")[1].strip();
                            value = value.split(";")[0];
                            // TODO: add functionality to storage manager to add attribute to table
                            storageManager.addAttributeToTable(a, value, table_name);
                        } else {
                            storageManager.addAttributeToTable(a, "", table_name);
                        }
                    }
                    System.out.println("Table " + table_name + " altered");
                }
                case DELETE -> {
                    //
                }
                case UPDATE -> {
                    //
                }
                case EMPTY -> {
                    System.out.println("Invalid queries...");
                }
            }
        }
        catch (TableException e) {
            System.out.println(e.getMessage());
        } catch (InvalidDataTypeException e) {
            System.out.println(e.getMessage());
        } catch (PrimaryKeyException e) {
            System.out.println(e.getMessage());
        } catch (UniqueException e) {
            System.out.println(e.getMessage());
        }

        return true;
    }

    private void select(String attr, String tableName) throws TableException {
        storageManager.select(tableName);
    }

    public void displayHelp() {
        System.out.println("\n To run 11QL, use");
        System.out.println("java Main <db loc> <page size> <buffer size>");
        System.out.println("Available functions are:");
        System.out.println("\tdisplay schema;");
        System.out.println("\tdisplay table <table name>;");
        System.out.println("\tselect * from <table name>;");
        System.out.println("\tinsert into <table name> values (...);");
        System.out.println("\tcreate table <table name> (<attribute name/type> constraints ...);");
        System.out.println("\talter table add attribute <attribute name/type> <constraints>");
        System.out.println("\talter table drop attribute <attribute name>");
        System.out.println("'quit' to exit");
    }
}