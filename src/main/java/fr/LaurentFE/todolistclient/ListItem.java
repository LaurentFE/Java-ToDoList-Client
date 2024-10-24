package fr.LaurentFE.todolistclient;

public class ListItem {
    private final Integer item_id;
    private final String label;
    private final Boolean checked;

    public ListItem(Integer item_id, String label, Boolean checked) {
        this.item_id = item_id;
        this.label = label;
        this.checked = checked;
    }

    public String getLabel() {
        return label;
    }

    public Boolean isChecked() {
        return checked;
    }

}