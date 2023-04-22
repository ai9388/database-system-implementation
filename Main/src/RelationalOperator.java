public class RelationalOperator extends Conditional{

    /**
     * the left conditional object
     */
    Conditional left;

    /**
     * the right conditional object
     */
    Conditional right;

    /**
     * the operator used in this relation
     */
    String operator;

    /**
     * a very small number
     */
    int small = -100000;

    /**
     * greater than operator
     */
    String GT = ">";

    /**
     * greater than or equal operator
     */
    String GTE = ">=";

    /**
     * less than operator
     */
    String LT = "<";

    /**
     * less than or equal operator
     */
    String LTE = "<=";

    /**
     * equal operator
     */
    String E = "=";

    /**
     * not equal operator
     */
    String NE = "!=";

    /**
     * creates the relational operator object
     * @param left the conditional
     * @param right right conditional
     * @param operator the operator used
     */
    public RelationalOperator(Conditional left, Conditional right, String operator){
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    /**
     * evaluates a given record to see if it meets the requirements
     * @param record the record to evaluate
     * @return a (boolean) object with the result
     * @throws ConditionalException it the operands are invalid
     * Possible expressions
     * attribute <operator> attribute [ types must match ]
     * attribute <operator> value [values must be the same type as the attribute]
     */
    @Override
    public Object evaluate(Record record) throws ConditionalException {

        AttributeOperand ao;
        Attribute a;
        Object valLeft;
        Object valRight = null;

        if(left instanceof AttributeOperand){
            ao = (AttributeOperand) left;
            a = ao.getAttribute();
            valLeft = record.getValueAtColumn(a);
        }else{
            //left must be an attribute
            throw new ConditionalException(10, operator);
        }

        if(right instanceof AttributeOperand){
            AttributeOperand aor = (AttributeOperand) right;
            Attribute aR = aor.getAttribute();

            // make sure right attribute is the same type
            if(aR.getType() != a.getType()){
                // left and right attributes must be of the same type
                throw new ConditionalException(9, "");
            }
            else{
                valRight = record.getValueAtColumn(aR);
            }
        }

        else if(right instanceof ValueOperand){
            ValueOperand aor = (ValueOperand) right;
            String val = aor.getVal();

            // make sure right value is the same type
            try {
                if(Type.validateType(val, a)){ // same type if true
                    // if the type is string, remove the quotes
                    if(a.getType() == Type.VARCHAR || a.getType() == Type.CHAR){
                        val = val.substring(1, val.length()-1);
                    }
                    valRight = val;
                }
                else{
                    // left and right attributes must be of the same type
                    throw new ConditionalException(9, "");
                }
            } catch (ConstraintException e) {
                throw new RuntimeException(e);
            }
        }

        // compare the values
        int res = compareValues(valLeft, valRight, a.getType());

        // evaluate the booleans separately
        if(a.getType() == Type.BOOLEAN && (!operator.equals(E) && !operator.equals(NE))){
            throw new ConditionalException(4, operator);
        }

        // evaluate depending on the operator
        if(this.operator.equals(GT)){
            return res > 0;
        }
        else if(this.operator.equals(GTE)){
            return res > 0 || res == 0;
        }
        else if(this.operator.equals(LT)){
            return res < 0;
        }
        else if(this.operator.equals(LTE)){
            return res < 0 || res == 0;
        }
        else if(this.operator.equals(E)){
            return res == 0;
        }
        else if(this.operator.equals(NE)){
            return res != 0;
        }


        return false;
    }

    public int compareValues(Object l, Object r, Type type){
        if(type == Type.INTEGER) {
            int lInt = (int) l;
            int rInt = Integer.parseInt(r.toString());
            return Integer.compare(lInt, rInt);
        }
        else if(type == Type.DOUBLE) {
            double lD = Double.parseDouble(l.toString());
            double rD = Double.parseDouble(r.toString());
            return Double.compare(lD, rD);
        }
        else if(type == Type.CHAR || type == Type.VARCHAR){
            String lString = l.toString();
            String rString = r.toString();
            return lString.compareTo(rString);
        }
        else if(type == Type.BOOLEAN){
            boolean lb = Boolean.parseBoolean(l.toString());
            boolean lr = Boolean.parseBoolean(r.toString());
            return Boolean.compare(lb, lr);
        }
        return small;
    }
}
