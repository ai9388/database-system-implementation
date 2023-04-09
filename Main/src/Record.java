import java.nio.ByteBuffer;
import java.util.*;

public class Record implements Comparable<Record>{
    /*
     * Arraylist of entries within record
     */
    private ArrayList<Object> entries;
    private int pkid;
    private int size;

    ArrayList<Attribute> attr;

    public Record(ArrayList<String> values, ArrayList<Attribute> attr) {
        this.entries = new ArrayList<>();
        
        for(int i = 0 ; i < values.size(); i++){
            String value = values.get(i);
            Attribute attribute = attr.get(i);
            if(attribute.isIsPrimaryKey()){
                pkid = i;
            }
            switch(attribute.getType()){
                case INTEGER:
                    if (value.equals("null")) {
                        entries.add(value);
                    } else {
                        entries.add(Integer.parseInt(value));
                    }
                    size += Integer.BYTES;
                    break;
                case DOUBLE:
                    if (value.equals("null")) {
                        entries.add(value);
                    } else {
                        entries.add(Double.parseDouble(value));
                    }
                    size += Double.BYTES;
                    break;
                case BOOLEAN:
                    if (value.equals("null")) {
                        entries.add(value);
                    } else {
                        entries.add(Boolean.parseBoolean(value));
                    }
                    size += 1;
                    break;
                case VARCHAR:
                    entries.add(value);
                    size += Integer.BYTES;
                    size += (Character.BYTES * value.length());
                    break;
                case CHAR:
                    entries.add(value);
                    size += Character.BYTES * attribute.getN();
                    break;
            }
        }

        this.attr = attr;
    }

    public Record(ArrayList<Object> entries, ArrayList<Attribute> attr, boolean exits){
        this.entries = entries;

        int i = 0;
        for(Object entry: entries){
            if(entry instanceof Integer){
                this.size += 4;
            }
            else if(entry instanceof Double){
                this.size += Double.BYTES;
            }
            else if(entry instanceof Boolean){
                this.size += 1;
            }
            else if(entry instanceof String){
                if (attr.get(i).getType() == Type.VARCHAR)
                {
                    this.size += Integer.BYTES;
                }
                this.size += (Character.BYTES * ((String)(entry)).length());
            }
            i += 1;
        }

        this.attr = attr;
    }

    /**
     * returns the value of this record at a specific column
     * @param idx attribute containing the name of the column
     */
    public Object getValueAtColumn(int idx){
        return this.entries.get(idx);
    }

    public Object getValueAtColumn(Attribute attribute){
        Object value = null ;

        for(int i = 0; i < attr.size(); i++){
            if(attr.get(i).equals(attribute)){
                value = getValueAtColumn(i);
                break;
            }
        }

        return value;
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


    public ArrayList<Object> getSubset(ArrayList<Attribute> subset){
        ArrayList<Object> subsetRecords = new ArrayList<>();
        for(int i = 0; i < subset.size(); i++){
            Attribute a = subset.get(i);
            if(a != null){
                subsetRecords.add(this.getValueAtColumn(a));
            }
        }

        return subsetRecords;
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

    /**
     * returns the value of the primary entry
     * @return
     */
    public Object getPrimaryObject(){
        return getValueAtColumn(pkid);
    }

    @Override
    public String toString() {
        // TODO: format record and make it pretty **
        String str = "";
        for (Object entry : this.entries) 
        {
            str += entry + ", ";
        }
        return "("  + str + "): " + this.size + " bytes";
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

    public ByteBuffer getRecordAsBytes()
    {
        ByteBuffer bb = ByteBuffer.allocate(this.size);
        for (int i = 0; i < entries.size(); i++)
        {
            Object entry = entries.get(i);
            if (entry instanceof Integer)
            {
                bb.putInt((int)entry);
            }
            else if (entry instanceof Double)
            {
                bb.putDouble((double)entry);
            }
            else if (entry instanceof Boolean)
            {
                boolean temp = (boolean)entry;
                bb.put((byte)(temp? 1 : 0));
            }
            else if (entry instanceof String word)
            {
                if(attr.get(i).getType() == Type.VARCHAR) {
                    // add the length of the string plus its
                    bb.putInt(word.length());
                }

                // for char it does not matter
                for(int x = 0; x < word.length(); x++){
                    char c = word.charAt(x);
                    bb.putChar(c);
                }
            }
        }

        return bb;
    }

    @Override
    public int compareTo(Record o) {
        return compareAtIndex(o, pkid);
    }

    public int compareAtIndex(Record other, int index){
        Object pkValue1 = this.getValueAtColumn(index);
        Object pkValue2 = other.getValueAtColumn(index);

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
            return String.valueOf(pkValue1).toLowerCase().compareTo(String.valueOf(pkValue2).toLowerCase());
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean res = false;
        if(obj instanceof Record r){
            res = this.compareTo(r) == 0;
        }
        return res;
    }
}
