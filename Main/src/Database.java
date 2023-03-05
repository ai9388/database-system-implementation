import java.util.*;

public class Database {
    
    private String name;
    private Map<String, Table> tables;
    private Catalog catalog;
    private String path;
    private Map<Integer, Table> tablesID;

    public Database(String name, Map<String, Table> tables, Catalog catalog, String path, Map<Integer, Table> tablesID)
    {
        this.name = name;
        this.tables = tables;
        this.catalog = catalog;
        this.path = path;
        this.tablesID = tablesID;
    }

    /**
     * @return String return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Map<String, Table> return the tables
     */
    public Map<String, Table> getTables() {
        return tables;
    }


    public Table getSingleTable(String name){
        return tables.get(name);
    }

    /**
     * @param tables the tables to set
     */
    public void setTables(Map<String, Table> tables) {
        this.tables = tables;
    }

    /**
     * @return Map<String, Attribute> return the catalog
     */
    public Catalog getCatalog() {
        return catalog;
    }

    /**
     * @param catalog the catalog to set
     */
    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    /**
     * @return String return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    public boolean addTable(Table table)
    {
        return false;
    }

    public void createTable(String tablename, ArrayList<Attribute> attributes) throws TableException{
        if(this.getTableByName(tablename) == null){
            Table newTable = new Table(tablename, attributes);
            tables.put(tablename, newTable);
            tablesID.put(newTable.getTableID(), newTable);
        }
    }

    public boolean dropTable(String tablename)
    {
        return tables.remove(tablename) != null;
    }

    public Table getTableByName(String name) throws TableException
    {
       try {
        return tables.get(name);
       } catch (NullPointerException e) {
        throw new TableException(2, name);
       }
    }

    public Table getTableByID(int id){
        return this.tablesID.get(id);
    }

    public void updateCatalog()
    {
        System.out.println("updating!");
    }

    public void getPageByTable()
    {
        System.out.println("getting page by table");
    }

    public void getPageByNum() 
    {
        
        System.out.println("getting page by number");
    } 

    public ArrayList<Record> getRecordsFromTable(Table table) 
    {
        return table.getRecords();
    }

    public ArrayList<Table> getAllTables()
    {
        return new ArrayList<Table>(this.tables.values());
    }
    
    public String selectFromTable(String tableName, String[] columns) throws TableException{
        Table table = this.getTableByName(tableName); 

        if(columns == null){
            return table.selectAll();
        }
        return table.select(columns);
    }

    public void dropAttribute(String attribute_name, String table_name) throws TableException {
        Table table = this.getTableByName(table_name);
        if (!table.removeAttribute(attribute_name)) {
            throw new TableException(1, attribute_name);
        }
        this.tables.remove(table_name);
        this.tablesID.remove(table.getTableID());
        this.tables.put(table_name, table);
        this.tablesID.put(table.getTableID(), table);
    }

    public void addAttribute(Attribute attribute, String value, String table_name) throws TableException {
        Table table = this.getTableByName(table_name);
        if (!table.addAttribute(attribute, value)) {
            throw new TableException(1, attribute.getName());
        }
    }
}
