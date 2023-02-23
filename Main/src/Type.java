import java.util.ArrayList;

import javax.swing.text.html.HTMLDocument.RunElement;

public enum Type {
    INTEGER ("int"),
    DOUBLE ("double"),
    BOOLEAN ("bool"),
    CHAR ("char"),
    VARCHAR ("varchar");
    
    private String name;
    private static String nullVal = "null";

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
     * @throws PrimaryKeyException
     */
    public static boolean validateType(String value, Attribute attribute) throws InvalidDataTypeException{
        // verify if null regardless of data type
        // null values are always allowed
        if(value.equalsIgnoreCase("null")){
            return true;
        }
        try{
            switch(attribute.getType()){
                case INTEGER:
                    Integer.parseInt(value);
                    return true;
                case DOUBLE:
                        Double.parseDouble(value);
                        return true;
                case BOOLEAN:
                        Boolean.parseBoolean(value);
                        return true;
                case CHAR:
                    if(value.length() != attribute.getN()){
                        break;
                    }
                    else{
                        return true;
                    }
                case VARCHAR:
                    if(value.length() > attribute.getN()){
                        break;
                    }
                    else{
                        return true;
                    }
            }
        }
        catch(NumberFormatException NFE){
            return false;
        }
        throw new InvalidDataTypeException(value, attribute);
    }

    /**
     * validates a collection of values
     * @param values collection of values
     * @param attributes attributes containing types of collection
     * @return true if none of the values fail validation
     * @throws InvalidDataTypeException at least one of the values fails 
     * @throws PrimaryKeyException
     */
    public static boolean validateAll(String[] values, ArrayList<Attribute> attributes) throws InvalidDataTypeException {
        for (int i = 0; i < values.length; i++) {
            Attribute attribute = attributes.get(i);
            String value = values[i];

            // if this validation fails an exception is raised
            try {
                Type.validateType(value, attribute);
            } catch (InvalidDataTypeException e) {
                System.out.println(e.getMessage());
                throw new InvalidDataTypeException(values, attributes);
        }
    }
        return true;
    }

    /**
     * Identify the type of the provided value
     * @param value values to identify
     */
    public static Type identifyType(String value, Attribute a){
        try {
            Integer.parseInt(value);
            return Type.INTEGER;
        }
        catch(Exception e){
            
        }
        try {
            Double.parseDouble(value);
            return Type.DOUBLE;
        }
        catch(Exception e){
            
        }
        if(value.strip().equalsIgnoreCase("true") || value.strip().equalsIgnoreCase("true")) {
            return Type.BOOLEAN;
        }
        return Type.VARCHAR;
    }

    @Override
    public String toString() {
        if (this.equals(CHAR) || this.equals(VARCHAR)) {
            return this.name + "(%s)";
        }
        return this.name;
    }
}