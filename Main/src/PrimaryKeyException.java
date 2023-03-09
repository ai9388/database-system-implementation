public class PrimaryKeyException extends Exception {
    
    private String[] errorMessages = new String[]{
        "No primary key defined", // 1
        "Duplicate primary key for row: ", // 2
        "More than one primary key", // 3
        "Invalid primary key value: ", // 4
        "Invalid primary key type: " // 5
    };
    private String message = "";

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
