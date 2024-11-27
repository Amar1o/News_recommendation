package org.example.news_recommendation;

import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Register {
    @FXML
    private static TextField firstname;
    @FXML
    private Button back;
    @FXML
    private static TextField lastname;

    @FXML
    private static TextField password;

    @FXML
    private static TextField retype;

    @FXML
    private Text success;

    @FXML
    private TextField result;





    public static int register(String firstname, String lastName, String pasword) throws SQLException, ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/news";
        String username = "root";
        String password = "";

        String selectSql = "SELECT * FROM members WHERE first_name = ?";


        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure MySQL driver is loaded

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {

                if (connection != null) {
                    System.out.println("Connected to the database!");
                }
                selectStmt.setString(1, firstname);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Name already exists!");
                        return 1;
                    }
                    else {
                        System.out.println("Registering new user...");
                        String insrtSQL = "INSERT INTO members (first_name, last_name, password ) VALUES (?, ?, ?)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(insrtSQL)) {
                            insertStmt.setString(1, firstname);
                            insertStmt.setString(2, lastName);
                            insertStmt.setString(3,pasword);


                            // Execute the insert statement
                            int rowsAffected = insertStmt.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Insert successful! " + rowsAffected + " row(s) affected.");
                            }
                        }
                    }
                }

            }
            return 0;

        } catch (ClassNotFoundException e) {
            System.out.println("mysql driver not included in path");
            e.printStackTrace();
            return -1;

        } catch (SQLException e) {
            System.out.println("Database connection error or query");
            e.printStackTrace();
            return -1;
        }
    }



}

