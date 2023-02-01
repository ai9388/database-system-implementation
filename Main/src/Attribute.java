///
/// Used in the catalog to store the name of the 
///

public class Attribute 
{
    
    private String name;
    private String type;

    public Attribute(String name, String type)
    {
        name = this.name;
        type = this.type;
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
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
