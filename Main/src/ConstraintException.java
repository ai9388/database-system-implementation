public class ConstraintException extends Exception {

    private String[] errorMessages = new String[]{
            "Duplicate unique value can't be inserted: %s",
            "Column \"%s\" cannot be null"
    };
    private String message = "";

    public ConstraintException(int errorCode, String argument){
        super();
        this.message = String.format(errorMessages[errorCode - 1],argument);
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
