import java.io.File;
import java.io.RandomAccessFile;
import java.util.*;

public class StorageManager {
    public Database db;
    private int bufferSize;
    public PageBuffer pageBuffer;
    public String dbPath;
    public int pageSize;
    public Catalog catalog;

    //call the get the most recent page from table for insert, update function
    //then call the LRU(on the page)

    public StorageManager(String dbName, String dbPath, int bufferSize, int pageSize){
        this.catalog = new Catalog(dbPath, pageSize);
        this.db = new Database(dbName, dbPath);
        this.bufferSize = bufferSize;
        this.dbPath = dbPath;
        this.pageSize = pageSize;
        this.pageBuffer = new PageBuffer(dbPath, bufferSize, pageSize);

        if (this.catalog.checkExistance())
        {
            this.catalog.readCatalog();
            this.db.setTables(this.catalog.getTables());
        }

    }

    /**
     * accessor for the database object used in this session
     * @return
     */
    public Database getDb() {
        return this.db;
    }

    /***
     * get table using table name from database
     * for parser be able to get the table with the given table name
     * @param table_name the name of the table
     * @return
     */
    public TableSchema getTable(String table_name) throws TableException{
        return db.getTable(table_name);
    }

    /**
     * returns all tables from the DB
     * @return table collection
     */
    public ArrayList<TableSchema> getAllTables(){
        return db.getTables();
    }

    /**
     * creates a new table
     * used by parser
     * @param tableName the name of the table
     * @param attributes the attributes of this table
     * @throws TableException
     */
    public void createTable(String tableName, ArrayList<Attribute> attributes) throws TableException{
        db.createTable(tableName, attributes);
        catalog.createTableFile(tableName);
    }

    /***
     * Insert records into given table. Only for records that already exist
     * @param tableName table schema name
     * @param recordsInfo the information about all the records to be inserted
     * @throws PrimaryKeyException
     */
    public void insertRecords(String tableName, ArrayList<String[]> recordsInfo) throws TableException, PrimaryKeyException, InvalidDataTypeException, ConstraintException {
        for(String[] recordInfo: recordsInfo){
            insertRecord(tableName, recordInfo);
        }
    }

    /***
     * displaying database schema
     */
    public void displaySchema() throws TableException {
        System.out.println("DB location: " + dbPath);
        System.out.println("Page Size: " + this.pageSize);
        System.out.println("Buffer Size: " + this.bufferSize);
        System.out.println();
        
        ArrayList<TableSchema> tables = db.getTables();
        if(tables.size() == 0){
            System.out.println("No tables to display");
        }
        else{
            System.out.println("Tables: ");
        }
        for (TableSchema table : tables) {
            System.out.println(table.displayTableSchema());
            System.out.println("Records: " + loadRecords(table, null).size() + "\n");
        }
    }

    /**
     * display the schema for a specific table
     * @param tableName the name of the table
     * @throws TableException if the table name is not valid
     */
    public void displayTableInfo(String tableName) throws TableException {
        TableSchema table = db.getTable(tableName);
        System.out.println(table.displayTableSchema());
        System.out.println("Records: " + loadRecords(table, null).size() + "\n");
    }

    /***
     * get all records for a given table number
     * @param table the table name
     * @return an arraylist of records
     */
    private ArrayList<Record> loadRecords(TableSchema table, ArrayList<Attribute> subsetAttributes) throws TableException {
        ArrayList<Record> records = null;

        // in case the attribute names have been changed

        records = pageBuffer.getRecords(table, subsetAttributes);

        return records;
    }

    public void select(ArrayList<String> tableNames, ArrayList<String> columns, String condition, String orderbyAtt) throws TableException, ConditionalException {
        // get all the tables
        boolean all = columns.get(0).equals("*");
        if(all){
            columns = new ArrayList<>();
        }
        ArrayList<TableSchema> tables = new ArrayList<>();
        HashMap<TableSchema, ArrayList<Attribute>> realAttributes = new HashMap<>();
        ArrayList<Attribute> combined = new ArrayList<>();
        ArrayList<Record> records = new ArrayList<>();

        // validate all tables
        for(String name: tableNames){
            TableSchema table = db.getTable(name);
            tables.add(table);

            // check the * case
            if(all){
                realAttributes.put(table, new ArrayList<>());

                for(Attribute a: table.getAttributes()){
                    columns.add(a.getName());
                    combined.add(a);
                }

            }
            else{
                realAttributes.put(table, new ArrayList<Attribute>());
            }
        }


       if(!all){
           // validate all columns
           realAttributes = getValidColumns(realAttributes, tables, columns);

           for(TableSchema table: realAttributes.keySet()){
               combined.addAll(realAttributes.get(table));
           }
           records = select(realAttributes, tables, combined);
       }
       else{
           // validate all columns
           realAttributes = getValidColumns(realAttributes, tables, columns);
           records = select(realAttributes, tables, combined);
       }

       // where
        if(!condition.equals("")){
            Conditional conditional = Conditional.run(combined, condition);
            ArrayList<Record> filteredRecords = new ArrayList<>();

            for(Record r: records){
                if((boolean)conditional.evaluate(r)){
                    filteredRecords.add(r);
                }
            }

            records = filteredRecords;
            // iterate through all records and filter
        }

       //order by
        Attribute orderAttribute = null;
       for(Attribute a: combined){
           if(a.getName().equals(orderbyAtt.strip())){
               orderAttribute = a;
               break;
           }
       }
       if(orderAttribute == null && !orderbyAtt.equals("")){
           throw new ConditionalException(3, orderbyAtt);
       }
       else if(orderAttribute != null && !orderbyAtt.equals("")){
           orderby(records, orderAttribute);
       }

        System.out.println(formatResults(combined, records));
    }

    public HashMap<TableSchema, ArrayList<Attribute>> getValidColumns(HashMap<TableSchema, ArrayList<Attribute>> attributes, ArrayList<TableSchema> tables, ArrayList<String> columns) throws TableException {
        HashMap<TableSchema, ArrayList<Attribute>> attributesByTable = attributes;
        // verify that all columns exist
        for(String column: columns){
            String[] colInfo = column.strip().split("\\.");
            boolean added = false;
            for(TableSchema table: tables){
                if(colInfo.length == 2){
                    String tableName = colInfo[0].strip();
                    String attributeName = colInfo[1].strip();
                    if(tableName.strip().equals(table.getName())){
                        Attribute a = table.getAttribute(attributeName);
                        a.setAlias(column); // set the column alias
                        attributesByTable.get(table).add(a);
                        added = true;
                        break;
                    }
                }
                else{
                    try{
                        Attribute a = table.getAttribute(column);
                        // attribute belongs to table
                        if(added){
                            throw new TableException(9, column);
                        }
                        attributesByTable.get(table).add(a);
                        added = true;
                    }
                    catch (TableException ta){
                        if(ta.getErrorCode() == 9){
                            throw ta;
                        }
                    }
                }
            }
            if(colInfo.length == 2 && !added){
                throw new TableException(1, column);
            }
        }

        return attributesByTable;
    }


    public ArrayList<Record> select(HashMap<TableSchema, ArrayList<Attribute>> attributesByTable, ArrayList<TableSchema> tables, ArrayList<Attribute> combined) throws TableException {

        ArrayList<Record> realRecords = new ArrayList<>();
        HashMap<TableSchema, ArrayList<Record>> recordsByTable = new HashMap<>();
        ArrayList<Record> allRecords = new ArrayList<>();

        // if there is only one table
        if(attributesByTable.size() == 1){
            TableSchema onlyTable = attributesByTable.keySet().iterator().next();
            allRecords = loadRecords(onlyTable, attributesByTable.get(onlyTable));

        }
        else{
            // combine all the records
            allRecords = createResultSet(attributesByTable, tables, combined);
        }

        return allRecords;
    }

    public ArrayList<Record> createResultSet(HashMap<TableSchema, ArrayList<Attribute>> attributesByTable, ArrayList<TableSchema> tables, ArrayList<Attribute> combined) throws TableException {
        ArrayList<Record> setOneRecords = new ArrayList<>();
        ArrayList<Record> setTwoRecords = new ArrayList<>();
        ArrayList<Record> combinedRecords = new ArrayList<>();
        ArrayList<Attribute> recordAttributes = new ArrayList<>();
        ArrayList<Object> recordEntries = new ArrayList<>();
        int p2 = 1;

        // set one is the first table
        setOneRecords = loadRecords(tables.get(0), attributesByTable.get(tables.get(0)));
        // set two is the second table
        setTwoRecords = loadRecords(tables.get(1), attributesByTable.get(tables.get(1)));

        recordAttributes.addAll(attributesByTable.get(tables.get(0)));

        while(true){
            // combine the attributes from table two
            recordAttributes.addAll(attributesByTable.get(tables.get(p2)));

            for(Record r1: setOneRecords){
                for(Record r2: setTwoRecords){
                    // combine the record entries
                    recordEntries.addAll(r1.getEntries());
                    recordEntries.addAll(r2.getEntries());

                    // create the new combined record
                    Record combinedRecord = new Record(recordEntries, recordAttributes, false);
                    combinedRecords.add(combinedRecord);
                    recordEntries = new ArrayList<>();
                }
            }

            // increment p2, so it become the next table in the sequence
            p2++;

            // make sure p2 is not out of bounds
            if(p2 == tables.size()){
                break;
            }

            // reset set one to be the combined records
            setOneRecords = combinedRecords;

            // set setTwoRecords to be the next table
            setTwoRecords = loadRecords(tables.get(p2), attributesByTable.get(tables.get(1)));
            // reset combined records
            combinedRecords = new ArrayList<>();
        }

        return combinedRecords;
    }

    /**
     * inserts a record into the named table pages
     * @param tableName the name of the pages
     * @param recordInfo information for the records
     * @throws TableException if the table name is invalid
     * @throws InvalidDataTypeException if the types provided in the record info are invalid
     * @throws PrimaryKeyException if the primary key isn't valid or if repeated
     */
    public void insertRecord(String tableName, String[] recordInfo) throws TableException, InvalidDataTypeException, PrimaryKeyException, ConstraintException {
        TableSchema table = db.getTable(tableName);
        Record record = null;

        record = db.validateRecord(table, recordInfo);
        if(record != null){
            ArrayList<Record> records = loadRecords(table, null);
            db.validatePrimaryKey(record, table, records);
            ArrayList<Integer> uniqueAttributes = db.uniqueAttribute(table.getAttributes());
            db.checkUniqueness(record, uniqueAttributes, records);
            pageBuffer.insertRecord(table, record);
        }
    }

    /**
     * Writing the data to the catalog
     */
    public void writeToCatalog()
    {
        this.catalog.setTables(db.getTables());
        byte[] bb = this.catalog.createBytesForCatalog();
        this.catalog.writeBytesToCatalogFile(bb);
    }

    public void shutDown(){
        // purge all the pages
        pageBuffer.purge();

        // write changes to the catalog
        writeToCatalog();
    }
    /**
      * adding the initial information to the file
      * this includes the file id, number of pages, and number of records
      */
    public void addIntialInfoToTable(File new_table, int fileID, int numOfPages, int numOfRecords)
    {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(new_table, "rw");

            byte[] bytes = new byte[0];

            bytes=Type.concat(bytes, Type.convertIntToByteArray(numOfPages));
            
            raf.write(bytes);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * drops the table with given name
     * @param table_name
     */
    public boolean dropTable(String table_name) throws TableException {

        TableSchema table = db.getTable(table_name);
         if(pageBuffer.dropTable(table.getName())){
             db.dropTable(table.getName());
             return true;
         }

         return false;
    }

    public boolean checkIfRecordMeetsCondition(Record record, String condition)
    {
        String[] splitCondition = condition.split(" ");
        String attribute = splitCondition[0];
        String operator = splitCondition[1];
        String cond = splitCondition[2];

        Attribute usedAttribute;
        for (Attribute a: record.attr)
        {
            if (a.getName().equals(attribute))
            {
                usedAttribute = a;
            }
        }
        // gonna have to use some tree for this to work better
        // but that is a problem for tomorrow

        return false;
    }

    /**
     * deletes all of the records from a given table
     * @param table_name name of the table to delete all records from
     */
    public int deleteRecords(String table_name, String where) throws TableException, ConditionalException {

        // keep track of rows affected
        int count = 0;

        // set the condition flag
        boolean condition = !where.equals("");

        // flag to determine if a page has been updated
        boolean pageUpdated = false;

        // getting the table schema for the table we want
        TableSchema taSchema = this.db.getTable(table_name);

        // getting all of the pageIDs associated with the table
        ArrayList<Integer> pageIDs = taSchema.getPageIds();

        // create the conditional object
        Conditional conditional = null;
        if(condition){
            // run the conditional tokenizer
            conditional = Conditional.run(taSchema.getAttributes(), where);
        }

        // looping over all of the page ids
        for (int i = 0; i < pageIDs.size(); i++)
        {
            // getting the individual page
            Page p = this.pageBuffer.getPage(taSchema, pageIDs.get(i));

            // make a copy of the records to refernce (READ - ONLY)
            ArrayList<Record> records = new ArrayList<>(p.getRecords());
            int pointer = 0; // to aid iteration

            // looping over all of the records and removing them
            for(Record record: records)
            {
                // (if there is a condition) check if the record meets condition
                if(condition && conditional.evaluateRecord(record)){
                    p.removeRecord(record);
                    count ++;
                    pageUpdated = true;
                }
                else if(!condition){
                    p.removeRecord(record);
                    pageUpdated = true;
                }

            }

            // update this page in the buffer if there has been a change
            if(pageUpdated) {
                this.pageBuffer.updateBuffer(p);
            }
        }
        return count;
    } 

    public int update(String table_name, String column, String value, String where_clause) throws TableException, ConditionalException {

        // keep track of rows affected
        int count = 0;

        // set the condition flag
        boolean condition = !where_clause.equals("");

        // flag to determine if a page has been updated
        boolean pageUpdated = false;

        // getting the table schema for the table we want
        TableSchema taSchema = this.db.getTable(table_name);

        // getting all of the pageIDs associated with the table
        ArrayList<Integer> pageIDs = taSchema.getPageIds();

        // create the conditional object
        Conditional conditional = null;
        if(condition){
            // run the conditional tokenizer
            conditional = Conditional.run(taSchema.getAttributes(), where_clause);
        }

        // looping over all of the page ids
        for (int i = 0; i < pageIDs.size(); i++)
        {
            // getting the individual page
            Page p = this.pageBuffer.getPage(taSchema, pageIDs.get(i));

            // make a copy of the records to refernce (READ - ONLY)
            ArrayList<Record> records = new ArrayList<>(p.getRecords());
            int pointer = 0; // to aid iteration

            // looping over all of the records and removing them
            for(Record record: records)
            {
                // (if there is a condition) check if the record meets condition
                if(condition && conditional.evaluateRecord(record)){
                    p.removeRecord(record);
                    Record newRecord = record.updateAtColumn(taSchema.getAttributeIndex(column), value);
                    p.addRecord(newRecord);
                    count ++;
                    pageUpdated = true;
                }
                else if(!condition){
                    p.removeRecord(record);
                    Record newRecord = record.updateAtColumn(taSchema.getAttributeIndex(column), value);
                    p.addRecord(newRecord);
                    count ++;
                    pageUpdated = true;
                }
            }

            // update this page in the buffer if there has been a change
            if(pageUpdated) {
                this.pageBuffer.updateBuffer(p);
            }
        }
        return count;
    }

    /**
     * Drop the given attribute from given table
     * @param attribute_name
     * @param table_name
     */
    public void dropAttributeFromTable(String attribute_name, String table_name) throws TableException {
        TableSchema table = db.getTable(table_name);
        int oldAttributeIndex = table.getAttributeIndex(attribute_name);
        ArrayList<Attribute> newAttributes = db.removeAttribute(attribute_name, table);

        // get all the records
        ArrayList<Record> records = loadRecords(table, null);
        ArrayList<Record> newRecords = new ArrayList<>();

        // drop the old table
        dropTable(table_name);

        // create a new table
        createTable(table_name, newAttributes);
        TableSchema newTable = getTable(table_name);

        // populate new records
        for(Record r: records){
            // copy the old entries
            ArrayList<Object> newEntries = new ArrayList<>();
            newEntries.addAll(r.getEntries());
            // remove entry related to old attribute
            newEntries.remove(oldAttributeIndex);
            // create the new record
            Record newRecord = new Record(newEntries, newAttributes, true);
            // add the new Record to new collection
            newRecords.add(newRecord);
            pageBuffer.insertRecord(newTable, newRecord);
        }
    }

    public void addAttributeToTable(Attribute attribute, String defaultValue, String table_name) throws TableException, InvalidDataTypeException, ConstraintException {
        TableSchema table = db.getTable(table_name);

        // remove the quotes
        if(attribute.getType() == Type.VARCHAR || attribute.getType() == Type.CHAR){
            if(defaultValue.charAt(0) == '\"'){
                defaultValue = defaultValue.substring(1);
            }
            if(defaultValue.charAt(defaultValue.length() - 1) == '\"'){
                defaultValue = defaultValue.substring(0, defaultValue.length() - 1);
            }
        }

        if(!defaultValue.equals("") && !Type.validateType(defaultValue, attribute)){
            throw new InvalidDataTypeException(defaultValue, attribute);
        }

        // get the attributes and add a new one
        ArrayList<Attribute> newAttributes = new ArrayList<>();
        newAttributes.addAll(table.getAttributes());

        if(newAttributes.contains(attribute)){
            throw new TableException(6, attribute.getName());
        }

        newAttributes.add(attribute);

        // get all the records
        ArrayList<Record> records = loadRecords(table, null);
        ArrayList<Record> newRecords = new ArrayList<>();

        // drop the old table
        dropTable(table_name);

        // create a new table
        createTable(table_name, newAttributes);
        TableSchema newTable = getTable(table_name);

        // populate new records
        for(Record r: records){
            // copy the old entries
            ArrayList<Object> newEntries = new ArrayList<>();
            newEntries.addAll(r.getEntries());

            // add the new entry on condition
            if(defaultValue.equals("")){
                newEntries.add("null");
            }
            else{
                newEntries.add(defaultValue);
            }

            Record newRecord = new Record(newEntries, newAttributes, true);
            // add the new Record to new collection
            newRecords.add(newRecord);
            pageBuffer.insertRecord(newTable, newRecord);
        }
    }

    public String formatResults(ArrayList<Attribute> tableAttributes, ArrayList<Record> records) {
        if(records == null || records.size() == 0){
            return "No Records to show";
        }
        String format = "|";
        String result = "";
        int len = 1;
        String dash;
        Object[] headers = new Object[tableAttributes.size()];
        for (int i = 0; i < tableAttributes.size(); i++) {
            Attribute a = tableAttributes.get(i);
            headers[i] = tableAttributes.get(i).getName().toUpperCase();
            if (a.getType() == Type.VARCHAR || a.getType() == Type.CHAR) {
                int temp = Math.max(a.getName().length() + 2, a.getN() + 2);
                format += "%-" + temp + "s|";
                len += temp + 1;
            } else {
                int temp = (a.getName().length() + 2);
                format += "%-" + temp + "s|";
                len += temp + 1;
            }
        }
        // create dashed line
        dash = String.format("%0" + len + "d", 0).replace("0", "-");
        // add the header to result
        result = dash + "\n" + String.format(format, headers) + "\n" + dash;

        // specific columns from all the records
        for (Record r : records) {
            ArrayList<Object> entries = new ArrayList<>();
            for (int x = 0; x < tableAttributes.size(); x++) {
                entries.add(r.getValueAtColumn(x));
            }
            result += "\n" + String.format(format, entries.toArray());
        }

        // bottom line
        result += "\n" + dash;
        return result;
    }

    public void orderby(ArrayList<Record> records, Attribute attribute){
        Collections.sort(records, new RecordComparator(attribute));
    }

}
