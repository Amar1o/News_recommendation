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

    public void openNewWindow() {
        // Create new window in a new thread
        Thread windowThread = new Thread(() -> {
            Platform.runLater(() -> {
                try {
                    // Create new stage (window)
                    Stage newStage = new Stage();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
                    Parent root = loader.load();

                    // Create scene
                    Scene scene = new Scene(root);
                    newStage.setScene(scene);
                    newStage.setTitle("NEWS");


                    newStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        windowThread.start();
    }

//    @FXML
//    public void register() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
//            Parent root = loader.load();
//
//            Stage stage = (Stage) submit.getScene().getWindow();
//            Scene scene = new Scene(root);
//            stage.setScene(scene);
//            stage.sizeToScene();
//            stage.show();
//            stage.setTitle("Register");
//
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Failed to load Register.fxml.");
//        }
//    }
//    private void switchtoarticle() {
//        try {
//
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("Article.fxml"));
//            Parent root = loader.load();
//
//
//            Stage stage = (Stage) submit.getScene().getWindow();
//
//
//            stage.setTitle("Articles");
//
//            Scene scene = new Scene(root);
//            stage.setScene(scene);
//            stage.sizeToScene();
//            stage.show();
//
//
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Failed to load Article.fxml.");
//        }
//    }
//
//    @FXML
//    public void submit() {
//        String inputFirstName = firstname.getText();
//        String inputpassword = Password.getText();
//        int result = validate(inputFirstName, inputpassword);
//        if (result == 1) {
//            success.setText("Login successful! Welcome, " + inputFirstName + "!");
//            success.setVisible(true);
//            switchtoarticle();
//            User.getInstance().setUserDetails(inputFirstName, inputpassword);
//        } else if (result == 2) {
//            success.setText("Invalid first name or last name.");
//            success.setVisible(true);
//        } else {
//            success.setText("An error occurred during validation.");
//            success.setVisible(true);
//        }
//
//    }
//}

}