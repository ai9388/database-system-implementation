import java.util.ArrayList;

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

    /**
     * get the name of the type
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * validate the provided value to be of type Type
     * @param value value to check
     * @param attribute attribute value is expected to belong to
     * @return true if value valid
     * @throws InvalidDataTypeException if validation fails
     */
    public static boolean validateType(String value, Attribute attribute) throws InvalidDataTypeException{
        switch(attribute.getType()){
            case INTEGER:
                try {
                    Integer.parseInt(value);
                } catch (Exception e) {
                    throw new InvalidDataTypeException(value, attribute);
                }
            case DOUBLE:
                try {
                    Double.parseDouble(value);
                } catch (Exception e) {
                    throw new InvalidDataTypeException(value, attribute);
                }
            case BOOLEAN:
                try {
                    Boolean.parseBoolean(value);
                } catch (Exception e) {
                    throw new InvalidDataTypeException(value, attribute);
                }
            case CHAR:
                if(value.length() > attribute.getN()){
                    throw new InvalidDataTypeException(value, attribute);
                }

            case VARCHAR:
                if(value.length() > attribute.getN()){
                    throw new InvalidDataTypeException(value, attribute);
                }
        }
        return true;
    }

    /**
     * validates a collection of values
     * @param values collection of values
     * @param attributes attributes containing types of collection
     * @return true if none of the values fail validation
     * @throws InvalidDataTypeException at least one of the values fails 
     */
    public static boolean validateAll(ArrayList<String> values, ArrayList<Attribute> attributes) throws InvalidDataTypeException{
        for (int i = 0; i < values.size(); i++) {
            Attribute attribute = attributes.get(i);
            String value = values.get(i);

            // if this validation fails an exception is raised
            Type.validateType(value, attribute);
        }
        return true;
    }

    /**
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