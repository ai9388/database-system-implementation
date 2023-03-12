public class TableException extends Exception{
    private String[] errorMessages = new String[]{
        "Invalid Column Name: \"%s\"", // 1
        "Invalid Table Name: \"%s\"", // 2
        "Too many entries!", // 3
        "Missing Values! ", // 4
        "Table %s already exists!", // 5
        "Column %s already exists!", // 6
        "Cannot drop Primary Attribute %s" ,// 7
        "Duplicate attribute \"%s\"" // 8
    };
    private String message = "Table Error:";

    public TableException(int errorCode, String arg){
        super();
        this.message = String.format(errorMessages[errorCode - 1], arg);
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
