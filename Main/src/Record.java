import java.util.*;

public class Record implements Comparable<Record>{
    /*
     * Arraylist of entries within record
     */
    private ArrayList<Object> entries;
    private ArrayList<Attribute> attributes;
    private int pkid;
    private int size;

    public Record(ArrayList<String> values, ArrayList<Attribute> attr) {
        this.entries = new ArrayList<>();
        this.attributes = attr;
        
        for(int i = 0 ; i < values.size(); i++){
            String value = values.get(i);
            Attribute attribute = attr.get(i);
            if(attribute.isIsPrimaryKey()){
                pkid = i;
            }
            switch(attribute.getType()){
                case INTEGER:
                    entries.add(Integer.parseInt(value));
                    size += Integer.BYTES;
                    break;
                case DOUBLE:
                    entries.add(Double.parseDouble(value));
                    size += Integer.BYTES;
                    break;
                case BOOLEAN:
                    entries.add(Boolean.parseBoolean(value));
                    size += 1;
                    break;
                case VARCHAR:
                    entries.add(value);
                    size += (Character.BYTES * value.length());
                    break;
                case CHAR:
                    entries.add(value);
                    size += Character.BYTES * attribute.getN();
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

    public int getSize(){
        return size;
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
        byte[] bb = new byte[0];

        for (Object entry : this.entries) 
        {
            if (entry instanceof Integer)
            {
                Type.concat(bb, Type.convertIntToByteArray( (int) entry));
            }
            else if (entry instanceof Double)
            {
                Type.concat(bb, Type.convertDoubleToByteArray( (double) entry));
            }
            else if (entry instanceof Boolean)
            {
                Type.concat(bb, Type.convertBooleanToByteArray( (boolean) entry));
            }
            else if (entry instanceof String)
            {
                Type.concat(bb, Type.convertStringToByteArray( (String) entry));
            }
        }
        return bb;
    }

    @Override
    public int compareTo(Record o) {
        Object pkValue1 = this.getValueAtColumn(pkid);
        Object pkValue2 = o.getValueAtColumn(pkid);

        // INT
        if(pkValue1 instanceof Integer){
            return Integer.compare((int)pkValue1, (int)pkValue2);
        }

        // DOUBLE
        else if(pkValue1 instanceof Double){
            return Double.compare((double)pkValue1, (double)pkValue2);
        }

        // BOOLEAN
        else if(pkValue1 instanceof Boolean){
            return Boolean.compare((boolean)pkValue1, (boolean)pkValue2);
        }

        // String
        else{
            return String.valueOf(pkValue1).compareTo(String.valueOf(pkValue2));
        }
    }  
}
