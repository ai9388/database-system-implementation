import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class PageTest {

    String[] columns;
    Type[] types;
    int[] pkidx;
    ArrayList<Attribute> attributes;
    int wordMaxLen = 20;
    PageBuffer pb;

    TableSchema ts;

    Parser parser;

    StorageManager sm;

    @Before
    public void setup() throws PrimaryKeyException {
        String dbname = "school";
        String path = "./Main/" + dbname;
        int pageSize = 100;
        int bufferSize = 15;
        this.parser = new Parser(dbname, path, pageSize, bufferSize);
        sm = this.parser.storageManager;
        pb = sm.pageBuffer;

        // test adding a record to a table
        columns = new String[]{"name", "lastname", "age"};
        types = new Type[]{Type.VARCHAR, Type.VARCHAR, Type.INTEGER};
        attributes = new ArrayList<>();
        createAttributes(0, columns, types);
        Attribute pka = attributes.get(0);

//        ArrayList<String> d1 = new ArrayList<>(Arrays.asList(new String[]{"carly", "maggiolo", "21"}));
//        ArrayList<String> d2 = new ArrayList<>(Arrays.asList(new String[]{"mark", "smith", "24"}));
//        ArrayList<String> d3 = new ArrayList<>(Arrays.asList(new String[]{"LongName", "ReallyLongLastName", "50"}));
//        ArrayList<String> d4 = new ArrayList<>(Arrays.asList(new String[]{"Zendaya", "WhateverHerLastNameIs", "25"}));
//        ArrayList<String> d5 = new ArrayList<>(Arrays.asList(new String[]{"Benito", "bunny", "27"}));
//        ArrayList<String> d6 = new ArrayList<>(Arrays.asList(new String[]{"ferxxo", "feid", "23"}));
//        ArrayList<String> d7 = new ArrayList<>(Arrays.asList(new String[]{"pancon", "queso", "100"}));
//        ArrayList<String> d8 = new ArrayList<>(Arrays.asList(new String[]{"George", "Lopez", "56"}));
//        Record r1 = new Record(d1, attributes);
//        Record r2 = new Record(d2, attributes);
//        Record r3 = new Record(d3, attributes);
//        Record r4 = new Record(d4, attributes);
//        Record r5 = new Record(d5, attributes);
//        Record r6 = new Record(d6, attributes);
//        Record r7 = new Record(d7, attributes);
//        Record r8 = new Record(d8, attributes);

        String[] d1 = new String[]{"carly", "maggiolo", "21"};
        String[] d2 = new String[]{"mark", "smith", "24"};
        String[] d3 = new String[]{"LongName", "ReallyLongLastName", "50"};
        String[] d4 = new String[]{"Zendaya", "WhateverHerLastNameIs", "25"};
        String[] d5 = new String[]{"Benito", "bunny", "27"};
        String[] d6 = new String[]{"ferxxo", "feid", "23"};
        String[] d7 = new String[]{"pancon", "queso", "100"};
        String[] d8 = new String[]{"George", "Lopez", "56"};

        try {
            // create the table
            String table_name = "person";
            sm.createTable(table_name, attributes);

            // insert records
            sm.insertRecord(table_name, d1);
            sm.insertRecord(table_name, d2);
            sm.insertRecord(table_name, d3);
            sm.insertRecord(table_name, d4);
            sm.insertRecord(table_name, d5);
            sm.insertRecord(table_name, d6);
            sm.insertRecord(table_name, d7);
            sm.insertRecord(table_name, d8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void createAttributes(int pkidx, String[] c, Type[] t) {
        for (int i = 0; i < c.length; i++) {
            Attribute a = new Attribute(c[i], t[i], i == pkidx ? true : false, false, false,
                    t[i] == Type.VARCHAR || t[i] == Type.CHAR ? 30 : 0);
            attributes.add(a);
        }

        attributes.get(0).setIsPrimaryKey(true);

        // for(Attribute a: attributes){
        // System.out.println(a);
        // }
    }

    public void printPages(ArrayList<Page> pages){

        if(pages.size() == 0){
            System.out.println("no pages");
            return;
        }
        for(Page page: pages){
            System.out.println(page);
        }
    }

    @Test
    public void TestWritePageWithOneRecords() {

        Page page = pb.getPages().get(0);
        pb.writePage(page);
        System.out.println(page);

        page = pb.getPages().get(1);
        pb.writePage(page);
        System.out.println(page);
    }

//    @Test public void testReadPage(){
//        Page page = null;
//        int id = 3;
//        String tableName = "person";
//
//        page = pb.getPage(tableName, id);
//        System.out.println(page);
//    }

    @Test
    public void TestDrop(){
        try {
            sm.dropTable("person");
        } catch (TableException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void purge(){
        pb.purge();
        System.out.println(pb.displayPages());
    }

//    @Test
//    public void testGetAllRecords() throws TableException {
//        TableSchema ts = sm.db.getTable("person");
////        ArrayList<Record> records = sm.loadRecords(ts);
//
//        for(Record r: records){
//            System.out.println(r);
//        }
//    }
}