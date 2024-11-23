package org.example.news_recommendation;

import javafx.fxml.FXML;

import java.sql.*;

import javafx.scene.control.TextField;
import javafx.scene.text.Text;


public class Register {
    @FXML
    private TextField firstname;

    @FXML
    private TextField lastname;

    @FXML
    private TextField Age;

    @FXML
    private TextField Preference;

    @FXML
    private Text success;

    @FXML
    private TextField result;

    private void clearTextFields() {
        firstname.clear();
        lastname.clear();
        Age.clear();
        Preference.clear();
    }


    public static int register(String firstname, String lastName) throws SQLException, ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/truy";
        String username = "root";
        String password = "";
        String selectSql = "SELECT * FROM members WHERE first_name = ? AND last_name = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure MySQL driver is loaded

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {

                if (connection != null) {
                    System.out.println("Connected to the database!");
                }
                selectStmt.setString(1, firstname);
                selectStmt.setString(2, lastName);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Name already exists!");
                        return 1;
                    } else {
                        System.out.println("Registering new user...");
                        String insrtSQL = "INSERT INTO members (first_name, last_name ) VALUES (?, ?)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(insrtSQL)) {
                            insertStmt.setString(1, firstname);
                            insertStmt.setString(2, lastName);


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

    @FXML
    public void Registerc() throws SQLException, ClassNotFoundException {

        String fname = firstname.getText();
        String lname = lastname.getText();


        int result = register(fname, lname);
        switch (result) {
            case 0:
                clearTextFields();
                success.setVisible(true);
                success.setText("Member successfully registered!");
                //System.out.println("Registered Member: " + registeredMember);
                break;
            case 1:
                clearTextFields();
                this.result.setText("Member already exists");
                break;
            default:
                System.out.println("An error occurred.");
                break;
        }
    }
}

