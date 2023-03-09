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

    @Before
    public void setup() throws PrimaryKeyException{
        // test adding a record to a table
        columns = new String[]{"name", "lastname", "age"};
        types = new Type[]{Type.VARCHAR, Type.VARCHAR, Type.INTEGER};
        attributes = new ArrayList<>();
        createAttributes(0 , columns, types);
        Attribute pka = attributes.get(0);
        pb = new PageBuffer("./db/", 5, 100);
        ts = new TableSchema("person", attributes);
        TableSchema.setLASTTABLEID(1);
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

    @Test
    public void writeTest() throws IOException {
        /**
         * <tableFile>
         *     n = # pages
         *     n pages id's sorted
         *      <page1>
         *          len in bytes
         *          r = number of records
         *
         *          ... r records
         *      </page1>
         *      ...
         * </tableFile>

         */
//        System.out.println("Working Directory = " + System.getProperty("user.dir"));
//
        String tableFilename = ts.getName();
        String path = "Main/db/" + tableFilename;
        File file = new File(path);
        RandomAccessFile rand = new RandomAccessFile(file, "rw");

//         write the number of pages
        rand.writeInt(ts.getNumberOfPages());

//        // write every page in sequential order
//        for(int i = 0; i < ts.getNumberOfPages(); i++){
//            rand.writeInt(ts.getPageIds().get(i));
//        }

        // write the pages
        for(Page p: pb.getPages()){
            byte[] bytePage = p.getPageAsBytess().array();
            rand.write(bytePage);
        }

        // close the writer
        rand.close();
    }

    public ArrayList<Page> readTest() throws IOException {
        String tableFilename = ts.getName();
        String path = "Main/db/" + tableFilename;
        RandomAccessFile rand = new RandomAccessFile(path, "rw");

//        // get the table name and number
//        String[] fileinfo= file.getName().split("_");
//        String tablename = fileinfo[0];
//        int tableid = Integer.parseInt(fileinfo[1]);
//        // read the number of pages
//        System.out.println(String.format("Table %s(%d):", tablename, tableid));

        // read the number of pages
//        int numPages = rand.readInt();
//        System.out.println(numPages + " pages");

//        // iterate to get the page order
//        int[] pageOrder = new int[numPages];
//        for(int i = 0; i < numPages; i++){
//            pageOrder[i] = rand.readInt();
//        }
//
//        System.out.println("pages ordered: " + Arrays.toString(pageOrder));


        /////// ALEX CATALOG CODE
        ArrayList<Page> pages = new ArrayList<>();
//
//        // first getting the number of pages from table file
//        try {
            int numOfPages = rand.readInt();

            // iterating over all the pages in the file
        for (int i = 0; i < numOfPages; i++) {
                int traversedBytes = 8;
                int pageID = rand.readInt();
                int numberOfRecords = rand.readInt();
                ArrayList<Record> records = new ArrayList<>();

                // iterating over the individual records
                for (int j = 0; j < numberOfRecords; j++) {
                    ArrayList<Object> recordData = new ArrayList<>();

                    for (int k = 0; k < attributes.size(); k++) {
                        switch (attributes.get(k).getType()) {
                            case BOOLEAN:
                                boolean b = rand.readBoolean();
                                traversedBytes += 1;
                                recordData.add(b);
                                break;
                            case CHAR:
                                int n = attributes.get(k).getN();
                                char[] ch = new char[n];

                                for (int l = 0; l < n; l++) {
                                    char c = rand.readChar();
                                    ch[l] = c;
                                }
                                traversedBytes += (Character.BYTES * n);
                                recordData.add(ch.toString());
                                break;
                            case DOUBLE:
                                double d = rand.readDouble();
                                traversedBytes += Double.BYTES;
                                recordData.add(d);
                                break;
                            case INTEGER:
                                int in = rand.readInt();
                                traversedBytes += Integer.BYTES;
                                recordData.add(in);
                                break;
                            case VARCHAR:
                                int len = rand.readInt();
                                char[] vch = new char[len];
                                String s = "";
                                for (int l = 0; l < len; l++) {
                                    char c = rand.readChar();
                                    vch[l] = c;
                                    s += c;
                                }
                                traversedBytes += (Character.BYTES * len) + Integer.BYTES;
                                recordData.add(new String(vch));
                                break;
                            default:
                                // program kills itself
                                break;
                        }
                    }
                    records.add(new Record(recordData));
                }
                Page page = new Page(pageID, records);
                pages.add(page);

                rand.seek(Page.getCapacity() - traversedBytes);
            }

        rand.close();
        return pages;
    }



    @Test
    public void readandwrite() throws IOException {
        writeTest();
        ArrayList<Page> pages = readTest();

        for(Page page: pages){
            System.out.println(page);
        }

    }

    @Test
    public void testWritePage(){
        String path = "Main/db/";
        pb.writePage(path, pb.getPages().get(0));
        pb.writePage(path, pb.getPages().get(1));
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
