public class AttributeOperand extends Conditional{
    private Attribute attribute;

    public AttributeOperand(Attribute attribute) {
        this.attribute = attribute;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public Object evaluate(Record record) {
        return "";
    }
}
