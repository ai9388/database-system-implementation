public class LogicalOperator extends Conditional{
    private Conditional left;
    private Conditional right;

    String operator;

    String AND = "and";
    String OR = "OR";

    public LogicalOperator(Conditional left, Conditional right, String operator){
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public Object evaluate(Record record) {

        boolean leftRes = false;
        boolean rightRes = false;

        // left is an attribute conditional - must be boolean
        if(left instanceof AttributeOperand) {
            AttributeOperand ao = (AttributeOperand) left;
            if (ao.getAttribute().getType() != Type.BOOLEAN) {
                // TODO: error
            }
            leftRes = (boolean) record.getValueAtColumn(ao.getAttribute());
        }

        // lfet cannot be a value
        else if(left instanceof ValueOperand){
            // TODO: error
        }

        else { // the left is a conditional
            leftRes = (boolean) left.evaluate(record);
        }


        // the right is an attribute - must be boolean
        if(right instanceof AttributeOperand) {
            AttributeOperand aor = (AttributeOperand) right;
            if(aor.getAttribute().getType() != Type.BOOLEAN){
                // TODO
            }
            rightRes = (boolean)record.getValueAtColumn(aor.getAttribute());
        }

        // the right is a value operand. That value must be either true or false (boolean);
        else if(right instanceof ValueOperand){
            ValueOperand vo = (ValueOperand) right;
            String val = vo.getVal();

            if(val.equalsIgnoreCase("true")){
                rightRes = true;
            }
            else if(val.equalsIgnoreCase("false")){
                rightRes = false;
            }
            else{
                // TODO: error
            }
        }

        else{ // the right is a conditional
            rightRes = (boolean) right.evaluate(record);
        }

        if(operator == AND){
            return leftRes && rightRes;
        }else{
            return leftRes || rightRes;
        }

    }
}
