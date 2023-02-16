import java.util.*;

public class Record {
    /*
     * Arraylist of entries within record
     */
    private ArrayList<String> entries;

    /*
     * data by columnName
     */
    private HashMap<String, String> dataByColumn;

    public Record(ArrayList<String> entries, ArrayList<Attribute> attributes) throws InvalidDataTypeException
    {
        this.entries = entries;

        // insert all values into map
        for (int i = 0; i < entries.size(); i++) {
            dataByColumn.put(attributes.get(i).getName(), entries.get(i));
        }
    }

    /**
     * returns the value of this record at a specific column
     * @param column the name of the column
     */
    public String getValueAtColumn(String column){
        return dataByColumn.get(column);
    }
    
    /**
     * sets the data collection
     * @param entries the data to set
     */
    public void setEntries(ArrayList<String> newData) {
        this.entries = newData;
    }

    /**
     * returns all the data
     * @return
     */
    public ArrayList<String> getEntries() {
        return entries;
    }

    /**
     * update value at column and update data collection
     * @param column column where update is happening
     * @param newValue new value replacing old at column
     */
    public void updateByColumn(String column, String newValue){
        dataByColumn.replace(column, dataByColumn.get(column), newValue);
        entries = new ArrayList<>(dataByColumn.values());
    }

}
