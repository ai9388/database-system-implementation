import java.util.ArrayList;

public class InvalidDataTypeException extends Exception{

    String message;
    /*
     * when a single value is incorrect
     * ussually raised when updating
     */
    public InvalidDataTypeException(String value, Type type){
        super();
        this.message = "Expected " + type + " got " + Type.identifyType(value);
    }

    @Override
    public String getMessage() {
        // TODO Auto-generated method stub
        return this.message;
    }

    /*
     * Used when creating a record
     */
    public InvalidDataTypeException(ArrayList<String> values, ArrayList<Attribute> attributes){
        super();
        String expected = "(";
        String got = "(";
        
        for (int i = 0; i < values.size(); i++) {
            expected += (attributes.get(i).getType().getName() + " ");
            Type type = Type.identifyType(values.get(i));
            if(type == Type.CHAR){
                got += (Type.identifyType(values.get(i)) + " (" + values.get(i).length() + ")");
            }
            else{
                got += (Type.identifyType(values.get(i)) + " ");
            }
        }
        expected = expected.strip() + ")"; 
        got = expected.strip() + ")"; 
        this.message = "Expected " + expected + " got " + got;
    }
    
}
