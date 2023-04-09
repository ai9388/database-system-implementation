import java.util.ArrayList;

public class InvalidDataTypeException extends Exception{

    private String expected;
    private String got;
    private String message;
    /*
     * when a single value is incorrect
     * usually raised when updating a single value and it needs validation
     */
    public InvalidDataTypeException(String value, Attribute attribute){
        super();
        int len = 0;
        if(value != null){
            len = value.length();
        }
        if(attribute.getType() == Type.CHAR || attribute.getType() == Type.VARCHAR){
            this.expected = (attribute.getType().getName() + "(" + attribute.getN() + ")");
            this.got = (Type.identifyType(value, attribute).getName() + " (" + len + ")");
        }
        else{
            this.expected = attribute.getType().getName();
            got += Type.identifyType(value, attribute).getName();
        }
    }

    @Override
    public String getMessage() {
        this.message = "Invalid Data Type: Expected(" + expected.strip() + ") got("
         + got.strip() + ")";
        return this.message;
    }

    /*
     * Used when creating a record
     */
    public InvalidDataTypeException(String[] values, ArrayList<Attribute> attributes){
        super();
        expected = "";
        got = "";
        for (int i = 0; i < values.length; i++) {
            if(i != 0){
                expected += ", ";
                got += ", ";
            }
            Attribute a = attributes.get(i);
            String v = values[i];
            int len = 0;
            if(v != null){
                len = v.length();
            }
            if(a.getType() == Type.CHAR || a.getType() == Type.VARCHAR){
                this.expected += (a.getType().getName() + "(" + a.getN() + ")");
                this.got += (Type.identifyType(v, a).getName() + "(" + len + ")");
            }
            else{
                this.expected += a.getType().getName().strip();
                this.got += Type.identifyType(v, a).getName();
            }
        }
    }
    
}
