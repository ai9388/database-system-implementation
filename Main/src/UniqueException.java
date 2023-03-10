public class UniqueException extends Exception {

    private String[] errorMessages = new String[]{
            "Duplicate unique value can't be inserted: "
    };
    private String message = "";

    public UniqueException(int errorCode, String argument){
        super();
        this.message = errorMessages[errorCode - 1] + argument;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
