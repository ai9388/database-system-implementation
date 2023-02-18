import java.util.*;

public class Main {
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
