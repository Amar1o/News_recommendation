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
import java.util.ArrayList;

public class news {

    @FXML
    private  WebView web;

    private WebEngine eng;


    private ArrayList<String[]> favoriteArticles = new ArrayList<String[]>();

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
//    public news(String URL,String description){
//        URL=URL;
//        description;
//    }

private String genre;
private String headline;
private String URL;

    public void Webarticles(String url){
        eng = web.getEngine();
        String disablevideo = "document.querySelectorAll('video, audio').forEach(function(media) { media.autoplay = false; });";

        String disableImages = "document.querySelectorAll('img').forEach(function(img) { img.style.display = 'none'; });";
        eng.executeScript(disableImages);
        eng.executeScript(disablevideo);
         eng.load(url);
        setzoom(1.0);
    }
    private void setzoom(double zoomFactor) {
        String zoomScript = "document.body.style.zoom = '" + zoomFactor + "';";
        eng.executeScript(zoomScript);
    }

    public int loadarticlesController(String filepath) {
        try {
            jsonreader jsonArticleReader = new jsonreader(filepath);
            articles = jsonArticleReader.readFile(); // Fetch articles from the JSON file

            // Check if articles were retrieved successfully
            if (articles != null && articles.size() > 0) {
                return 0;
            } else {
                return 1;
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void loadArticlesbutton() throws JSONException {
        String filePath = "src/main/java/org/example/news_recommendation/News_Category_Dataset_v3.json";
            int status = loadarticlesController(filePath);
            if (status == 0) {
                displayArticle(0); // Display the first article
            } else if (status == 1) {
                title.setText("No articles found.");
                content.setText("");
            } else {
                title.setText("Error fetching articles.");
                content.setText("Unable to load articles. Please check the file format and content.");
            }
    }

    private void displayArticle( int index) throws JSONException {
        if (articles != null && index >= 0 && index < articles.size()) {
            JsonObject article = articles.get(index).getAsJsonObject();

            // Check for title field and handle JsonNull
            if (article.has("headline") && !article.get("headline").isJsonNull()) {
                title.setText(article.get("headline").getAsString());
                headline=article.get("headline").getAsString();
            } else {
                title.setText("Title not available");
            }

            String articlecontent="";
            // Check for content field and handle JsonNull
            if (article.has("short_description") && !article.get("short_description").isJsonNull()) {
                content.setText(article.get("short_description").getAsString());
                 URL=article.get("link").getAsString();
                articlecontent=article.get("short_description").getAsString();
            } else {
                content.setText("Content not available");
            }

             genre = LLM(articlecontent);
           // Webarticles(URL);
            System.out.println(genre);
            currentIndex = index;


        }
    }



    public String LLM(String articlecontent) throws JSONException {
        LLMAPI llmAPI = new LLMAPI();
        String requestBody = llmAPI.createRequestBody(articlecontent);
        String apiResponse = llmAPI.sendingRequest(requestBody);
        String genre = llmAPI.recieverequest(apiResponse);


        return genre;
    }


    public void viewarticles() {
        if (URL != null && !URL.isEmpty()) {
            Webarticles(URL);
        } else {
            System.out.println("No valid URL to display.");
        }
    }

    public void favarticle() {
        if (headline != null && genre != null && URL != null) {
            // Save the current article data (headline, genre, URL) into favorite articles
            String[] articleData = {headline, genre, URL};
            favoriteArticles.add(articleData);
            System.out.println("Article added to favorites: " + headline);
        } else {
            System.out.println("No valid article to add to favorites.");
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
