import java.util.*;
import java.io.File;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) throws Exception {

        if(args.length < 3 | args.length > 4){
            System.out.println("Usage: Filename, Page Size, Buffer Size");
            exit(0);
        }

        String dbPath = args[0];
        int buffSize = Integer.parseInt(args[1]);
        int pageSize = Integer.parseInt(args[2]);
        boolean indexing = args.length == 4 && args[3].equals("indexing");

        System.out.println("Welcome to 11QL....");

        File directory = new File(dbPath);

        // Check for DB
        System.out.println("Looking at " + dbPath + " for existing db....");

        if(directory.exists()){
            System.out.println("The database exist");
        }
        else{
            System.out.println("No existing database found");
            System.out.println("Creating new database at " + dbPath);
            directory.mkdirs();
            System.out.println("New database created successfully");
            System.out.println("Page size: " + pageSize);
            System.out.println("Buffer size: " + buffSize);
        }

        if (indexing) {
            System.out.println("Indexing is on.");
        }

        // Basic nice-ities for starting up the DB
        System.out.println();
        System.out.println("Please enter commands, enter <quit> to shutdown the db");
        System.out.println();

        // starting the user input
        Scanner userInput = new Scanner(System.in);
        
        // parser object for this session
        Parser parser = new Parser(directory.getName(), dbPath, pageSize, buffSize, indexing);
        // looping until <quit> is taken in
        
        boolean flag = true;
        while (flag){
            // oh no the user needs help, have to display it   
            // keep asking user for input
            System.out.print("11QL> ");
            String strInput = userInput.nextLine();
            boolean hasSemicolon = strInput.indexOf(";") != -1;
            if(strInput.equals("")){
                continue;
            }
            if (hasSemicolon | strInput.equals("quit") | strInput.equals("help")) {
                String[] inputLines = strInput.split(";");

                for (String input : inputLines) {
                    parser.classifyInput(input);
                    flag = parser.parse();
                }
            } else {
                System.out.println("Invalid command. Command does not end with a semicolon.");
            }
        }
        userInput.close();
    }
}