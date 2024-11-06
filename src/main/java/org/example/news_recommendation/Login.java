package org.example.news_recommendation;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
    private TextField firstname;

    @FXML
    private TextField lastname;

    public int validate(String first, String lastName) {
        String url = "jdbc:mysql://localhost:3306/truy";
        String sql = "SELECT * FROM members WHERE first_name = ? AND last_name = ?";
        String username = "root";
        String password = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure MySQL driver is loaded

            try (Connection connection = DriverManager.getConnection(url,username, password);
                 PreparedStatement pre = connection.prepareStatement(sql)) {

                if (connection != null) {
                    System.out.println("Connected to the database!");
                }

                pre.setString(1, first);
                pre.setString(2, lastName);

                try (ResultSet rs = pre.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Login successful! Welcome, " + first + "!");
                        return 1;
                    } else {
                        System.out.println("Invalid first name or last name.");
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

    @FXML
    public void register() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) submit.getScene().getWindow();

            stage.setScene(new Scene(root, 1000, 1000));
            stage.setTitle("Register");

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load Register.fxml.");
        }
    }
    private void switchtoarticle() {
        try {
            // Load the news.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Article.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) submit.getScene().getWindow();

            // Set the new scene (news scene)
            stage.setScene(new Scene(root, 600, 400)); // Adjust width and height as needed
            stage.setTitle("Articles");

            // Show the stage (already visible, but this ensures the new scene is displayed)
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load news.fxml.");
        }
    }
    @FXML
    public void submit() {
        String inputFirstName = firstname.getText();
        String inputLastName = lastname.getText();

        int result = validate(inputFirstName, inputLastName);

        if (result == 1) {
            success.setText("Login successful! Welcome, " + inputFirstName + "!");
            success.setVisible(true);
            switchtoarticle();


        } else if (result == 2) {
            success.setText("Invalid first name or last name.");
            success.setVisible(true);
        } else {
            success.setText("An error occurred during validation.");
            success.setVisible(true);
        }

    }
}

