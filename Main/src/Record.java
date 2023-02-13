import java.util.*;

public class Record {
    /*
     * Arraylist of entries within record
     */
    private ArrayList<String> data;

    public Record(String[] values)
    {
        data = new ArrayList<String>(Arrays.asList(values));
    }

    /**
     * @param data the data to set
     */
    public void setData(ArrayList<String> newData) {
        this.data = newData;
    }

    /*
     * compare the value types with the ones from the schema
     */
    public void validateDataTypeS(ArrayList<Attribute> attributes) throws Exception{
        for (int i = 0; i < data.size(); i++) {
            Attribute attribute = attributes.get(i);
            String value = data.get(i);

            switch(attribute.getType()){
                case INTEGER:
                    try {
                        Integer.parseInt(value);
                    } catch (Exception e) {
                        // TODO: handle exception
                        // throw custom exception with message
                    }
                case DOUBLE:
                    try {
                        Double.parseDouble(value);
                    } catch (Exception e) {
                        // TODO: handle exception
                        // throw custom exception with message
                    }
                case BOOLEAN:
                    try {
                        Boolean.parseBoolean(value);
                    } catch (Exception e) {
                        // TODO: handle exception
                        // throw custom exception with message
                    }
                case CHAR:
                    try {
                        if(value.length() > attribute.getN()){
                            // throw custom exception
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        // throw custom exception with message
                    }
                case VARCHAR:
                    try {
                        if(value.length() > attribute.getN()){
                            // throw custom exception
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        // throw custom exception with message
                    }
            }
        }
    }

}
