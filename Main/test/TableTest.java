import org.junit.*;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class TableTest {
    String[] columns;
    Type[] types;
    int[] pkidx;
    ArrayList<Attribute> attributes;
    int wordMaxLen = 20;
    Table table;

    @Before
    public void testCreateTable(){
        // test adding a record to a table
        columns = new String[]{"name", "lastname", "age"};
        types = new Type[]{Type.VARCHAR, Type.VARCHAR, Type.INTEGER};
        pkidx = new int[]{1};
        attributes = new ArrayList<>();
        createAttributes();
        Attribute pka = attributes.get(0);
        table = new Table("person", attributes);
    }

    @Test
    public void testAddRecordAsObject(){
        // test to add a record
        ArrayList<String> d1 = new ArrayList<>(Arrays.asList(new String[]{"carly", "maggiolo", "21"}));
        ArrayList<String> d2 = new ArrayList<>(Arrays.asList(new String[]{"mark", "smith", "24"}));
        Record r1 = new Record(d1, attributes);
        Record r2 = new Record(d2, attributes);
        table.insertRecord(r1);
        table.insertRecord(r2);
        assertTrue(table.getNumberOfRecords() == 2);
    }

    @Test
    public void testAddRecordAsArray() throws InvalidDataTypeException{
        // test to add a record
        String[] d1 = new String[]{"jan", "Gomez", "14"};
        String[] d2 = new String[]{"Alice", "Chen", "56"};
        table.insertRecord(d1);
        table.insertRecord(d2);
        assertTrue(table.getNumberOfRecords() == 2);
    }

    @Test
    public void testTableSelect(){
        try {
            System.out.println(table.select(new String[]{"name", "lastname"}));
        } catch (TableException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testTableRecords(){
        System.out.println(table.displayTableSchema());
    }

    public void createAttributes(){
        for(int i = 0; i < columns.length; i++){
            attributes.add(new Attribute(columns[i], types[i], Arrays.asList(pkidx).contains(i), 
            types[i] == Type.VARCHAR || types[i] == Type.CHAR ? 20 : 0));
        }

        attributes.get(0).setIsPrimaryKey(true);

        // for(Attribute a: attributes){
        //     System.out.println(a);
        // }
    }

    public static void main(String[] args) {
        TableTest test = new TableTest();
        test.testCreateTable();
        test.testAddRecordAsObject();
        test.testTableRecords();
        test.testTableSelect();
    }
}
