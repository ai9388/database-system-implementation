import java.util.*;
import java.io.File;
import java.nio.file.Path;

public class Main {


    public static void main(String[] args) throws Exception {

        String dbPath = args[0];
        String pageSize = args[1];
        String buffSize = args[2];

        System.out.println("Starting up 11QL...");
        // Check for DB

        // Basic nice-ities for starting up the DB
        System.out.println("Enter <quit> to quit");
        System.out.println("Enter <help> for help");

        // starting the user input
        Scanner userInput = new Scanner(System.in);
        String strInput = userInput.nextLine().toLowerCase();

        File directory = new File(dbPath);

        // Check for DB
        System.out.println("Looking at " + dbPath + " for existing db....");
        System.out.print(">");

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
            System.out.println("New db created successfully");
            System.out.println("Page size: " + pageSize);
            System.out.println("Buffer size: " + buffSize);
        }

        // Basic nice-ities for starting up the DB
        // System.out.println("Enter <quit> to quit");
        // System.out.println("Enter <help> for help");
        System.out.println();
        System.out.println("Please enter commands, enter <quit> to shutdown the db");
        System.out.println();
        System.out.print("11QL> ");

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
