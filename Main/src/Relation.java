import java.util.*;

public class Relation {
    private String relationName;
    private ArrayList<Attribute> relationAttributes;
    private ArrayList<View> relationViews;
    private int numOfTuples;
    private String relationPath;

    public Relation(String name, ArrayList<Attribute> attr, 
    ArrayList<View> views, int tuples, String path)
    {
        this.relationName = name;
        this.relationAttributes = attr;
        this.relationViews = views;
        this.numOfTuples = tuples;
        this.relationPath = path;
    }


    /**
     * @return String return the relationName
     */
    public String getRelationName() {
        return relationName;
    }

    /**
     * @param relationName the relationName to set
     */
    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    /**
     * @return ArrayList<Attribute> return the relationAttributes
     */
    public ArrayList<Attribute> getRelationAttributes() {
        return relationAttributes;
    }

    /**
     * @param relationAttributes the relationAttributes to set
     */
    public void setRelationAttributes(ArrayList<Attribute> relationAttributes) {
        this.relationAttributes = relationAttributes;
    }

    /**
     * @return ArrayList<View> return the relationViews
     */
    public ArrayList<View> getRelationViews() {
        return relationViews;
    }

    /**
     * @param relationViews the relationViews to set
     */
    public void setRelationViews(ArrayList<View> relationViews) {
        this.relationViews = relationViews;
    }

    /**
     * @return int return the numOfTuples
     */
    public int getNumOfTuples() {
        return numOfTuples;
    }

    /**
     * @param numOfTuples the numOfTuples to set
     */
    public void setNumOfTuples(int numOfTuples) {
        this.numOfTuples = numOfTuples;
    }

    /**
     * @return String return the relationPath
     */
    public String getRelationPath() {
        return relationPath;
    }

    /**
     * @param relationPath the relationPath to set
     */
    public void setRelationPath(String relationPath) {
        this.relationPath = relationPath;
    }

}
