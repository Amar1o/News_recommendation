package org.example.news_recommendation;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.news_recommendation.Models.Database;
import org.example.news_recommendation.Models.User;

import java.io.IOException;

public class LoginController {
    @FXML
    private Button submit;

    @FXML
    private Text success;

    @FXML
    public TextField firstname;


    @FXML
    public TextField Password;


    static User user = new User();
    @FXML
    public void submit() {
        String inputFirstName = firstname.getText();
        String inputpassword = Password.getText();
        int result = user.Login(inputFirstName, inputpassword);
        if (result == 1) {
            success.setText("Login successful! Welcome, " + inputFirstName + "!");
            success.setVisible(true);
            switchtoarticle();
            user.getInstance().setUserDetails(inputFirstName, inputpassword);
        } else if (result == 2) {
            success.setText("Invalid name or password.");
            success.setVisible(true);
        } else {
            success.setText("An error occurred during validation.");
            success.setVisible(true);
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
    @FXML
    public void register() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) submit.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.show();
            stage.setTitle("Register");

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load Register.fxml.");
        }
    }
    private void switchtoarticle() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Article.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) submit.getScene().getWindow();


            stage.setTitle("Articles");

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.show();




        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load Article.fxml.");
        }
    }
}
