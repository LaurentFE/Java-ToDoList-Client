package fr.LaurentFE.todolistclient;

public class User {
    private final Integer userId;
    private final String userName;

    public User(Integer user_id, String user_name) {
        this.userId = user_id;
        this.userName = user_name;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
