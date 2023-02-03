public class Parser {
    private String[] args;
    public Parser(String[] args) {
        this.args = args;
    }
    /**
     * Assume user passes in database
     */
    public void parse() {
        for (String input : args) {
            input = input.toLowerCase();
            if (input.startsWith("create")){

            } else if (input.startsWith("select")) {
                //
            } else if (input.startsWith("insert")) {
                //
            } else if (input.startsWith("display")){
                //
            } else {
                return;
            }
            // create table <name>( <attr1> <attr1type primarykey,
            //  <attr2> <attr2type,
            // );
            //
        }
    }
}
