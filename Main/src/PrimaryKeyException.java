public class PrimaryKeyException extends Exception {
    
    private String[] errorMessages = new String[]{
        "No primary key defined",
        "Duplicate primary key for row: ",
        "More than one primary key",
        "Invalid primary key value: ",
        "Invalid primary key type: "
    };
    private String message = "Primary Key Error: ";

    public PrimaryKeyException(int errorCode, String argument){
        super();
        if(errorCode == 2 || errorCode == 4  || errorCode == 5){
            this.message += errorMessages[errorCode - 1] + argument;
        }
        else
        {
            this.message = errorMessages[errorCode - 1];
        }
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
