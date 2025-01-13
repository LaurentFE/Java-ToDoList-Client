package fr.LaurentFE.todolistclient;

import java.util.ArrayList;

public class ToDoList {
    private final Integer listId;
    private String label;
    private final ArrayList<ListItem> items;

    public ToDoList(Integer listId, String label, ArrayList<ListItem> items) {
        this.listId = listId;
        this.label = label;
        this.items = items;
    }

    public Integer getListId() {
        return listId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<ListItem> getItems() {
        return items;
    }
}
