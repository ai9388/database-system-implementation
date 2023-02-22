import java.util.*;
import java.io.File;

public class Main {

    public boolean checkDatabase(String path, int pageSize, int bufferSize){

        //TODO: do we need the pageSize and bufferSize for the parameter rn?

        
        //look for the database from the path and get the table from the database
        // Path dire = Paths.get(path);
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

        // looping until <quit> is taken in
        while (!strInput.equals("<quit>")) {
            // oh no the user needs help, have to display it   
            if (strInput.equals("<help>")) {
                displayHelp();
            } else {
                // keep asking user for input
                Parser parser = new Parser(strInput);
                parser.saveArgs(args);
                parser.parse();
            }

            System.out.print(">");
            strInput = userInput.nextLine();
        }
        System.out.println("Shutting down 11QL...");
        userInput.close();

    }

    public static void displayHelp() {
        System.out.println("\n To run 11QL, use");
        System.out.println("java Main <db loc> <page size> <buffer size>");
        System.out.println("Available functions are:");
        System.out.println("\tdisplay schema");
        System.out.println("\tdisplay table <table name>");
        System.out.println("\tselect * from <table name>");
        System.out.println("\tinsert into <table name> values");
        System.out.println("\tcreate table <table name> (<values>)");
    }
}
