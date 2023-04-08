import java.util.*;

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
        this.dbName = dbName;
        this.dbLocation = dbLocation;
        storageManager = new StorageManager(dbName, dbLocation, bufferSize, pageSize);
    }

    public void classifyInput(String str_input) {

        this.user_input = str_input.toLowerCase().strip();
        if((user_input.charAt(user_input.strip().length() -1 ) != ';') && !user_input.strip().startsWith("quit") && !user_input.strip().startsWith("help") ){
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
            System.out.println("Invalid Command.");
            command = commands.EMPTY;
        }
    }

    /**
     * Assume user passes in database
     */
    public boolean parse() throws ConditionalException {
        try {
            switch (command) {
                case CREATE_TABLE -> {
                    String input = user_input.replaceFirst("create table", "").strip();
                    int start_index = input.indexOf("(");
                    int end_index = input.indexOf(")");
                    String table_name = input.substring(0, start_index);
                    switch (table_name) {
                        case "integer": throw new TableException(2, "Integer");
                        case "create": throw new TableException(2, "create");
                        case "table": throw new TableException(2, "table");
                        case "drop": throw new TableException(2, "drop");
                        case "alter": throw new TableException(2, "alter");
                        case "display": throw new TableException(2, "display");
                        case "quit": throw new TableException(2, "quit");
                        case "update": throw new TableException(2, "update");
                        case "delete": throw new TableException(2, "delete");
                        case "insert": throw new TableException(2, "insert");
                        case "select": throw new TableException(2, "select");
                        case "help": throw new TableException(2, "help");
                        case "char": throw new TableException(2, "char");
                        case "varchar": throw new TableException(2, "varchar");
                        case "primarykey": throw new TableException(2, "primarykey");
                        case "notnull": throw new TableException(2, "notnull");
                        case "unique": throw new TableException(2, "unique");
                        case "bool": throw new TableException(2, "bool");
                        case "double": throw new TableException(2, "double");
                    }
                    String columns = input.substring(start_index + 1, end_index).strip();
                    String[] attr = columns.split(",");
                    ArrayList<Attribute> attributes = new ArrayList<>();
                    Attribute primaryAttribute = null;
                    int primaryIndex = 0;

                    boolean hasOnePK = false;
                    for (String attribute : attr) {
                        String[] components = attribute.strip().replaceAll("\\(", " ").split(" ");
                        String attr_name = components[0];
                        switch (attr_name) {
                            case "integer": throw new TableException(1, "Integer");
                            case "create": throw new TableException(1, "create");
                            case "table": throw new TableException(1, "table");
                            case "drop": throw new TableException(1, "drop");
                            case "alter": throw new TableException(1, "alter");
                            case "display": throw new TableException(1, "display");
                            case "quit": throw new TableException(1, "quit");
                            case "update": throw new TableException(1, "update");
                            case "delete": throw new TableException(1, "delete");
                            case "insert": throw new TableException(1, "insert");
                            case "select": throw new TableException(1, "select");
                            case "help": throw new TableException(1, "help");
                            case "char": throw new TableException(1, "char");
                            case "varchar": throw new TableException(1, "varchar");
                            case "primarykey": throw new TableException(1, "primarykey");
                            case "notnull": throw new TableException(1, "notnull");
                            case "unique": throw new TableException(1, "unique");
                            case "bool": throw new TableException(1, "bool");
                            case "double": throw new TableException(1, "double");
                        }
                        boolean primarykey = false;
                        boolean notNull = false;
                        boolean unique = false;
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
                                Attribute a = new Attribute(attr_name, Type.CHAR, primarykey, notNull, unique, Integer.parseInt(components[2].replace(')', ' ').strip()));
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
                                Attribute a = new Attribute(attr_name, Type.VARCHAR, primarykey, notNull, unique, Integer.parseInt(components[2].replace(')', ' ').strip()));
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
                                Attribute a = new Attribute(attr_name, Type.BOOLEAN, primarykey, notNull, unique, 0);
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
                                Attribute a = new Attribute(attr_name, Type.INTEGER, primarykey, notNull, unique, 0);
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
                    storageManager.displayTableInfo(table_name);
                }
                case SELECT -> {
                    String input = user_input.replaceFirst("select", "").strip();
                    String replaceSemi = input.replace(";", "").strip();
                    int start_index = input.indexOf("from");
                    ArrayList<String> attributes = new ArrayList<>();
                    String attribute = input.substring(0, start_index).strip();
                    if (attribute.equals("*")) {
                        attributes.add(attribute);
                    }
                    else {
                        attributes.addAll(List.of(attribute.replaceAll(" ", "").split(",")));
                    }
                    
                    String table_name = "";
                    String where_clause = "";
                    String orderby_clause = "";
                    String[] splitFrom = replaceSemi.split("from");
                    if(splitFrom[1].contains("where") && splitFrom[1].contains("orderby")){
                        String[] splitWhere = splitFrom[1].split("where");
                        table_name = splitWhere[0].strip();
                        String[] splitOrder = splitWhere[1].split("orderby");
                        where_clause = splitOrder[0].strip();
                        orderby_clause = splitOrder[1].strip();
                    }
                    else if(splitFrom[1].contains("where")){
                        String[] splitWhere = splitFrom[1].split("where");
                        table_name = splitWhere[0].strip();
                        where_clause = splitWhere[1].strip();
                
                    }
                    else if(splitFrom[1].contains("orderby")){
                        String[] splitOrder = splitFrom[1].split("orderby");
                        table_name = splitOrder[0].strip();
                        orderby_clause = splitOrder[1].strip();
                    }
                    else{
                        table_name = splitFrom[1].strip();
                    }
                                
                    // start_index = input.indexOf("m", start_index);
                    // int end_index = input.indexOf("where") != -1 ? input.indexOf("where") : input.length() - 1;
                    ArrayList<String> tables = new ArrayList<>();
                    // String table_name = input.substring(start_index + 1, end_index).strip();


                    // check if multiple tables
                    if (table_name.indexOf(",") != -1) {
                        tables.addAll(List.of(table_name.replaceAll(" ", "").split(",")));
                    } else {
                        tables.add(table_name);
                    }
        
                    
                    
                    // TODO: update this with where and orderby and the multiple tables/attributes
                    storageManager.select(tables, attributes, where_clause, orderby_clause);
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
                    } catch (ConstraintException e) {
                        System.out.println(e.getMessage());
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
                    switch (table_name) {
                        case "integer": throw new TableException(1, "Integer");
                        case "create": throw new TableException(1, "create");
                        case "table": throw new TableException(1, "table");
                        case "drop": throw new TableException(1, "drop");
                        case "alter": throw new TableException(1, "alter");
                        case "display": throw new TableException(1, "display");
                        case "quit": throw new TableException(1, "quit");
                        case "update": throw new TableException(1, "update");
                        case "delete": throw new TableException(1, "delete");
                        case "insert": throw new TableException(1, "insert");
                        case "select": throw new TableException(1, "select");
                        case "help": throw new TableException(1, "help");
                        case "char": throw new TableException(1, "char");
                        case "varchar": throw new TableException(1, "varchar");
                        case "primarykey": throw new TableException(1, "primarykey");
                        case "notnull": throw new TableException(1, "notnull");
                        case "unique": throw new TableException(1, "unique");
                        case "bool": throw new TableException(1, "bool");
                        case "double": throw new TableException(1, "double");
                    }
                    boolean drop = input.split(" ")[1].strip().equals("drop");
                    String attribute_name = input.split(" ")[2];
                    switch (attribute_name) {
                        case "integer": throw new TableException(1, "Integer");
                        case "create": throw new TableException(1, "create");
                        case "table": throw new TableException(1, "table");
                        case "drop": throw new TableException(1, "drop");
                        case "alter": throw new TableException(1, "alter");
                        case "display": throw new TableException(1, "display");
                        case "quit": throw new TableException(1, "quit");
                        case "update": throw new TableException(1, "update");
                        case "delete": throw new TableException(1, "delete");
                        case "insert": throw new TableException(1, "insert");
                        case "select": throw new TableException(1, "select");
                        case "help": throw new TableException(1, "help");
                        case "char": throw new TableException(1, "char");
                        case "varchar": throw new TableException(1, "varchar");
                        case "primarykey": throw new TableException(1, "primarykey");
                        case "notnull": throw new TableException(1, "notnull");
                        case "unique": throw new TableException(1, "unique");
                        case "bool": throw new TableException(1, "bool");
                        case "double": throw new TableException(1, "double");
                    }
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
                            storageManager.addAttributeToTable(a, value, table_name);
                        } else {
                            storageManager.addAttributeToTable(a, "", table_name);
                        }
                    }
                    System.out.println("Table " + table_name + " altered");
                }
                case DELETE -> {
                    String input = user_input.replaceFirst("delete from", "").strip();
                    String replaceSemi = input.replace(";", "").strip();
                    String table_name = replaceSemi.split(" ")[0];
                    int start_index = replaceSemi.indexOf("e", replaceSemi.indexOf("where"));
                    int end_index = replaceSemi.length() - 1;
                    if (start_index != -1) {
                        String where_clause = replaceSemi.substring(start_index, end_index).strip();
                        storageManager.delete(table_name, where_clause);
                    } else {
                        storageManager.deleteRecords(table_name);
                    }
                }
                case UPDATE -> {
                    String input = user_input.replaceFirst("update", "").strip();
                    int start_index = input.indexOf("set");
                    String table_name = input.substring(0, start_index).strip();
                    start_index = input.indexOf("t", start_index) + 1;
                    int end_index = input.indexOf("=");
                    // TODO: Add functionality for multiple columns/values? (May not be required)
                    String column = input.substring(start_index, end_index).strip();
                    start_index = end_index + 1;
                    end_index = input.indexOf(",", start_index) != -1 ? input.indexOf(",", start_index) : input.indexOf("where", start_index) != -1 ? input.indexOf("where", start_index): input.length() - 1;
                    String value = input.substring(start_index, end_index).strip();
                    // check for where
                    String where_clause = "";
                    if (input.indexOf("where") != -1) {
                        start_index = end_index;
                        end_index = input.length() - 1;
                        where_clause = input.substring(start_index, end_index).replaceFirst("where", "").strip();
                    }
//                    storageManager.update(table_name, column, value, where_clause);
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
        } catch (ConstraintException e) {
            System.out.println(e.getMessage());
        }

        return true;
    }

    private void select(String attr, String tableName) throws TableException {
//        storageManager.select(tableName);
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
        System.out.println("\talter table <name> add <attribute name/type> <constraints>");
        System.out.println("\talter table <name> drop <attribute name>");
        System.out.println("\talter table <name> add <a_name> <a_type> default <value>;");
        System.out.println("'quit' to exit");
    }
}