import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class PageTest {

    String[] columns;
    Type[] types;
    int[] pkidx;
    ArrayList<Attribute> attributes;
    int wordMaxLen = 20;
    Table table;

    @Before
    public void setup() throws PrimaryKeyException{
        // test adding a record to a table
        columns = new String[]{"name", "lastname", "age"};
        types = new Type[]{Type.VARCHAR, Type.VARCHAR, Type.INTEGER};
        attributes = new ArrayList<>();
        createAttributes(0 , columns, types);
        Attribute pka = attributes.get(0);
        table = new Table("person", attributes);
        ArrayList<String> d1 = new ArrayList<>(Arrays.asList(new String[]{"carly", "maggiolo", "21"}));
        ArrayList<String> d2 = new ArrayList<>(Arrays.asList(new String[]{"mark", "smith", "24"}));
        ArrayList<String> d3= new ArrayList<>(Arrays.asList(new String[]{"LongName", "ReallyLongLastName", "50"}));
        ArrayList<String> d4= new ArrayList<>(Arrays.asList(new String[]{"Zendaya", "WhateverHerLastNameIs", "25"}));
        ArrayList<String> d5= new ArrayList<>(Arrays.asList(new String[]{"Benito", "bunny", "27"}));
        ArrayList<String> d6= new ArrayList<>(Arrays.asList(new String[]{"ferxxo", "feid", "23"}));
        ArrayList<String> d7= new ArrayList<>(Arrays.asList(new String[]{"pancon", "queso", "100"}));
        ArrayList<String> d8= new ArrayList<>(Arrays.asList(new String[]{"George", "Lopez", "56"}));
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

    @Test
    public void createPage(){
        Page p1 = new Page(0);
        System.out.println(p1);
    }

    @Test
    public void test2(){
        for(Page p: table.getPages()){
            System.out.println(p);
        }
    }


    



    public void createAttributes(int pkidx, String[] c, Type[] t){
        for(int i = 0; i < columns.length; i++){
            Attribute a = new Attribute(c[i], t[i], i == pkidx ? true : false, types[i] == Type.VARCHAR || types[i] == Type.CHAR ? 20 : 0);
            attributes.add(a); 
        }

        attributes.get(0).setIsPrimaryKey(true);

        // for(Attribute a: attributes){
        //     System.out.println(a);
        // }
    }

    
    
}
