package org.example.news_recommendation;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class Login {

    @FXML
    private Button submit;

    @FXML
    private Text success;

    @FXML
    public TextField firstname;


    @FXML
    public TextField Password;


    public static int validate(String firstName, String Password) {
        String url = "jdbc:mysql://localhost:3306/news";
        String sql = "SELECT * FROM members WHERE first_name = ? AND password = ?";
        String username = "root";
        String password = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure MySQL driver is loaded

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement pre = connection.prepareStatement(sql)) {

                if (connection != null) {
                    System.out.println("Connected to the database!");
                }

                pre.setString(1, firstName);
                pre.setString(2, Password);

                try (ResultSet rs = pre.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Login successful! Welcome, " + firstName + "!");
                        return 1;
                    } else {
                        System.out.println("Invalid first name or password.");
                        return 2;
                    }
                }

            }

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            return 0;
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            return 0;
        }
    }

}