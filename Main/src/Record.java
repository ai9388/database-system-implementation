import java.util.*;

public class Record {
    /*
     * Arraylist of entries within record
     */
    private ArrayList<Object> entries;
    private ArrayList<Attribute> attributes;

    public Record(ArrayList<String> values, ArrayList<Attribute> attr){
        this.entries = new ArrayList<>();
        this.attributes = attr;
        
        for(int i = 0 ; i < values.size(); i++){
            String value = values.get(i);
            Attribute attribute = attr.get(i);
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
                case VARCHAR:
                    entries.add(value);
                    break;
                case CHAR:
                    entries.add(value);
                    break;
            }
        }
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

    public byte[] recordToBytes()
    {
        StorageManager sm = new StorageManager();

        byte[] bb = new byte[0];

        for (Object entry : this.entries) 
        {
            if (entry instanceof Integer)
            {
                sm.concat(bb, sm.convertIntToByteArray( (int) entry));
            }
            else if (entry instanceof Double)
            {
                sm.concat(bb, sm.convertDoubleToByteArray( (double) entry));
            }
            else if (entry instanceof Boolean)
            {
                sm.concat(bb, sm.convertBooleanToByteArray( (boolean) entry));
            }
            else if (entry instanceof String)
            {
                sm.concat(bb, sm.convertStringToByteArray( (String) entry));
            }
        }
        return bb;
    }  
}
