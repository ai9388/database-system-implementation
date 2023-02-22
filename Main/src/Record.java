import java.util.*;

public class Record {
    /*
     * Arraylist of entries within record
     */
    private ArrayList<String> entries;

    /*
     * data by attribute object
     */
    private HashMap<Attribute, String> entriesByAttribute;

    public Record(ArrayList<String> entries, ArrayList<Attribute> attributes){
        this.entries = entries;
        this.entriesByAttribute = new HashMap<>();
        // insert all values into map
        for (int i = 0; i < entries.size(); i++) {
            entriesByAttribute.put(attributes.get(i), entries.get(i));
        }
    }

    /**
     * returns the value of this record at a specific column
     * @param attribute attribute containing the name of the column
     */
    public String getValueAtColumn(Attribute attribute){
        String value = "";
        for (Attribute a : entriesByAttribute.keySet()) {
            if(a.getName().equals(attribute.getName())){
                value = entriesByAttribute.get(a);
            }
        }

        return value;
    }

    /**
     * sets the data collection
     * @param newEntries the data to set
     */
    public void setEntries(ArrayList<String> newEntries) {
        this.entries = newEntries;
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
    public void updateByColumn(Attribute attribute, String newValue){
        entriesByAttribute.replace(attribute, entriesByAttribute.get(attribute), newValue);
        entries = new ArrayList<>(entriesByAttribute.values());
    }

    @Override
    public String toString() {
        // TODO: format record and make it pretty **
        return String.join(" ", entries);
    }

    /**
     * compacts the record so it can be written to page
     * @return
     */
    public String compact(){
        String info = "";

        for(String data: entries){
            if(Type.identifyType(data).equals(Type.VARCHAR) || Type.identifyType(data).equals(Type.CHAR)){
                info += (data.length() + data);
                continue;
            }
            info += data;
        }

        return info;
    }
}
