public class ValueOperand extends Conditional{
    private String val;

    public ValueOperand(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public boolean getValAsBoolean(){
        Boolean res = null;
        if(val.equalsIgnoreCase("true")){
            res = true;
        }
        else if(val.equalsIgnoreCase("false")){
            res = false;
        }
        return res;
    }

    @Override
    public Object evaluate(Record record) {
        return null;
    }
}
