public class PrimaryKeyException extends Exception {
    
    private String[] errorMessages = new String[]{
        "No primary key defined",
        "Duplicate primary key for row ",
        "More than one primary key"
    };
    private String message = "Primary Key Error: ";

    public PrimaryKeyException(int errorCode, String argument){
        super();
        this.message += errorMessages[errorCode];
        if(errorCode == 2){
            this.message += argument;
        }

    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
