public class TableException extends Exception{
    private String[] errorMessages = new String[]{
        "Column Error: Invalid name",
    };
    private String message = "Table Error:";

    public TableException(int errorCode){
        super();
        this.message = errorMessages[errorCode - 1];

    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
