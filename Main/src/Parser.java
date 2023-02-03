public class Parser {
    private String[] args;
    public Parser(String[] args) {
        this.args = args;
    }
    /**
     * Assume user passes in database
     */
    public void parse() {
        for (String input : args) {
            input = input.toLowerCase();
            if (input.startsWith("create")){
                String[] createString = input.split("create");
                if (createString[0].startsWith("table")) {
                    createTable(createString[0].split("\\("));
                }
            } else if (input.startsWith("select")) {
                String[] selectCommand = input.split("select")[0].split("from");
                select(selectCommand[0], selectCommand[1]);
                //
            } else if (input.startsWith("insert")) {
                String[] insertCommand = input.strip().split("insertinto")[0].split("values");
                insert(insertCommand[0], insertCommand[1]);
                //
            } else if (input.startsWith("display")){
                if (input.split("display")[0].startsWith("schema")) {
                    displaySchema();
                } else if (input.split("display")[0].startsWith("info")) {
                    displayInfo(input.strip().split("displayinfo")[0]);
                } else {
                    return;
                }
                //
            } else {
                return;
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
        System.out.println("Table schema: ");
    }

    private void insert(String tableName, String vals) {
        String t = tableName;
        String[] values = vals.split(",");

        // Call storage manager insert pass in table name and list seperated vals
    }

    private void select(String attr, String tableName) {
        String[] attributes = attr.split(",");
        String t = tableName;

        // Call storage manager select and pass in attributes selected + table name
    }

    private void createTable(String[] params) {
        String tableName = params[0];
        String[] args = params[1].split(",");
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
