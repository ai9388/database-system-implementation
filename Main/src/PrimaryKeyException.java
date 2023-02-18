public class PrimaryKeyException extends Exception {
    
    private String[] errorMessages = new String[]{
        "No primary key defined",
        "Duplicate primary key for row ",
        "More than one primary key"
    };
    private String message = "Primary Key Error: ";

    public PrimaryKeyException(int errorCode, String argument){
        super();
        this.message += errorMessages[errorCode - 1];
        if(errorCode == 2){
            this.message += argument;
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
