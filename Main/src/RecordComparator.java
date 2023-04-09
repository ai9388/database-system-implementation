import java.util.*;

public class RecordComparator implements Comparator<Record> {

    Attribute attribute;

    public RecordComparator(Attribute attribute){
        this.attribute = attribute;
    }

    @Override
    public int compare(Record o1, Record o2) {
        Object val1 = o1.getValueAtColumn(attribute);
        Object val2 = o2.getValueAtColumn(attribute);
        
        if(attribute.getType() == Type.BOOLEAN){
            Boolean b1 = (Boolean) val1;
            Boolean b2 = (Boolean) val2;
            return Boolean.compare(b1, b2);
        }
        else if(attribute.getType() == Type.INTEGER){
            Integer int1 = (Integer) val1;
            Integer int2 = (Integer) val2;
            return Integer.compare(int1, int2);
        }
        else if((attribute.getType() == Type.VARCHAR) || attribute.getType() == Type.CHAR){
            String str1 = val1.toString();
            String str2 = val2.toString();
            return str1.compareTo(str2);
        }
        else if(attribute.getType() == Type.DOUBLE){
            Double do1 = (Double) val1;
            Double do2 = (Double) val2;
            return Double.compare(do1, do2);
        }

        return 0;
    }
    
}
