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
import java.sql.SQLException;

public class RegisterController {
    @FXML
    private TextField firstname;
    @FXML
    private Button back;
    @FXML
    private TextField lastname;

    @FXML
    private TextField password;

    @FXML
    private  TextField retype;

    @FXML
    private Text success;

    @FXML
    private void switchback() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) back.getScene().getWindow();


            stage.setTitle("Login");

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load Article.fxml.");
        }
    }

    public  String passwordcheck(String pass, String retyp) {
        if (!pass.equals(retyp)) {
            return "wrong";
        }


        return pass;
    }

    @FXML
    public void Registerc() throws SQLException, ClassNotFoundException {
        String fname = firstname.getText();
        String lname = lastname.getText();
        String Pword = password.getText();
        String Rword = retype.getText();

        // First check if passwords match
        String passCheckResult = passwordcheck(Pword, Rword);

        if ("wrong".equals(passCheckResult)) {
            System.out.println("Passwords do not match");

            success.setText("Passwords do not match");
            return; // Exit the method if passwords don't match
        }

        // Only attempt to register if passwords match
        int result = Register.register(fname, lname, Pword);

        switch (result) {
            case 0:

                success.setVisible(true);
                success.setText("Member successfully registered!");

                break;
            case 1:

                success.setText("Member already exists");

                break;
            default:
                System.out.println("An error occurred.");
                success.setText("Registration failed. Please try again.");
                break;
        }
    }
}
