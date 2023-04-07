public class ConditionalException extends Exception {
    private String[] errorMessages = new String[]{
            "Unknown Table: \"%s\"", // 1

    };
    private String message = "SelectError:";

    public ConditionalException(int errorCode, String arg){
        super();
        this.message = String.format(errorMessages[errorCode - 1], arg);
    }

    @Override
    public String getMessage() {
        return this.message;
    }


}
