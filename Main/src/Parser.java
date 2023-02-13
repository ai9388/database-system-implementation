import java.util.*;

public class Parser 
{
    private String[] args;
    private String path;
    private String pageSize;
    private String bufferSize;

    public Parser(String[] args) 
    {
        this.args = args;
    }

    public void saveArgs(String[] args)
    {
        this.path = args[0];
        this.pageSize = args[1];
        this.bufferSize = args[2];
    }
    /**
     * Assume user passes in database
     */
    public void parse() 
    {   
        // check if we have to create a table
        if (this.args[0].equals("create"))
        {

            ArrayList<String> tableInfo = new ArrayList<>();

            // getting rid of 'create table'
            for (int i = 2; i < this.args.length; i ++)
            {
                tableInfo.add(this.args[i]);
            }

            // getting the name of the table by splitting on '('
            String tableName = tableInfo.get(0).split("[(]")[0];

            // since we got the table name, everything else is the key params

            //tableInfo.remove(0);
            // System.out.println(tableInfo);
            createTable(tableName, tableInfo);
            
            
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

    private void createTable(String tableName, ArrayList<String> params) 
    {

        int ps = Integer.parseInt(this.pageSize);
        int bs = Integer.parseInt(this.bufferSize);
        Catalog catalog = new Catalog(this.path, null, ps, bs);
        catalog.writeToFile();

        // String tableName = params[0];
        // String[] args = params[1].split(",");
        // String attrName, attrType;
        // boolean primarykey = false;

        // for (String arg : args) 
        // {
        //     attrName = arg;
        //     attrType = arg;
        //     if (arg.endsWith("primarykey")) {
        //         primarykey = true;
        //     }
        // }
        // Call storage manager create Table with name, attrname, attrtype, and primary key if true
    }
}
