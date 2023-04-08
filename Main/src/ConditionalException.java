public class ConditionalException extends Exception {
    private String[] errorMessages = new String[]{
            "Unknown Table: \"%s\"", // 1
            "Conditional expression in where clause must contain operators", //2
            "Invalid OrderBy clause", // 3
            "Operator \"%s\" does not apply to Boolean", // 4
            "Operator \"%s\" missing operands", // 5
            "Operands for \"\"%s\"\" must be (true/false)", // 6
            "Left operand cannot be a literal value", // 7
            "Logical operands must equate to a boolean", // 8
            "Data types of relational operands must match", // 9
            "left value for relational operator \"%s\" must be an attribute", // 10
            "unable to interpret where clause", //11
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
