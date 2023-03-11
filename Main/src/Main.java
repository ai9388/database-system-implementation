import java.util.*;
import java.io.File;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) throws Exception {
        // args = new String[]{"Main/db/", "100", "15"};

        if(args.length != 3){
            System.out.println("Usage: Filename, Page Size, Buffer Size");
            exit(0);
        }

        String dbPath = args[0];
        int pageSize = Integer.parseInt(args[1]);
        int buffSize = Integer.parseInt(args[2]);

        System.out.println("Welcome to 11QL....");

        File directory = new File(dbPath);

        // Check for DB
        System.out.println("Looking at " + dbPath + " for existing db....");

        if(directory.exists()){
            System.out.println("The db exist");
        }
        else{
            System.out.println("No existing db found");
            System.out.println("Creating new db at " + dbPath);
            directory.mkdirs();
            System.out.println("New db created successfully");
            System.out.println("Page size: " + pageSize);
            System.out.println("Buffer size: " + buffSize);
        }

        // Basic nice-ities for starting up the DB
        System.out.println();
        System.out.println("Please enter commands, enter <quit> to shutdown the db");
        System.out.println();

        // starting the user input
        Scanner userInput = new Scanner(System.in);
        
        // parser object for this session
        Parser parser = new Parser(directory.getName(), dbPath, pageSize, buffSize);
        // looping until <quit> is taken in
        
        
        boolean flag = true;
        while (flag == true){
            // oh no the user needs help, have to display it   
            // keep asking user for input
            System.out.print("11QL> ");
            String strInput = userInput.nextLine();
            parser.classifyInput(strInput);
            flag = parser.parse();
            
        }
        userInput.close();

    }
}
