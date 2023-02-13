public enum Type {
    INTEGER ("int"),
    DOUBLE ("double"),
    BOOLEAN ("bool"),
    CHAR ("char"),
    VARCHAR ("varchar");
    
    private String name;

    Type(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}