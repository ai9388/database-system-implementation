///
/// Used in the catalog to store the name of the 
///

public class Attribute {

    private String name;
    private String alias;
    private Type type;
    private boolean primaryKey;
    private final boolean notNull;
    private final boolean unique;
    /*
     * length of the value
     * only applicable for char/varchar
     */
    private int N;

    public Attribute(String name, Type type, boolean primaryKey, boolean notNull, boolean unique, int N) {
        this.name = name;
        this.type = type;
        this.primaryKey = primaryKey;
        this.notNull = notNull;
        this.unique = unique;
        this.N = N;
        this.alias = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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

    public void setN(int N) {
        this.N = N;
    }

    public int getN() {
        return N;
    }

    /**
     * @return Type return the type
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

    /**
     * @return boolean return the isPrimaryKey
     */
    public boolean isIsPrimaryKey() {
        return this.primaryKey;
    }

    /**
     * @param primaryKey the isPrimaryKey to set
     */
    public void setIsPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public String toString() {
        return String.format(this.name + ": " + this.type.toString() + " " + (isIsPrimaryKey() ? "primary key" : "") + " " + (notNull ? "not null" : "") + " " + (unique ? "unique" : ""), N == 0 ? "" : this.N);
    }

    /**
     * @return notNull
     */
    public boolean getNotNull() {
        return this.notNull;
    }

    public boolean getUnique() {
        return this.unique;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public boolean equals(Object obj) {
        boolean res = false;
        if(obj instanceof Attribute){
            Attribute other = (Attribute) obj;
            res = this.name.equals(other.getName()) && this.alias.equals(other.getAlias());
        }

        return res;
    }
}
