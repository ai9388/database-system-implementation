import java.util.ArrayList;

public class InvalidDataTypeException extends Exception{

    String expected;
    String got;
    String message;
    /*
     * when a single value is incorrect
     * usually raised when updating a single value and it needs validation
     */
    public InvalidDataTypeException(String value, Attribute attribute){
        super();
        if(attribute.getType() == Type.CHAR || attribute.getType() == Type.VARCHAR){
            this.expected = (attribute.getType().getName() + "(" + attribute.getN() + ")");
            this.got = (Type.identifyType(value) + " (" + value.length() + ")");
        }
        else{
            this.expected = attribute.getType().getName();
            got += Type.identifyType(value);
        }
    }

    @Override
    public String getMessage() {
        this.message = "Invalid Data Type: Expected( " + expected + ") got( " 
         + got + ")";
        return this.message;
    }

    /*
     * Used when creating a record
     */
    public InvalidDataTypeException(ArrayList<String> values, ArrayList<Attribute> attributes){
        super();
        expected = "";
        got = "";
        for (int i = 0; i < values.size(); i++) {
            Attribute a = attributes.get(i);
            String v = values.get(i);
            if(a.getType() == Type.CHAR || a.getType() == Type.VARCHAR){
                this.expected += (a.getType().getName() + "(" + a.getN() + ")");
                this.got += (Type.identifyType(v) + "(" + v.length() + ")");
            }
            else{
                this.expected += a.getType().getName() ;
                this.got += Type.identifyType(v);
            }

            expected += " ";
            got += " ";
        }
    }
    
}
