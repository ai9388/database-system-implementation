import java.util.*;
import java.io.File;

public class Main {

    public boolean checkDatabase(String path, int pageSize, int bufferSize){

        //TODO: do we need the pageSize and bufferSize for the parameter rn?
        
        //look for the database from the path and get the table from the database
        File directory = new File(path);
        
        if(directory.exists()){
            if(directory.length() > 0){
                System.out.println("db exists");
                return true;
            }
            else{
                System.out.println("No existing db found");
                System.out.println("Creating new db at" + path);
                return directory.mkdir();
            }
        }
        else{
            System.out.println("No existing db found");
            System.out.println("Creating new db at" + path);
            return directory.mkdir();

            //need to add a catalog into the folder.
        }
    }


    public static void main(String[] args) throws Exception {
        System.out.println("Starting up 11QL...");
        // Check for DB
        // Create DB if it doesn't exist make it

        // Basic nice-ities for starting up the DB
        System.out.println("Enter <quit> to quit");
        System.out.println("Enter <help> for help");
        System.out.print(">");

        // starting the user input
        Scanner userInput = new Scanner(System.in);
        String strInput = userInput.nextLine().toLowerCase();

        // parser object for this session
        Parser parser = new Parser();
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
