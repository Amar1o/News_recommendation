package org.example.news_recommendation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import java.io.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import java.io.IOException;

public class news {
    private String genre;

    @FXML
    private TextArea title;

    @FXML
    private TextArea content;
    @FXML
    private Button prev;
    @FXML
    private Button forward;
    private JsonArray articles; // Holds all articles received from the API
    private int currentIndex = 0; // Keeps track of the currently displayed article




    @FXML
    public void loadArticlesbutton() {
        String filePath = "src/main/java/org/example/news_recommendation/News_Category_Dataset_v3.json";
        try {
            jsonreader jsonArticleReader = new jsonreader(filePath);
            articles = jsonArticleReader.readFile(); // Fetch articles from the JSON file

            // Check if articles were retrieved successfully
            if (articles != null && articles.size() > 0) {
                displayArticle(0); // Display the first article
            } else {
                title.setText("No articles found.");
                content.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            title.setText("Error fetching articles.");
            content.setText("Unable to load articles. Please check the file format and content.");
        }
    }
    private void displayArticle ( int index){
        if (articles != null && index >= 0 && index < articles.size()) {
            JsonObject article = articles.get(index).getAsJsonObject();

            // Check for title field and handle JsonNull
            if (article.has("headline") && !article.get("headline").isJsonNull()) {
                title.setText(article.get("headline").getAsString());
            } else {
                title.setText("Title not available");
            }

            // Check for content field and handle JsonNull
            if (article.has("short_description") && !article.get("short_description").isJsonNull()) {
                content.setText(article.get("short_description").getAsString());
            } else {
                content.setText("Content not available");
            }

            currentIndex = index;
        }
    }


    public void showNextArticle () {
        if (currentIndex < articles.size() - 1) {
            displayArticle(currentIndex + 1);
        }
    }


    public void showPreviousArticle () {
        if (currentIndex > 0) {
            displayArticle(currentIndex - 1);
        }
    }
}
