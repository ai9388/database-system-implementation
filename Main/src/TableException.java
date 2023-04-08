public class TableException extends Exception{
    private String[] errorMessages = new String[]{
        "Invalid Column: \"%s\"", // 1
        "Invalid Table Name: \"%s\"", // 2
        "Too many entries!", // 3
        "Missing Values! ", // 4
        "Table %s already exists!", // 5
        "Column %s already exists!", // 6
        "Cannot drop Primary Attribute %s" ,// 7
        "Duplicate attribute \"%s\"", // 8
        "Ambiguous column \"%s\", belongs to multiple tables", // 9
        "No data for table \"%s\"", // 10
    };

    private int errorCode;
    private String message = "Table Error:";

    public TableException(int errorCode, String arg ){
        super();
        this.errorCode = errorCode;
        this.message = String.format(errorMessages[errorCode - 1], arg);
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
