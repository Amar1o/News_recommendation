package org.example.news_recommendation;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class ViewLiked {
    private static List<String> head = new ArrayList<>();

    @FXML
    private ListView<String> headlist;
//

    @FXML
    private Button back;

    static Database sql = new Database();
    static User user = new User();

    public void headline() throws ClassNotFoundException {
        // Fetch the first name of the user (assuming a singleton User class)
        String name = user.getInstance().getFirstName();

        head.clear();

        // Get the updated list of liked article headlines from the database
        head = sql.Getlikedarticles(name);

        // Check if the fetched list is not null and populate the ListView
        if (head != null && !head.isEmpty()) {
            ObservableList<String> articles = FXCollections.observableArrayList(head); // Create a new ObservableList
            headlist.setItems(articles); // Update the ListView with the new list
        }
    }


    @FXML
    private void switchtoarticle() {
            Stage stage = (Stage) back.getScene().getWindow();
            stage.close();
    }

}