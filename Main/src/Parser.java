import java.util.*;

public class Parser {
     enum commands {
        CREATE_TABLE, DISPLAY_SCHEMA, DISPLAY_INFO, SELECT, INSERT, HELP, QUIT
    }

    private final String user_input;
    private commands command;
    private String dbLocation;
    private int pageSize;
    private int bufferSize;

    public Parser(String str_input) {
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
            System.out.println("Invalid Command.");
        }
    }

    /**
     * Args need to be saved to pass into catalog
     */
    public void saveArgs(String[] args)
    {
        this.dbLocation = args[0];
        this.pageSize = Integer.parseInt(args[1]);
        this.bufferSize = Integer.parseInt(args[2]);
    }

    /**
     * Assume user passes in database
     */
    public void parse() {
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
                Table t = StorageManager.getTable(table_name);
                if (t != null) {
                    System.out.println("This table already exists.");
                    break;
                }
                boolean hasOnePK = false; //character.bytes dont hardcode vals
                for (String attribute : attr) {
                    String[] components = attribute.strip().replaceAll("\\(", " ").split(" ");
                    String attr_name = components[0];
                    boolean primarykey = components.length > 2 && components[2].equals("primarykey");
                    switch (components[1]) {
                        case "char" -> {
                            // originally had A XOR B, should be !(A XOR B)
                            hasOnePK = !((components.length > 3 && !hasOnePK) || (components.length <= 3 && hasOnePK));
                            Attribute a = new Attribute(attr_name, Type.CHAR, components.length > 3, Integer.parseInt(components[1]));
                            primaryAttribute = a.isIsPrimaryKey() ? a : primaryAttribute;
                            primaryIndex = a.isIsPrimaryKey() ? primaryIndex : primaryIndex + 1;
                            attributes.add(a);
                            //check component after char to know length
                        }
                        case "varchar" -> {
                            hasOnePK = !((components.length > 3 && !hasOnePK) || (components.length <= 3 && hasOnePK));
                            Attribute a = new Attribute(components[1], Type.VARCHAR, components.length > 3, Integer.parseInt(components[1]));
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
                            System.out.println("one or more attributes have a poorly written type.");
                        }
                    }
                }
                if (!hasOnePK) {
                    System.out.println("ERROR!");
                    System.out.println("No primary key defined.");
                } else {
                    System.out.println(attributes.toString());
                    try{
                        Table table = new Table(table_name, 1, attributes, primaryAttribute, primaryIndex);
                        StorageManager.addTable(table);
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
            case DISPLAY_SCHEMA -> displaySchema();
            case DISPLAY_INFO -> {
                String table_name = user_input.replaceFirst("display info", "").strip();
                displayInfo(table_name);
            }
            case SELECT -> {
                String input = user_input.replaceFirst("select", "").strip();
                int start_index = input.indexOf("from");
                int end_index = input.indexOf("m", start_index);
                String table_name = input.substring(end_index + 1).replaceAll(";", "").strip();
                select("*", table_name);
            }
            case INSERT -> {
                String input = user_input.replaceFirst("insert into", "").strip();
                String table_name = input.split(" ")[0];
                ArrayList<Record> table_values = new ArrayList<>();
                int start_index = input.indexOf("(");
                input = input.substring(start_index - 1);
                String[] vals = input.split(",");
                Table table = StorageManager.getTable(table_name);
                if(table == null) {
                    System.out.println("ERROR! Table " + table_name + "does not exist.");
                    command = commands.QUIT;
                    break;
                }
                for (String value : vals) {
                    String[] values = value.replaceAll("[();]", "").strip().split(" ");
                    ArrayList<Attribute> a = table.getAttributes();
                    try {
                        table_values.add(new Record(new ArrayList<>(Arrays.asList(values)), a));
                    } catch (Exception e) {
                        System.out.println("ERROR! Invalid data entered.");
                        command = commands.QUIT;
                        break;
                    }
                }
                insert(table_name, table_values);
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

    private void displayInfo(String table_name) {
        StorageManager.displayInfo(table_name);
    }

    private void displaySchema() {
        // Todo: Data needs to be gotten from storage manager
        System.out.println("Database Location: ");
        System.out.println("Page Size: ");
        System.out.println("Buffer Size: ");
        if (StorageManager.hasTable()) {
            StorageManager.displaySchema("table");
        }
        else {
            System.out.println("No tables to display");
        }
        System.out.println("SUCCESS");
    }

    private void insert(String tableName, ArrayList<Record> vals) {
        StorageManager.insertRecords(tableName, vals);
    }

    private void select(String attr, String tableName) {
        // if (attr.equals("*")) {
        //     Table t = StorageManager.getTable(tableName);
        //     if (t == null) {
        //         System.out.println("ERROR!");
        //     }
        //     else {
        //         t.getRecords().forEach(System.out::println);
        //     }
        // }
        // else {
        //     String[] attributes = attr.strip().split(",");
        //     Table t = StorageManager.getTable(tableName);
        //     if (t != null) {
        //         ArrayList<Record> records = t.getRecords();
        //         ArrayList<String> selected = new ArrayList<>();
        //         for (int i = 0; i < records.size(); i++) {
        //             for (String attribute : attributes) {
        //                 attribute = attribute.strip();
        //                 // TODO: this needs to be edited but rough idea...
        //                 selected.add(i, records.get(i).getValueAtColumn(attribute));
        //             }
        //         }
        //     }
        //     else {
        //         System.out.println("ERROR!");
        //     }
        //}
    }

    private void createTable(String tableName, String[] args) {
        //  attrName, String attrType, boolean primaryKey
        String attrName, attrType;
        boolean primarykey = false;

        for (String arg : args) {
            attrName = arg;
            attrType = arg;
            if (arg.endsWith("primarykey")) {
                primarykey = true;
            }
        }
        // Call storage manager create Table with name, attrname, attrtype, and primary key if true
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
