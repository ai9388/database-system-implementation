import java.nio.ByteBuffer;
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
     */
    public static boolean validateType(String value, Attribute attribute){
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
        return false;
    }

    /**
     * validates a collection of values
     * @param values collection of values
     * @param attributes attributes containing types of collection
     * @return true if none of the values fail validation
     */
    public static boolean validateAll(String[] values, ArrayList<Attribute> attributes){
        for (int i = 0; i < values.length; i++) {
            Attribute attribute = attributes.get(i);
            String value = values[i];

            if(!Type.validateType(value, attribute)){
                return false;
            }
        }
        return true;
    }


    /**
     * returns an Object object depending on the type of the entry
     * @param value value of entry
     * @param type its attribute type
     * @return the object
     */
    public static Object getObjFromType(String value, Type type){
        Object res = value;
        switch(type){
            case INTEGER:
                return (Integer.parseInt(value));
            case DOUBLE:
                return (Double.parseDouble(value));
            case BOOLEAN:
                return Boolean.parseBoolean(value);
            case VARCHAR:
                return value;
            case CHAR:
                return value;
        }

        return res;
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

    /**
     * converts int to a byte array
     * 
     * @param i integer we want to change
     * @return byte array
     */
    public static byte[] convertIntToByteArray(int i) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(i).array();
    }

    /**
     * convert boolean to byte
     * 
     * @param bool boolean we wnat to change
     * @return byte
     */
    public static byte[] convertBooleanToByteArray(boolean bool) {
        return ByteBuffer.allocate(1).put((byte) (bool ? 1 : 0)).array();
    }

    /**
     * converts double to a byte array
     * 
     * @param d double we want to change
     * @return byte array
     */
    public static byte[] convertDoubleToByteArray(double d) {
        return ByteBuffer.allocate(Double.BYTES).putDouble(d).array();
    }

    /**
     * converts char to a byte array
     * 
     * @param c char we want to change
     * @return byte array
     */
    public static byte[] convertCharToByteArray(char c) {
        return ByteBuffer.allocate(Character.BYTES).putChar(c).array();
    }

    /**
     * converts string to a byte array
     * 
     * @param st string we want to change
     * @return byte array
     */
    public static byte[] convertStringToByteArray(String st) {
        byte[] bb = new byte[st.length()];

        char[] ch = st.toCharArray();

        for (char c : ch) {
            bb = concat(bb, convertCharToByteArray(c));
        }
        return bb;
    }

    /**
     * turning the bytes into a string that we can use for records later
     * 
     * @param attributes string version of all of the schema's attributes
     * @param bytes      bytes we want to change
     * @return concatenated string seprated by spaces(?)
     */
    public static ArrayList<Object> convertBytesToObjects(ArrayList<String> attributes, byte[] bytes) {
        ArrayList<Object> result = new ArrayList<Object>();
        for (int i = 0; i < attributes.size(); i++) {
            switch (attributes.get(i)) {
                case "int" -> {
                    result.add(ByteBuffer.wrap(bytes).getInt());
                }
                case "bool" -> {
                    result.add(ByteBuffer.wrap(bytes).get());
                }
                case "double" -> {
                    result.add(ByteBuffer.wrap(bytes).getDouble());
                }
                case "char" -> {
                    result.add(ByteBuffer.wrap(bytes).getChar());
                }
                case "varchar" -> {
                    result.add(ByteBuffer.wrap(bytes).getChar());
                }
            }
        }

        return result;
    }

    /**
     * helper method to concatenate multiple byte arrays
     * 
     * @param arrays any N number of byte[]
     * @return concated cyte[]
     */
    public static byte[] concat(byte[]... arrays) {
        // Determine the length of the result array
        int totalLength = 0;
        for (int i = 0; i < arrays.length; i++) {
            totalLength += arrays[i].length;
        }

        // create the result array
        byte[] result = new byte[totalLength];

        // copy the source arrays into the result array
        int currentIndex = 0;
        for (int i = 0; i < arrays.length; i++) {
            System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
            currentIndex += arrays[i].length;
        }

        return result;
    }

}