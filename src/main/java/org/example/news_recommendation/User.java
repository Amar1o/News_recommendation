package org.example.news_recommendation;

public class User {
    // Singleton instance of the user session
    private static User instance;

    // User details
    private String firstName;
    private String lastName;
    private boolean Logged;

    // Private constructor to prevent direct instantiation
    User() {
        Logged = false;
    }

    // Singleton getInstance method
    public User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    // Method to set user details
    public void setUserDetails(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.Logged = true;
    }


    public String getFullName() {
        return firstName + " " + lastName;
    }


    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }

    // Check if user is logged in
    public boolean isLoggedIn() {
        return Logged;
    }

    // Method to clear session
    public void clearSession() {
        firstName = null;
        lastName = null;
        Logged = false;
    }
}