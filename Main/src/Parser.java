import java.util.*;

public class Parser 
{
    private String[] args;
    public Parser(String[] args) 
    {
        this.args = args;
    }
    /**
     * Assume user passes in database
     */
    public void parse() 
    {
        System.out.println("the args in parse()");
        System.out.println(Arrays.toString(args));

        // check if we have to create a table
        if (this.args[0].equals("create"))
        {
            // String[] createString = this.args.split("create");
            // if (createString[0].startsWith("table")) 
            // {
            //     createTable(createString[0].split("\\("));
            // }
        } 
        else if (this.args[0].equals("select")) 
        {
            select(this.args[1], this.args[3]);
        } 
        else if (this.args[0].equals("insert")) 
        {
            insert(this.args[2], this.args[4]);
        } 
        else if (this.args[0].equals("display"))
        {
            if (this.args[1].equals("schema")) 
            {
                displaySchema();
            } 
            else if (this.args[1].equals("info")) 
            {
                displayInfo(this.args[2]);
            } 
            else 
            {
                return;
            }
        } 
        else 
        {
            return;
        }
    }

    private void displayInfo(String name) 
    {
        // Call storage manager to display information
        System.out.println("Table name: " + name);
        System.out.println("Table schema: "); // Print table schema
        System.out.println("Number of pages: "); // Print # of pages
        System.out.println("Number of records: "); // Print # of records
    }

    private void displaySchema() 
    {
        // Data needs to be gotten from storage manager
        System.out.println("Database Location: ");
        System.out.println("Page Size: ");
        System.out.println("Buffer Size: ");
        System.out.println("Table schema: ");
    }

    private void insert(String tableName, String vals) 
    {
        String t = tableName;
        String[] values = vals.split(",");

        // Call storage manager insert pass in table name and list seperated vals
    }

    private void select(String attr, String tableName) 
    {
        String[] attributes = attr.split(",");
        String t = tableName;

        // Call storage manager select and pass in attributes selected + table name
    }

    private void createTable(String[] params) 
    {
        String tableName = params[0];
        String[] args = params[1].split(",");
        String attrName, attrType;
        boolean primarykey = false;

        for (String arg : args) 
        {
            attrName = arg;
            attrType = arg;
            if (arg.endsWith("primarykey")) {
                primarykey = true;
            }
        }
        // Call storage manager create Table with name, attrname, attrtype, and primary key if true
    }
}
