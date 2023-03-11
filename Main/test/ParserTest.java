import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserTest {
    
    String[] columns;
    Type[] types;
    int[] pkidx;
    ArrayList<Attribute> attributes;
    int wordMaxLen = 20;
    Table table;
    Parser parser;

    @Before
    public void setupData() throws PrimaryKeyException{

        
    }

    @Before
    public void setupParser(){
        String dbname = "school";
        String path = "./Main/" + dbname;
        int pageSize = 100;
        int bufferSize = 15;
        this.parser = new Parser(dbname, path, pageSize, bufferSize);


    }

    @Test
    public void createTest(){
        String[] commands = new String[]{
                "create table foo (num integer primarykey, name varchar(10));",
                "insert into foo values (45, \"carly\");",
                "insert into foo values (50, \"hai-yen\");",
                "insert into foo values (50, \"alex\");",
                "display info foo;",
                "select * from foo;"
        };
        runCommands(commands);
    }

    @Test
    public void insertTest2(){
        String[] commands = new String[]{
                "create table foo (num integer primarykey, name varchar(10) unique);"
//                "insert into foo values (45, \"carly\");",
//                "insert into foo values (50, \"hai-yen\");",
//                "display info foo;"
        };
        runCommands(commands);
    }

    public void runCommands(String[] commands){
        for(int i = 0; i < commands.length; i++){
            String command = commands[i];
            parser.classifyInput(command);
            parser.parse();
        }
    }

    public void parseValueRecords(String queries){
        int idx = 1;
        char c = queries.charAt(idx);
        String single = "";
        while(true){
            if(c == ')'){
                System.out.println("single: " + single);
                break;
            }
            else{ //only one
                if(c == '\"'){
                    idx++;
                    c = queries.charAt(idx);
                }
                else{
                    single += c;
                    idx++;
                    c = queries.charAt(idx);
                }
                
            }
        }
    }

    public void recursiveParse(String queries){
        //TODO: character at 0 shoudl be "("
        int idx = 1;
        char c = queries.charAt(idx);
        while(true){
            if(c == '('){
                String substring = queries.substring(idx);
                parseValueRecords(substring);
            }
        }
    }

    @Test
    public void parseInsertTest(){
        parseValueRecords("(45, \"haiyen\")");
        //test
    }

    @Test
    public void TestWithInvalidVarchar() {
        String[] commands = new String[]{
                "create table foo (num integer primarykey, name varchar(10));",
                "display info foo;"
        };

        runCommands(commands);

        // insert a valid record
        String[] values = new String[]{"45", "\"CarlyMaggiolo"};
        String tableName = "foo";
        String ex = "Invalid Data Type: Expected(int, varchar(10)) got(int, varchar(14))";
        try {
            parser.storageManager.insertRecord(tableName, values);
        } catch (Exception e) {
            assertEquals(ex, e.getMessage());
        }
    }

    @Test
    public void TestWithValidVarchar() {
        String[] commands = new String[]{
                "create table foo (num integer primarykey, name varchar(10));",
                "display info foo;"
        };

        runCommands(commands);

        // insert a valid record
        String[] values = new String[]{"45", "\"Carly"};
        String tableName = "foo";
        try {
            parser.storageManager.insertRecord(tableName, values);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // there should be one page and one records
        assertTrue(parser.storageManager.pageBuffer.getPages().get( 0).getNumOfRecords() == 1);
    }
}
