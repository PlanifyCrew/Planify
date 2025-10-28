package com.planify.model.user;

public class User {

    private String id = "";
    private String firstName = "";
    private String lastName = "";
    private String loggedInToken = "";


    public User(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() { return this.id; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLoggedInToken() {
        return loggedInToken;
    }

    public void setLoggedInToken(String loggedInToken) {
        this.loggedInToken = loggedInToken;
    }

    public void logStudentOff() {
        loggedInToken = "";
    }


}
