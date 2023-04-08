
import java.util.Arrays;
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
        List<String> postfix = toPostfix(expression, attributes);
        Conditional conditional = tokenize(postfix, attributes);
        if(conditional == null){
            // ERROR: unable to interpret where clause
        }
        return conditional;
    }

    public static List<String> toPostfix(String expression, ArrayList<Attribute> attributes) {
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

//    public static void main(String[] args) {
//        String expression = "age > 50";
//
//        String[] columns = new String[]{"student", "person", "age"};
//        String[] data = new String[]{"true", "false", "23"};
//        ArrayList<String> entries = new ArrayList<>(Arrays.asList(data));
//        Type[] types = new Type[]{Type.BOOLEAN, Type.BOOLEAN, Type.INTEGER};
//        ArrayList<Attribute> attributes = createAttributes(0, columns, types);
//        Conditional conditional = run(attributes, expression);
//        System.out.println(conditional.evaluate(new Record(entries, attributes)));
//    }
//
//    public static ArrayList<Attribute> createAttributes(int pkidx, String[] c, Type[] t) {
//        ArrayList<Attribute> attributes = new ArrayList<>();
//        for (int i = 0; i < c.length; i++) {
//            Attribute a = new Attribute(c[i], t[i], i == pkidx ? true : false, false, false,
//                    t[i] == Type.VARCHAR || t[i] == Type.CHAR ? 30 : 0);
//            attributes.add(a);
//        }
//
//        attributes.get(pkidx).setIsPrimaryKey(true);
//        return attributes;
//    }
//    public boolean evaluateClause(Record record){
//        return (boolean)evaluate(record);
//    }

    public static int getAttribute(List<Attribute> attributes, String name){
        int idx = -1;

        for (int i = 0; i < attributes.size(); i++) {
            Attribute a = attributes.get(i);
            if(a.getName().equals(name)){
                idx = i;
                break;
            }
        }

        return idx;
    }
    public boolean evaluateRecord(Record record) throws ConditionalException{
        return (boolean) evaluate(record);
    }

    public abstract Object evaluate(Record record) throws ConditionalException;
}

