public class PrimaryKeyException extends Exception {
    
    private String[] errorMessages = new String[]{
        "No primary key defined", // 1
        "Duplicate primary key for row: \"%s\"", // 2
        "More than one primary key", // 3
        "Invalid primary key value: \"%s\"", // 4
        "Invalid primary key type: \"%s\"", // 5
        "Primary attribute \"%s\" cannot be updated" // 6

    };
    private String message = "";

    public PrimaryKeyException(int errorCode, String argument){
        super();
        if(errorCode == 2 || errorCode == 4  || errorCode == 5){
            this.message += errorMessages[errorCode - 1] + argument;
        }
        else
        {
            this.message = String.format(errorMessages[errorCode - 1], argument);
        }
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
