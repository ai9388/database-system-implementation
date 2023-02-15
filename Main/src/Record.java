import java.util.*;

public class Record {
    /*
     * Arraylist of entries within record
     */
    private ArrayList<String> data;

    /*
     * data by columnName
     */
    private HashMap<String, String> dataByColumn;

    public Record(String[] values, ArrayList<Attribute> attributes) throws InvalidDataTypeException
    {
        data = new ArrayList<String>(Arrays.asList(values));
        validateDataTypeS(attributes);

        // insert all values into map
        for (int i = 0; i < values.length; i++) {
            dataByColumn.put(attributes.get(i).getName(), values[i]);
        }
    }

    /*
     * returns the value of this record at a specific column
     * @param column the name of the column
     */
    public String getvalueAtColumn(String column){
        return dataByColumn.get(column);
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
    public void validateDataTypeS(ArrayList<Attribute> attributes) throws InvalidDataTypeException{
        try{
        for (int i = 0; i < data.size(); i++) {
            Attribute attribute = attributes.get(i);
            String value = data.get(i);

            // if this validation fails an exception is raised
            Type.validateType(value, attribute);
        }
        }
        catch(InvalidDataTypeException idte){
            // this means at least one of the attribute entries 
            // is incorrect
            throw new InvalidDataTypeException(data, attributes);
        }

    }

    public ArrayList<String> getData() {
        return data;
    }

}
