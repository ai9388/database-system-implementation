import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
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
                "insert into foo values (45, \"          carly     \");",
                "display info foo;"
        };
        runCommands(commands);
    }

    @Test
    public void insertTest2(){
        String[] commands = new String[]{
                "create table foo (num integer primarykey, name varchar(10));",
                "insert into foo values (45, \"carly\");",
                "display info foo;"
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
        int idx = 0;
        char c = queries.charAt(0);
        String single = "";
        while(true){
            if(c == ')'){
                System.out.println("single: " + single);
                break;
            }
            // idx++;
            // c = queries.charAt(idx);
            if (c == '('){ // multiple tuples
                System.out.println("multiple");
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

    @Test
    public void parseTest(){
        parseValueRecords("(45, \"haiyen\")");
    }

    @Test
    public void test2(){
        String[] commands = new String[]{
                "create table foo (num integer primarykey, name varchar(10));",
                "insert into foo values (45, \"carly\"), (54, \"hai-yen\");",
                "display info foo;"
        };

        runCommands(commands);
    }


}
