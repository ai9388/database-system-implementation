import java.util.*;
import java.io.File;
import java.nio.file.Path;

public class Main {


    public static void main(String[] args) throws Exception {

        System.out.println("Starting up 11QL...");
        // Check for DB

        // Basic nice-ities for starting up the DB
        System.out.println("Enter <quit> to quit");
        System.out.println("Enter <help> for help");
        System.out.print(">");

        // starting the user input
        Scanner userInput = new Scanner(System.in);
        String strInput = userInput.nextLine().toLowerCase();

        String dbPath = args[1];
        File directory = new File(dbPath);
        
        if(directory.exists()){
            if(directory.length() > 0){
                System.out.println("catalog exists");
            }
            // else creating new catalog
        }
        else{
            System.out.println("No existing db found");
            System.out.println("Creating new db at " + dbPath);
            directory.mkdir();

            //need to add a catalog into the folder.
        }

        
        // parser object for this session
        Parser parser = new Parser(directory.getName());
        // save the user provided arguments
        parser.saveArgs(args);
        // looping until <quit> is taken in
        while (!strInput.equals("quit")) {
            // oh no the user needs help, have to display it   
            // keep asking user for input
            parser.clasifyInput(strInput);
            parser.parse();
            System.out.print(">");
            strInput = userInput.nextLine();
        }
        System.out.println("Shutting down 11QL...");
        System.out.println("Shutting down the database...");
        System.out.println("Purging the page buffer...");
        System.out.println("Saving catalog...");
        System.out.println("\nExiting the database...");
        userInput.close();

    }
}
