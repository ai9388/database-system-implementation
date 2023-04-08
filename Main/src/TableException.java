public class TableException extends Exception{
    private String[] errorMessages = new String[]{
        "Invalid Column: \"%s\"", // 1
        "Invalid Table Name: \"%s\"", // 2
        "Too many entries", // 3
        "Missing entries ", // 4
        "Table %s already exists!", // 5
        "Column %s already exists!", // 6
        "Cannot drop Primary Attribute %s" ,// 7
        "Duplicate attribute \"%s\"", // 8
        "Ambiguous column \"%s\", belongs to multiple tables", // 9
        "Table \"%s\" does not exist", // 10
        "No records to \"%s\"", // 11
        "Unable to load table data. Memory corrupted" // 12
    };

    private int errorCode;

    private String arg;
    private String message = "Table Error:";

    public TableException(int errorCode, String arg ){
        super();
        this.arg = arg;
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

    public String getArg() {
        return arg;
    }
}
