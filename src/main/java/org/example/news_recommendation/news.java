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

    public JsonArray readfile() {
        JsonArray jsonArray = new JsonArray();
        String file = "src/main/java/org/example/demo/News_Category_Dataset_v3.json";

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                JsonObject jsonObject = JsonParser.parseString(line).getAsJsonObject();
                jsonArray.add(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }


    @FXML
    public void loadArticlesbutton() {
        // Assuming 'preference' is the genre you want to use to fetch articles
        try {
            articles = readfile(); // Fetch articles from the JSON file

            // Check if articles were retrieved successfully
            if (articles != null && articles.size() > 0) {
                displayArticle(0); // Display the first article
            } else {
                title.setText("No articles found.");
                content.setText(""); // Clear content if no articles are found
            }
        } catch (Exception e) { // Catching general exception as readfile() does not throw IOException anymore
            e.printStackTrace(); // Log the error for debugging
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
