package org.example.news_recommendation.Models;

import java.sql.*;

public class User {
    // Singleton instance of the user session
    private static User instance;

    // User details
    private String firstName;
    private String lastName;
    private boolean Logged;

    static Database sql = new Database();

    // Private constructor to prevent direct instantiation
    public User() {
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

    public int Login(String firstName, String Password){
        int check = sql.validate(firstName,Password);
        if (check==1){
            return 1;
        }else if (check ==2){
            return 2;
        }else{
            return 0;
        }

    }


}