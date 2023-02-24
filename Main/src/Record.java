import java.util.*;

public class Record {
    /*
     * Arraylist of entries within record
     */
    private ArrayList<Object> entries;

    public Record(String[] values, ArrayList<Attribute> attributes){
        ArrayList<Object> entries = new ArrayList<>();
        
        for(int i = 0 ; i < values.length; i++){
            String value = values[i];
            Attribute attribute = attributes.get(i);
            switch(attribute.getType()){
                case INTEGER:
                    entries.add(Integer.parseInt(value));
                    break;
                case DOUBLE:
                    entries.add(Double.parseDouble(value));
                    break;
                case BOOLEAN:
                    entries.add(Boolean.parseBoolean(value));
                    break;
                default:
                    entries.add(value);
                    break;
            }
        }

        this.entries = entries;
    }

    public Record(ArrayList<Object> entries){
        this.entries = entries;
        
    }
    /**
     * returns the value of this record at a specific column
     * @param attribute attribute containing the name of the column
     */
    public Object getValueAtColumn(int idx){
        return this.entries.get(idx);
    }

    /**
     * sets the entries array to a new collection
     * @param newEntries the data to set
     */
    public void setEntries(ArrayList<Object> newEntries) {
        this.entries = newEntries;
    }

    /**
     * returns all the entries as an array
     * @return array of entries
     */
    public ArrayList<Object> getEntries() {
        return entries;
    }

    /**
     * update value at column
     * @param index column index where update is happening
     * @param newValue new value replacing old at column
     */
    public void updateAtColumn(int index, Object newValue){
        this.entries.set(index, newValue);
    }

    @Override
    public String toString() {
        // TODO: format record and make it pretty **
        return "";
    }

    /**
     * compacts the record so it can be written to page
     * @return
     */
    public String compact(){
        String info = "";

        for(Object entry: entries){

            if(entry instanceof String){
                String temp = (String) entry;
                info += temp.length() + temp.strip();
                continue;
            }

            info += entry.toString();
        }

        return info;
    }
}
