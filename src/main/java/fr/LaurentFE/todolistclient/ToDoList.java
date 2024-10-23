package fr.LaurentFE.todolistclient;

import java.util.ArrayList;

public class ToDoList {
    private final Integer list_id;
    private String label;
    private final ArrayList<ListItem> items;

    public ToDoList(Integer list_id, String label, ArrayList<ListItem> items) {
        this.list_id = list_id;
        this.label = label;
        this.items = items;
    }

    public Integer getList_id() {
        return list_id;
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
