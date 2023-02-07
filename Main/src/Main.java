import java.util.*;

public class Main 
{
    public static void main(String[] args) throws Exception 
    {
        // Basic nice-ities for starting up the DB
        System.out.println("Starting up 11QL...");
        System.out.println("Enter <quit> to quit");
        System.out.println("Enter <help> for help");
        System.out.print(">");

        // starting the user input
        Scanner userInput = new Scanner(System.in);
        String str_input = userInput.nextLine();

        // looping until <quit> is taken in
        while (!str_input.equals("quit"))
        {
            // oh no the user needs help, have to display it   
            if (str_input.equals("help"))
            {
                displayHelp();
            }
            else
            {
            // first get the user input and separate it into a string array
            String[] arguments = str_input.split(" ", 0);
            System.out.println("the args in main:");
            System.out.println(Arrays.toString(arguments));
            Parser parser = new Parser(arguments);

            parser.parse();  
            }
            
            System.out.print(">");
            str_input = userInput.nextLine();
        }

        userInput.close();

    }

    public static void displayHelp()
    {
        System.out.println("To run 11QL, use");
        System.out.println("java Main <db loc> <page size> <buffer size>");
        System.out.println("Available functions are:");
        System.out.println("\tdisplay schema");
        System.out.println("\tdisplay table <table name>");
        System.out.println("\tselect * from <table name>");
        System.out.println("\tinsert into <table name> values");
        System.out.println("\tcreate table <table name> (<values>)");
    }
}
