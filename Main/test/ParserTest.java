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
        String path = "./Main/db/school";
        int pageSize = 100;
        int bufferSize = 15;
        this.parser = new Parser(dbname, path, pageSize, bufferSize);


    }

    @Test
    public void createTest(){
        parser.classifyInput("create table foo (num integer primarykey, name char(20));");
        parser.parse();
    }


}
