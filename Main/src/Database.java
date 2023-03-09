import java.util.*;

public class Database {
    
    private String name;

    private String path;
    private Map<String, TableSchema> tables;

    private Map<Integer, TableSchema> tablesID;

    /**
     * used when loading a database from hardware
     * @param name the name of the database
     * @param path the path
     * @param tables the tables in hardware
     */
    public Database(String name, String path, ArrayList<TableSchema> tables)
    {
        this.name = name;
        this.path = path;
        setTables(tables);
    }

    /**
     * constructor when creating a database without tables yet
     * @param name the name of the database
     * @param path the path
     */
    public Database(String name, String path){
        this.name = name;
        this.path = path;
    }

    /**
     * @return String return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return String return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * adds an already existing table from hardware
     * @param table the table schema to insert
     */
    public void addTable(TableSchema table)
    {
        this.tables.put(table.getName(), table);
        this.tablesID.put(table.getTableID(), table);
    }

    /**
     * creates a new table and makes sure its name is unique
     * @param tablename the name of the table
     * @param attributes the attributes that make up the schema
     * @throws TableException if the table name already exists
     */
    public void createTable(String tablename, ArrayList<Attribute> attributes) throws TableException {
        if (!this.tables.containsKey(tablename)) {
            TableSchema table = new TableSchema(tablename, attributes);
            tables.put(tablename, table);
            tablesID.put(table.getTableID(), table);
        }
        else{
            throw new TableException(5, tablename);
        }
    }

    /**
     * @return a collection of all the tables
     */
    public ArrayList<TableSchema> getTables() {
        return new ArrayList<>(tables.values());
    }

    /**
     * returns a single table based on the name
     * @param name the name of the table
     * @return TableSchema Object matching
     */
    }

    /**
     * iterates the table collection and populates the
     * tables by name and tables by ID Map
     * @param tables the tables to set
     */
    public void setTables(ArrayList<TableSchema> tables) {
        this.tables = new HashMap<>();
        this.tablesID = new HashMap<>();
        for(TableSchema table: tables){
            this.tables.put(table.getName(),table);
            this.tablesID.put(table.getTableID(), table);
        }
    }

    /**
     * drops a table from the database
     * @param tablename the name of the table to delete from database
     * @throws TableException if table referenced does not exist
     */
    public void dropTable(String tablename) throws TableException
    {
        if(tables.containsKey(tablename)){
            this.tablesID.remove(tables.get(tablename).getTableID());
            this.tables.remove(tablename);
        }
        else{
            throw new TableException(2, tablename);
        }
    }

    /**
     * returns a table based on its ID
     * @param id the table id
     * @return the table object (most likely exists)
     *  no need to validate name
     */
    public TableSchema getTableByID(int id){
        return this.tablesID.get(id);
    }

    /**
     * validates the record by verifying that all value types are correct
     * @param tableName the name of the table
     * @param values the values being inserted
     * @return true if all the types are valid
     * @throws TableException if the table name/object does not exist
     * @throws InvalidDataTypeException if the object type is not valid
     */
    public Record validateRecord(TableSchema table, String[] values) throws TableException, InvalidDataTypeException {
        // get all the attributes
        ArrayList<Attribute> attributes = table.getAttributes();

        if(values.length < attributes.size()){
            throw new TableException(4, "");
        }
        if(values.length > attributes.size()){
            throw new TableException(3, "");
        }
        if (Type.validateAll(values, attributes)) {
            return new Record(new ArrayList<>(Arrays.asList(values, attributes)));
        } else {
            // creation of record failed
            throw new InvalidDataTypeException(values, attributes);
        }
    }

    public String selectFromTable(String tableName, String[] columns) throws TableException{
        //TODO : how to do this?

        return "";
    }

    /**
     * drops an attribute from the table
     * @param attribute_name the name of the attribute
     * @param table_name the name of the table
     * @throws TableException
     *         code 2: if the table name provided does not match a table
     *         code 1: if the attribute name does not match a column
     */
    public void dropAttribute(String attribute_name, String table_name) throws TableException {
        TableSchema table = this.getTable(table_name); // throws except. if table name invalid
        table.removeAttribute(attribute_name); // throws exception if column name invalid
//        this.tables.remove(table_name);
//        this.tablesID.remove(table.getTableID());
//        this.tables.put(table_name, table);
//        this.tablesID.put(table.getTableID(), table);
    }


    /**
     * adds an attribute to a table
      * @param attribute the name of the attribute
     * @param value the value
     * @param table_name
     * @throws TableException
     */
    public void addAttribute(Attribute attribute, String value, String table_name) throws TableException {
        TableSchema table = this.getTable(table_name);
        table.addAttribute(attribute);
    }

}
