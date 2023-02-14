///
/// Used in the catalog to store the name of the 
///

public class Attribute
{
    
    private String name;
    private Type type;
    private boolean pk;
    /*
     * lenght of the value
     * only applicable for char/varchar
     */
    private int N;

    public Attribute(String name, Type type, boolean pk)
    {
        this.name = name;
        this.type = type;
        this.pk = pk;
    }

    public Attribute(String name, Type type, int N, boolean pk)
    {
        this.name = name;
        this.type = type;
        this.N = N;
        this.pk = pk;
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

    public void setN(int N){
        this.N = N;
    }

    public int getN() {
        return N;
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
