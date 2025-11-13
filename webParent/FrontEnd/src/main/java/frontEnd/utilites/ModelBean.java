package frontEnd.utilites;

public class ModelBean {

    private String listItems;
    private String pageTitle;

    public ModelBean(String listItems) {
        this.listItems = listItems;
    }

    public String getListItems() {
        return listItems;
    }

    public void setListItems(String listItems) {
        this.listItems = listItems;
    }
}
