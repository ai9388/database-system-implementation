public class TableException extends Exception{
    private String[] errorMessages = new String[]{
        "Invalid Column Name: ",
    };
    private String message = "Table Error:";

    public TableException(int errorCode, String arg){
        super();
        this.message = errorMessages[errorCode - 1] + arg;

    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
