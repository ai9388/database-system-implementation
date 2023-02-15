import java.util.*;

public class Parser {
    enum commands {
        CREATE_TABLE, DISPLAY_SCHEMA, DISPLAY_INFO, SELECT, INSERT, HELP, QUIT
    }

    private final String user_input;
    private commands command;

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
        } else if (user_input.startsWith("<help>")) {
            command = commands.HELP;
        } else if (user_input.startsWith("<quit>")) {
            command = commands.QUIT;
        } else {
            System.out.println("Invalid Command.");
        }
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
                // Check if table exists
                // If it doesn't create it with table
                boolean hasOnePK = false;
                for (String attribute : attr) {
                    String[] components = attribute.strip().replaceAll("\\(", " ").split(" ");
                    String attr_name = components[0];
                    boolean primarykey = components.length > 2;
                    switch (components[1]) {
                        case "char" -> {
                            hasOnePK = components.length > 3 && !hasOnePK;
                            attributes.add(new Attribute(attr_name, Type.CHAR, components.length > 3, Integer.parseInt(components[1])));
                            //check component after char to know length
                        }
                        case "varchar" -> {
                            hasOnePK = components.length > 3 && !hasOnePK;
                            attributes.add(new Attribute(components[1], Type.VARCHAR, components.length > 3, Integer.parseInt(components[1])));
                        }
                        case "bool" -> {
                            hasOnePK = primarykey && !hasOnePK;
                            attributes.add(new Attribute(attr_name, Type.BOOLEAN, primarykey, 0));
                        }
                        case "integer" -> {
                            hasOnePK = primarykey && !hasOnePK;
                            attributes.add(new Attribute(attr_name, Type.INTEGER, primarykey, 0));
                        }
                        case "double" -> {
                            hasOnePK = primarykey && !hasOnePK;
                            attributes.add(new Attribute(attr_name, Type.DOUBLE, primarykey, 0));
                        }
                    }
                }
                if (!hasOnePK) {
                    System.out.println("ERROR!");
                } else {
                    Table table = new Table(table_name, 1, attributes, new ArrayList<Record>());
                    // send to db through storage manager
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
                for (String value : vals) {
                    String[] values = value.replaceAll("[();]", "").strip().split(" ");
                    table_values.add(new Record(values));
                }
                insert(table_name, table_values);
            }
            case HELP -> System.out.println();
            case QUIT -> {
                System.out.println("Shutting down the database...");
                System.out.println("Purging the page buffer...");
                System.out.println("Saving catalog...");
                System.out.println("\nExiting the database...");
            }
        }
    }

    private void displayInfo(String name) {
        // Call storage manager to display information
        System.out.println("Table name: " + name);
        System.out.println("Table schema: "); // Print table schema
        System.out.println("Number of pages: "); // Print # of pages
        System.out.println("Number of records: "); // Print # of records
    }

    private void displaySchema() {
        // Data needs to be gotten from storage manager
        System.out.println("Database Location: ");
        System.out.println("Page Size: ");
        System.out.println("Buffer Size: ");
        // If table
        System.out.println("Table schema: ");
        // otherwise
        System.out.println("No tables to display");
        System.out.println("SUCCESS");
    }

    private void insert(String tableName, ArrayList<Record> vals) {

        // Call storage manager insert pass in table name and record to insert
    }

    private void select(String attr, String tableName) {
        String[] attributes = attr.split(",");
        String t = tableName;

        // Call storage manager select and pass in attributes selected + table name
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
}
