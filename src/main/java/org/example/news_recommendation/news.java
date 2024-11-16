package org.example.news_recommendation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import java.io.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.json.JSONException;
import java.net.URL;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.io.IOException;

public class news {
    private String genre;
    @FXML
    private  WebView web;

    private WebEngine eng;


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


    public void Webarticles(String url){
        eng = web.getEngine();
        String disableImagesScript = "document.querySelectorAll('img').forEach(function(img) { img.style.display = 'none'; });";
        eng.executeScript(disableImagesScript);
         eng.load(url);
        setzoom(1.0);
    }
    private void setzoom(double zoomFactor) {
        String zoomScript = "document.body.style.zoom = '" + zoomFactor + "';";
        eng.executeScript(zoomScript);
    }
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
    private void displayArticle ( int index) throws JSONException {
        if (articles != null && index >= 0 && index < articles.size()) {
            JsonObject article = articles.get(index).getAsJsonObject();

            // Check for title field and handle JsonNull
            if (article.has("headline") && !article.get("headline").isJsonNull()) {
                title.setText(article.get("headline").getAsString());
            } else {
                title.setText("Title not available");
            }
            String URL="";
            String articlecontent="";
            // Check for content field and handle JsonNull
            if (article.has("short_description") && !article.get("short_description").isJsonNull()) {
                content.setText(article.get("short_description").getAsString());
                 URL=article.get("link").getAsString();
                articlecontent=article.get("short_description").getAsString();
            } else {
                content.setText("Content not available");
            }
            LLMAPI llmAPI = new LLMAPI();
            String requestBody = llmAPI.createRequestBody(articlecontent);
            String apiResponse = llmAPI.sendingRequest(requestBody);

            // Extract the predicted genre from the API response
            String genre = llmAPI.recieverequest(apiResponse);
            Webarticles(URL);
            System.out.println(genre);
            currentIndex = index;


        }
    }



    public void showNextArticle () throws JSONException {
        if (currentIndex < articles.size() - 1) {
            displayArticle(currentIndex + 1);
        }
    }


    public void showPreviousArticle () throws JSONException {
        if (currentIndex > 0) {
            displayArticle(currentIndex - 1);
        }
    }
}
