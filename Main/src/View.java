public class View {
    private String viewName;
    private String viewDefinitions;

    public View(String name, String defn)
    {
        this.viewName = name;
        this.viewDefinitions = defn;
    }


    /**
     * @return String return the viewName
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * @param viewName the viewName to set
     */
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    /**
     * @return String return the viewDefinitions
     */
    public String getViewDefinitions() {
        return viewDefinitions;
    }

    /**
     * @param viewDefinitions the viewDefinitions to set
     */
    public void setViewDefinitions(String viewDefinitions) {
        this.viewDefinitions = viewDefinitions;
    }

}
