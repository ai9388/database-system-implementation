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

    /*
     * validate that the provided value is of type Type
     * @param value values to check
     * @param attribute attribute value is expected to belong to
     */
    public static boolean validateType(String value, Attribute attribute) throws InvalidDataTypeException{
        switch(attribute.getType()){
            case INTEGER:
                try {
                    Integer.parseInt(value);
                } catch (Exception e) {
                    // TODO: handle exception
                    // throw custom exception with message
                    throw new InvalidDataTypeException(value, attribute.getType());
                }
            case DOUBLE:
                try {
                    Double.parseDouble(value);
                } catch (Exception e) {
                    // TODO: handle exception
                    // throw custom exception with message
                    throw new InvalidDataTypeException(value, attribute.getType());
                }
            case BOOLEAN:
                try {
                    Boolean.parseBoolean(value);
                } catch (Exception e) {
                    // TODO: handle exception
                    // throw custom exception with message
                    throw new InvalidDataTypeException(value, attribute.getType());
                }
            case CHAR:
                try {
                    if(value.length() > attribute.getN()){
                        // throw custom exception
                        return false;
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    // throw custom exception with message
                    throw new InvalidDataTypeException(value, attribute.getType());
                }
            case VARCHAR:
                try {
                    if(value.length() > attribute.getN()){
                        // throw custom exception
                        throw new InvalidDataTypeException(value, attribute.getType());
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    // throw custom exception with message
                    throw new InvalidDataTypeException(value, attribute.getType());
                }
        }
        return true;
    }

    /*
     * Identify the type of the provided value
     * @param value values to identify
     */
    public static Type identifyType(String value){
        try {
            Integer.parseInt(value);
            return Type.INTEGER;
        }
        catch(Exception e){

        }
        try {
            Boolean.parseBoolean(value);
            return Type.BOOLEAN;
        }
        catch(Exception e){

        }
        return Type.CHAR;
    }

}