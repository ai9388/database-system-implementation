import org.junit.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class TableTest {
    String[] columns;
    String[] columns2;
    Type[] types;
    Type[] types2;
    Attribute a1;
    Attribute a2;
    ArrayList<Attribute> attributes;
    ArrayList<Attribute> attributes2;
    int wordMaxLen = 20;
    Table table;
    Table table2;

    @Before
    public void setupTables() throws PrimaryKeyException {
        setupTable1();
        setupTable2();
    }

    public void setupTable1() throws PrimaryKeyException {
        // test adding a record to a table
        columns = new String[] { "name", "lastname", "age" };
        types = new Type[] { Type.VARCHAR, Type.VARCHAR, Type.INTEGER };
        attributes = new ArrayList<>();
        int pkid = 0;
        createAttributes(pkid, columns, types, attributes);
        Attribute pka = attributes.get(pkid);
        table = new Table("person", attributes);
        ArrayList<String> d1 = new ArrayList<>(Arrays.asList(new String[] { "carly", "maggiolo", "21" }));
        ArrayList<String> d2 = new ArrayList<>(Arrays.asList(new String[] { "mark", "smith", "24" }));
        ArrayList<String> d3 = new ArrayList<>(Arrays.asList(new String[] { "LongName", "ReallyLongLastName", "50" }));
        ArrayList<String> d4 = new ArrayList<>(
                Arrays.asList(new String[] { "Zendaya", "WhateverHerLastNameIs", "25" }));
        ArrayList<String> d5 = new ArrayList<>(Arrays.asList(new String[] { "Benito", "bunny", "27" }));
        ArrayList<String> d6 = new ArrayList<>(Arrays.asList(new String[] { "ferxxo", "feid", "23" }));
        ArrayList<String> d7 = new ArrayList<>(Arrays.asList(new String[] { "pancon", "queso", "100" }));
        ArrayList<String> d8 = new ArrayList<>(Arrays.asList(new String[] { "George", "Lopez", "56" }));
        Page.setCapacity(200);
        Record r1 = new Record(d1, attributes);
        Record r2 = new Record(d2, attributes);
        Record r3 = new Record(d3, attributes);
        Record r4 = new Record(d4, attributes);
        Record r5 = new Record(d5, attributes);
        Record r6 = new Record(d6, attributes);
        Record r7 = new Record(d7, attributes);
        Record r8 = new Record(d8, attributes);
        table.insertRecord(r1);
        table.insertRecord(r2);
        table.insertRecord(r3);
        table.insertRecord(r4);
        table.insertRecord(r5);
        table.insertRecord(r6);
        table.insertRecord(r7);
        table.insertRecord(r8);

    }

    public void setupTable2() throws PrimaryKeyException {
        columns2 = new String[] { "id", "name", "dep" };
        types2 = new Type[] { Type.INTEGER, Type.VARCHAR, Type.VARCHAR };
        attributes2 = new ArrayList<>();
        int pkid = 0;
        createAttributes(pkid, columns2, types2, attributes2);
        a2 = attributes2.get(pkid);
        table2 = new Table("student", attributes2);
        Page.setCapacity(200);
        ArrayList<String> d1 = new ArrayList<>(Arrays.asList(new String[] { "1234", "carly", "CSCI" }));
        ArrayList<String> d2 = new ArrayList<>(Arrays.asList(new String[] { "5678", "dell", "GCIS" }));
        Record r1 = new Record(d1, attributes2);
        Record r2 = new Record(d2, attributes2);
        table2.insertRecord(r1);
        table2.insertRecord(r2);
    }

    @Test
    public void testTableSelectByColumn() {
        try {
            System.out.println(table.select(new String[] { "name", "lastname" }));
        } catch (TableException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testTableSelect() {
        System.out.println(table.selectAll());
    }

    @Test
    public void testTableSchema() {
        System.out.println(table.displayTableSchema());
    }

    @Test
    public void testTableInfo() {
        System.out.println(table.displayTableInfo());
    }

    @Test
    public void testGetRecordByPKWithValidKey() {
        String pkvalue = "carly"; // this is valid in table 1
        Record r = null;

        try {
            r = table.getRecordByPK(pkvalue);
        } catch (PrimaryKeyException e) {
            System.out.println(e.getMessage());
        }

        assertNotNull(r);
    }

    @Test
    public void testGetRecordByPKWithInvalidTypeKey() {
        String str = "cute"; // the pk for table2 is an int
        Record r = null;
        String msgExpected = new PrimaryKeyException(5, new InvalidDataTypeException(str, a2).getMessage())
                .getMessage();
        String msgGot = "";
        try {
            r = table2.getRecordByPK(str);
        } catch (PrimaryKeyException e) {
            msgGot = e.getMessage();
        }

        assertTrue(r == null && msgGot.equals(msgExpected));
    }

    @Test
    public void testGetRecordByPKWithInvalidKey() {
        String str = "cute"; // there is no key with this name
        Record r = null;
        String msgExpected = new PrimaryKeyException(4, str).getMessage();
        String msgGot = "";
        try {
            r = table.getRecordByPK(str);
        } catch (PrimaryKeyException e) {
            msgGot = e.getMessage();
        }

        assertTrue(r == null && msgGot.equals(msgExpected));
    }

    @Test
    public void testRemoveRecordByPrimaryKey() {
        // remove carly from records of table1
        String name = "carly";

        boolean res = false;

        try {
            res = table.removeRecordByPK(name);
        } catch (PrimaryKeyException e) {
            // TODO Auto-generated catch block
        } catch (InvalidDataTypeException e) {
            // TODO Auto-generated catch block
        }

        assertTrue(res);
    }

    @Test
    public void testRemoveRecordByPrimaryKeyWithInvalidKey() {
        // remove carly from records of table1
        String name = "jackson";
        boolean res = false;
        String msgExpected = new PrimaryKeyException(4, name).getMessage();
        String msgGot = "";

        try {
            res = table.removeRecordByPK(name);
        } catch (PrimaryKeyException e) {
            msgGot = e.getMessage();
        } catch (InvalidDataTypeException e) {
            // TODO Auto-generated catch block
        }

        assert (res == false && msgExpected.equals(msgGot));
    }

    @Test
    public void testInsertRecordValid(){
        // this is a valid record that be inserted into table
        boolean res = false;
        String[] d1 = new String[] { "4563", "madelina", "FINC" };
        try {
            res = table2.insertRecord(d1);
        } catch (InvalidDataTypeException e) {
            // this shouldn't fail
        } catch (PrimaryKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testInsertRecordInValidPKType(){
        // this is a valid record that be inserted into table
        boolean res = false;
        String[] d1 = new String[] { "false", "madelina", "FINC" };
        String msgExpected = new InvalidDataTypeException(d1, attributes2).getMessage();
        String msgGot = "";

        try {
            res = table2.insertRecord(d1);
        } catch (InvalidDataTypeException e) {
            // this should fail
            msgGot = e.getMessage();
        } catch (PrimaryKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        assertTrue(!res && msgExpected.equals(msgGot));
    }

    @Test
    public void testInsertRecordDuplicatePK(){
         // there is a record with this pk value
         boolean res = false;
         String[] d1 = new String[] { "1234", "madelina", "FINC" };
         String msgExpected = new PrimaryKeyException(2, "0").getMessage();
         String msgGot = "";
 
         try {
             res = table2.insertRecord(d1);
         } catch (InvalidDataTypeException e) {
             // this should fail
             msgGot = e.getMessage();
         } catch (PrimaryKeyException e) {
             // TODO Auto-generated catch block
             msgGot = e.getLocalizedMessage();
         }
         assertTrue(!res && msgExpected.equals(msgGot));
    }

    @Test
    public void testUpdateTableByPK(){
        System.out.println(table.selectAll());
        try {
            table.updateRecordByPK("carly", "age", "15");
            assert(table.getRecordByPK("carly").getValueAtColumn(2).equals(15));
        } catch (TableException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } catch (PrimaryKeyException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        } catch (InvalidDataTypeException e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        }
        
    }

    public void createAttributes(int pkidx, String[] c, Type[] t, ArrayList<Attribute> attr) {
        for (int i = 0; i < c.length; i++) {
            Attribute a = new Attribute(c[i], t[i], i == pkidx ? true : false,
                    t[i] == Type.VARCHAR || t[i] == Type.CHAR ? 20 : 0);
            attr.add(a);
        }

        attr.get(0).setIsPrimaryKey(true);

        // for(Attribute a: attributes){
        // System.out.println(a);
        // }
    }
}
