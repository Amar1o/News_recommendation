package org.example.news_recommendation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.json.JSONException;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class news {

    @FXML
    private static WebView web;

    private static WebEngine eng;

    @FXML
    private Button submit;

    @FXML
    private  TextArea title;

    @FXML
    private  TextArea content;
    @FXML
    private Button prev;
    @FXML
    private Button forward;
    private static JsonArray articles; // Holds all articles received from the API
    private static int currentIndex = 0; // Keeps track of the currently displayed article
private static JsonArray filtered;

    private static String genre;
    private static String headline;
    private static String URL;

    private String name;
    private static String articlecontent = "";
    private ExecutorService executorService = Executors.newFixedThreadPool(3);
    private static CompletableFuture<Void> webLoadingFuture;
    private static ExecutorService webExecutor = Executors.newSingleThreadExecutor();



    public static void Webarticles(String url) {
        // If already loading this URL, don't start again
        if (webLoadingFuture != null && !webLoadingFuture.isDone()) {
            return;
        }

        webLoadingFuture = CompletableFuture.runAsync(() -> {
            Platform.runLater(() -> {
                if (eng == null) {
                    eng = web.getEngine();
                }

                String disablevideo = "document.querySelectorAll('video, audio').forEach(function(media) { media.autoplay = false; });";
                String disableImages = "document.querySelectorAll('img').forEach(function(img) { img.style.display = 'none'; });";

                eng.load(url);

                // Wait for page load to complete before running scripts
                eng.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        eng.executeScript(disableImages);
                        eng.executeScript(disablevideo);
                        setzoom(1.0);
                    }
                });
            });
        }, webExecutor);
    }


    private static void setzoom(double zoomFactor) {
        String zoomScript = "document.body.style.zoom = '" + zoomFactor + "';";
        eng.executeScript(zoomScript);
    }

    public static JsonArray checkifliked(JsonArray articles) {
        CompletableFuture<JsonArray> future = CompletableFuture.supplyAsync(() -> {
            String url = "jdbc:mysql://localhost:3306/news";
            String username = "root";
            String password = "";
            String query = "SELECT headline FROM preference WHERE name = ? AND headline = ?";
            String name = User.getInstance().getFirstName();
            JsonArray filteredArticles = new JsonArray();

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                for (int i = 0; i < articles.size(); i++) {
                    JsonObject article = articles.get(i).getAsJsonObject();
                    String headline = article.get("headline").getAsString();

                    statement.setString(1, name);
                    statement.setString(2, headline);

                    try (ResultSet rs = statement.executeQuery()) {
                        if (!rs.next()) {
                            filteredArticles.add(article);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return filteredArticles;
        });

        try {
            return future.get(); // Wait for the result
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonArray();
        }
    }
    public static int loadarticlesController(String filepath) {
        try {
            jsonreader jsonArticleReader = new jsonreader(filepath);
            articles = jsonArticleReader.readFile(); // Fetch articles from the JSON file
            filtered=checkifliked(articles);
            // Check if articles were retrieved successfully
            if (filtered != null && filtered.size() > 0) {
                return 0;
            } else {
                return 1;
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public JsonArray getArticles(){
        return filtered;
    }


    public  void displayArticle(int index) throws JSONException {
        if (filtered != null && index >= 0 && index < filtered.size()) {
            JsonObject article = filtered.get(index).getAsJsonObject();


            // Check for title field and handle JsonNull
            if (article.has("headline") && !article.get("headline").isJsonNull()) {
//                this.content.setText(article.get("short_description").getAsString());
                URL = article.get("link").getAsString();

//                this.title.setText(article.get("headline").getAsString());
                headline = article.get("headline").getAsString();
                articlecontent = article.get("short_description").getAsString();
            } else {
                this.title.setText("Title not available");
            }
            genre = LLM(articlecontent);
            System.out.println(genre);
            System.out.println(headline);
            currentIndex = index;
        }
    }
public static String getURL(){
        return  URL;
}
    public static String gettitle() {

        return headline;
    }
    public String getcontent(){
        return articlecontent;
    }
public static String getgenre(){
        return genre;
}

    public static String LLM(String articlecontent) throws JSONException {
        LLMAPI llmAPI = new LLMAPI();
        String requestBody = llmAPI.createRequestBody(articlecontent);
        String apiResponse = llmAPI.sendingRequest(requestBody);


        return llmAPI.recieverequest(apiResponse);
    }





    public static int AddtoDB(String name, String headline, String genre, String URL) throws ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/news";
        String username = "root";
        String password = "";

        // SQL query to check if the article already exists
        String checkSQL = "SELECT COUNT(*) FROM preference WHERE name = ? AND headline = ?";

        // SQL query to insert a new article if not already in the database
        String insertSQL = "INSERT INTO preference (name, headline, genre, URL) VALUES (?, ?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure MySQL driver is loaded

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement checkStmt = connection.prepareStatement(checkSQL)) {

                // Set parameters for the check query
                checkStmt.setString(1, name);
                checkStmt.setString(2, headline);

                // Execute the check query
                try (ResultSet resultSet = checkStmt.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        if (count > 0) {
                            // If an entry already exists with the same name and headline
                            System.out.println("Article already exists in the database.");
                            return 1; // Indicating that the article already exists
                        }
                    }
                }

                // If no existing article was found, proceed to insert the new article
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {
                    // Set parameters for the insert
                    insertStmt.setString(1, name);
                    insertStmt.setString(2, headline);
                    insertStmt.setString(3, genre);
                    insertStmt.setString(4, URL);

                    // Execute the insert statement
                    int rowsAffected = insertStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Preference added successfully! " + rowsAffected + " row(s) affected.");
                        return 0; // Success
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL driver not included in path.");
            e.printStackTrace();
            return -1;
        } catch (SQLException e) {
            System.out.println("Database connection error or query issue.");
            e.printStackTrace();
            return -1;
        }

        return -1; // Default case (unexpected error)
    }

}