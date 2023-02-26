import java.util.*;
import java.io.File;
import java.nio.file.Path;

public class Main {


    public static void main(String[] args) throws Exception {
        
        String dbPath = args[0];
        File directory = new File(dbPath);

        System.out.println("Welcome to JottOL");
        // Check for DB
        System.out.println("Looking at " + dbPath + " for existing db....");

        if(directory.exists()){
            String catalogPath = dbPath;
            if(dbPath.contains("\\")){
                catalogPath += "\\Catalog";
            }
            else{
                catalogPath += "/Catalog";
            }

            System.out.println(catalogPath);
            
            File catalogFile = new File(catalogPath);
            if(catalogFile.exists() && !catalogFile.isDirectory()){
                System.out.println("catalog exists - maybe delete this line later");
            }
            else{
                System.out.println("catalog file not exist - delete this lien later");
            }
            // else creating new catalog
        }
        else{
            System.out.println("No existing db found");
            System.out.println("Creating new db at " + dbPath);
            directory.mkdir();

            //need to add a catalog into the folder.
        }

        // Basic nice-ities for starting up the DB
        System.out.println("Enter <quit> to quit");
        System.out.println("Enter <help> for help");
        System.out.print(">");

        // starting the user input
        Scanner userInput = new Scanner(System.in);
        String strInput = userInput.nextLine().toLowerCase();

        // parser object for this session
        Parser parser = new Parser(directory.getName(), dbPath);
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
