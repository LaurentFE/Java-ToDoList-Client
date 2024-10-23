package fr.LaurentFE.todolistclient;

import java.util.ArrayList;

public class ToDoListList {
    private ArrayList<ToDoList> lists;

    public ToDoListList(ArrayList<ToDoList> lists){
        this.lists = lists;
    }

    public ArrayList<ToDoList> getLists() {
        return lists;
    }

    public void setLists(ArrayList<ToDoList> lists) {
        this.lists = lists;
    }
}
