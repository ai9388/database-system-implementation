import java.util.*;

public class Database {
    
    private String name;
    private Map<String, Table> tables;
    private Catalog catalog;
    private String path;

    public Database(String name, Map<String, Table> tables, Catalog catalog, String path)
    {
        this.name = name;
        this.tables = tables;
        this.catalog = catalog;
        this.path = path;
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

    public boolean dropTable(int tableID)
    {
        return false;
    }

    public Table getTableByName(String name)
    {
        return this.tables.get(name);
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
    
}
