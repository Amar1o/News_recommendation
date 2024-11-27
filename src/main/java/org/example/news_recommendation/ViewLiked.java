package org.example.news_recommendation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViewLiked {
//    private static List<String> headline = new ArrayList<>();

    private static JsonArray filtered;
    private ListView<String> listView; // Your ListView instance

    @FXML
    private ListView<String> headlist;
//

    @FXML
    private Button back;

    static Database sql = new Database();

//    public void headline() throws ClassNotFoundException {
//
//        // Initialize an empty list to clear previous data
//        List<String> head = new ArrayList<>();
//
//        // Fetch the first name of the user (assuming a singleton User class)
//        String name = User.getInstance().getFirstName();
//
//        // Get the updated list of liked article headlines from the database
//        head = sql.Getlikedarticles(name);
//
//        // Clear the current items in the ListView to refresh it
//        headlist.getItems().clear();
//
//        // Check if the list is not null and populate the ListView
//        if (head != null) {
//            ObservableList<String> articles = FXCollections.observableArrayList(head); // for dynamic updating
//
//            // Bind the list to the ListView
//            headlist.setItems(articles);
//        }
//
//    }
    public void headline() throws ClassNotFoundException {
        // Fetch the first name of the user (assuming a singleton User class)
        String name = User.getInstance().getFirstName();

        // Clear the ListView by setting an empty ObservableList
        headlist.setItems(FXCollections.observableArrayList());

        // Get the updated list of liked article headlines from the database
        List<String> head = sql.Getlikedarticles(name);

        // Check if the fetched list is not null and populate the ListView
        if (head != null && !head.isEmpty()) {
            ObservableList<String> articles = FXCollections.observableArrayList(head); // Create a new ObservableList
            headlist.setItems(articles); // Update the ListView with the new list
        }
    }


//    public List<String> Getlikedarticles(String name) throws ClassNotFoundException {
//        String url = "jdbc:mysql://localhost:3306/news";
//        String username = "root";
//        String password = "";
//
//        // Query to retrieve all headlines for the given name
//        String query = "SELECT headline FROM preference WHERE name = ?";
//
//
//        try {
//            // Load the MySQL driver
//            Class.forName("com.mysql.cj.jdbc.Driver");
//
//            // Connect to the database
//            try (Connection connection = DriverManager.getConnection(url, username, password);
//                 PreparedStatement statement = connection.prepareStatement(query)) {
//
//                // Set the parameter for the name in the query
//                statement.setString(1, name);
//
//                // Execute the query and process the result
//                try (ResultSet resultSet = statement.executeQuery()) {
//                    while (resultSet.next()) {
//                        // Add each headline to the list
//                        headline.add(resultSet.getString("headline"));
//                    }
//                }
//            }
//
//            if (headline.isEmpty()) {
//                headline.add("No data found for name: " + name);
//            }
//
//        } catch (Exception e) {
//            System.err.println("Error retrieving data: " + e.getMessage());
//            e.printStackTrace();
//            headline.add("Error retrieving data.");
//        }
//
//        return headline;
//    }
    @FXML
    private void switchtoarticle() {
            Stage stage = (Stage) back.getScene().getWindow();
            stage.close();
    }

}