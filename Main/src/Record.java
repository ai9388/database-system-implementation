import java.util.*;

public class Record {
    
    private Attribute attribute;
    private Map<Attribute, String> data;

    public Record(Attribute attr, Map<Attribute, String> data)
    {
        this.attribute = attr;
        this.data = data;
    }
    
    /**
     * @return Attribute return the attribute
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * @param attribute the attribute to set
     */
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    /**
     * @return Map<Attribute, String> return the data
     */
    public Map<Attribute, String> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Map<Attribute, String> data) {
        this.data = data;
    }

    public void validateDataType() {
        System.out.println("validating!");
    }

}
