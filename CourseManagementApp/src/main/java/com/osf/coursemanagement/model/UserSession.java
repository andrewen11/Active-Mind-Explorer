package com.osf.coursemanagement.model;

public class UserSession {

    // Instanța unica a clasei (Singleton)
    private static UserSession instance;

    private User loggedInUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void cleanUserSession() {
        loggedInUser = null; // Logout
    }
}
