import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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

    @Before
    public void setup() throws PrimaryKeyException{
        // test adding a record to a table
        columns = new String[]{"name", "lastname", "age"};
        types = new Type[]{Type.VARCHAR, Type.VARCHAR, Type.INTEGER};
        attributes = new ArrayList<>();
        createAttributes(0 , columns, types);
        Attribute pka = attributes.get(0);
        pb = new PageBuffer("./db", 5, 100);
        ts = new TableSchema("person", attributes);
        ArrayList<String> d1 = new ArrayList<>(Arrays.asList(new String[]{"carly", "maggiolo", "21"}));
        ArrayList<String> d2 = new ArrayList<>(Arrays.asList(new String[]{"mark", "smith", "24"}));
        ArrayList<String> d3= new ArrayList<>(Arrays.asList(new String[]{"LongName", "ReallyLongLastName", "50"}));
        ArrayList<String> d4= new ArrayList<>(Arrays.asList(new String[]{"Zendaya", "WhateverHerLastNameIs", "25"}));
        ArrayList<String> d5= new ArrayList<>(Arrays.asList(new String[]{"Benito", "bunny", "27"}));
        ArrayList<String> d6= new ArrayList<>(Arrays.asList(new String[]{"ferxxo", "feid", "23"}));
        ArrayList<String> d7= new ArrayList<>(Arrays.asList(new String[]{"pancon", "queso", "100"}));
        ArrayList<String> d8= new ArrayList<>(Arrays.asList(new String[]{"George", "Lopez", "56"}));
        Record r1 = new Record(d1, attributes);
        Record r2 = new Record(d2, attributes);
        Record r3 = new Record(d3, attributes);
        Record r4 = new Record(d4, attributes);
        Record r5 = new Record(d5, attributes);
        Record r6 = new Record(d6, attributes);
        Record r7 = new Record(d7, attributes);
        Record r8 = new Record(d8, attributes);

        pb.insertRecord(ts, r1);
        pb.insertRecord(ts, r2);
        pb.insertRecord(ts, r3);
        pb.insertRecord(ts, r4);
        pb.insertRecord(ts, r5);
        pb.insertRecord(ts, r6);
    }

    @Test
    public void test2(){
        System.out.println(pb.displayPages());
        System.out.println(String.format("Table %s has %d pages: %s", ts.getName(), ts.getNumberOfPages(), Arrays.toString(ts.getPageIds().toArray())));
    }


    



    public void createAttributes(int pkidx, String[] c, Type[] t){
        for(int i = 0; i < columns.length; i++){
            Attribute a = new Attribute(c[i], t[i], i == pkidx, false, false, types[i] == Type.VARCHAR || types[i] == Type.CHAR ? 20 : 0);
            attributes.add(a); 
        }

        // for(Attribute a: attributes){
        //     System.out.println(a);
        // }
    }

    
    
}
