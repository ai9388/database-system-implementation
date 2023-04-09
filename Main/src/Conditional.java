
import java.util.*;

public abstract class Conditional{
    private static final Map<String, Integer> precedence = new HashMap<>();
    static {
        precedence.put("and", 3);
        precedence.put("or", 2);
        precedence.put("<", 4);
        precedence.put("<=", 4);
        precedence.put(">", 4);
        precedence.put(">=", 4);
        precedence.put("=", 4);
        precedence.put("!=", 4);
    }
     private ArrayList<Attribute> attributes;
    public static Conditional run(ArrayList<Attribute> attributes, String expression) throws ConditionalException {
        List<String> postfix = toPostfix(expression);
        Conditional conditional = tokenize(postfix, attributes);
        if(conditional == null){
            // ERROR: unable to interpret where clause
        }
        return conditional;
    }

    public static List<String> toPostfix(String expression) {
        List<String> outputQueue = new ArrayList<>();
        Stack<String> operatorStack = new Stack<>();
        String[] tokens = expression.split("\\s+");
        for (String token : tokens) {
            if (precedence.containsKey(token)) {
                while (!operatorStack.empty() &&
                        precedence.containsKey(operatorStack.peek()) &&
                        precedence.get(operatorStack.peek()) > precedence.get(token)) {
                    outputQueue.add(operatorStack.pop());
                }
                operatorStack.push(token);
            }
            else{
                outputQueue.add(token);
            }
        }
        while (!operatorStack.empty()) {
            outputQueue.add(operatorStack.pop());
        }
        return outputQueue;
    }

    /**
     * tokenizes the
     * @param postfixTokens
     * @param attributes
     * @return
     * @throws ConditionalException
     */
    public static Conditional tokenize(List<String> postfixTokens, ArrayList<Attribute> attributes) throws ConditionalException {

        Stack<Conditional> conditionals = new Stack<>();
        for(String token: postfixTokens){
            int idx = getAttribute(attributes, token);
            // token is an attribute name
            if(idx > -1){
                // create an attribute operand
                AttributeOperand a = new AttributeOperand(attributes.get(idx));
                conditionals.add(a);
            }

            // token is an operator
            else if(precedence.containsKey(token)){
                if(conditionals.size() < 2){
                    throw new ConditionalException(5, token);
                }
                Conditional right = conditionals.pop();
                Conditional left = conditionals.pop();
                if(token.equals("and") || token.equals("or")){
                    LogicalOperator andOr = new LogicalOperator(left, right, "and");
                    conditionals.push(andOr);
                }
                else if(!token.equals("and") && !token.equals("or") && precedence.containsKey(token)){
                    RelationalOperator ro = new RelationalOperator(left, right, token);
                    conditionals.push(ro);
                }
            }
            else{
                // create an entry operand
                ValueOperand val = new ValueOperand(token);
                conditionals.add(val);
            }
        }

        return conditionals.get(0);
    }

    /**
     * method that checks if an attribute name is valid
     * based on its table name.
     * @param attributes the attributes used to verify the attribite name
     * @param name the name of the operand
     * @return int representing the index of the attribute within the array
     *          -1 if not a valid attribute
     */
    public static int getAttribute(List<Attribute> attributes, String name){
        int idx = -1;

        for (int i = 0; i < attributes.size(); i++) {
            Attribute a = attributes.get(i);
            if(a.getAlias().equals(name)){
                idx = i;
                break;
            }
        }

        return idx;
    }

    /**
     * method used to call evaluate on the child classes
     * converts the (object) result to boolean
     * @param record the record object
     * @return boolean result of the condition evaluation
     * @throws ConditionalException
     */
    public boolean evaluateRecord(Record record) throws ConditionalException{
        return (boolean) evaluate(record);
    }

    /**
     * evaluates a conditional expression based on the operator
     * @param record the record to evaluate
     * @return boolean result
     * @throws ConditionalException
     */
    public abstract Object evaluate(Record record) throws ConditionalException;
}

