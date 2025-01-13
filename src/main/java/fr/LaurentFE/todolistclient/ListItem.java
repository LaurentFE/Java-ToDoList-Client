package fr.LaurentFE.todolistclient;

public class ListItem {
    private final Integer itemId;
    private String label;
    private final Boolean checked;

    public ListItem(Integer itemId, String label, Boolean checked) {
        this.itemId = itemId;
        this.label = label;
        this.checked = checked;
    }

    public Integer getItemId() {
        return itemId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean isChecked() {
        return checked;
    }

}
