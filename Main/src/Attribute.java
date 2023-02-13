///
/// Used in the catalog to store the name of the 
///

public class Attribute <Type>
{
    
    private String name;
    private Type type;
    private boolean primarykey;

    public Attribute(String name, Type type, boolean primarykey)
    {
        this.name = name;
        this.type = type;
        this.primarykey = primarykey;
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
     * @return String return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

}
