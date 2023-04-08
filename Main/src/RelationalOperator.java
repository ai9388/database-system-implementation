
public class RelationalOperator extends Conditional{

    Conditional left;
    Conditional right;

    String operator;

    int small = -100000;
    String GT = ">";
    String GTE = ">=";
    String LT = "<";
    String LTE = "<";
    String E = "=";
    String NE = "!=";

    public RelationalOperator(Conditional left, Conditional right, String operator){
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
    @Override
    public Object evaluate(Record record) {

        AttributeOperand ao;
        Attribute a;
        Object valLeft;
        Object valRight = null;

        if(left instanceof AttributeOperand){
            ao = (AttributeOperand) left;
            a = ao.getAttribute();
            valLeft = record.getValueAtColumn(a);
        }else{
            //TODO: error: left must be an attribute
            return null;
        }

        if(right instanceof AttributeOperand){
            AttributeOperand aor = (AttributeOperand) right;
            Attribute aR = aor.getAttribute();

            // make sure right attribute is the same type
            if(aR.getType() != a.getType()){
                // TODO: ERROR: left and right attributes must be of the same type
                return null;
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
                    valRight = val;
                }
                else{
                    // TODO: ERROR: left and right attributes must be of the same type
                    return null;
                }
            } catch (ConstraintException e) {
                throw new RuntimeException(e);
            }
        }

        // compare the values
        int res = compareValues(valLeft, valRight, a.getType());

        // evaluate the booleans separately
        if(a.getType() == Type.BOOLEAN && (operator != E || operator != NE)){
            // ERROR: operator does not apply to  boolean
            return null;
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
            double lD = (Double) l;
            double rD = (Double) r;
            return Double.compare(lD, rD);
        }
        else if(type == Type.CHAR || type == Type.VARCHAR){
                String lString = (String)l;
                String rString = (String)r;
                return lString.compareTo(rString);
        }
        return small;
    }
}
