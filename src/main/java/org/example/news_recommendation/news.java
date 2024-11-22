package org.example.news_recommendation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.json.JSONException;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.*;

public class news {

    @FXML
    private WebView web;

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
private JsonArray filtered;

    private String genre;
    private String headline;
    private String URL;

    private String name;
    private String articlecontent = "";

    public void Webarticles(String url) {
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

    public static JsonArray checkifliked(JsonArray articles) {
        String url = "jdbc:mysql://localhost:3306/truy";
        String username = "root";
        String password = "";

        String query = "SELECT headline FROM preference WHERE name = ? AND headline = ?";

        String name = User.getInstance().getFirstName();
        JsonArray filteredArticles = new JsonArray();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                // Iterate through each article in the input JsonArray
                for (int i = 0; i < articles.size(); i++) {
                    JsonObject article = articles.get(i).getAsJsonObject(); // Get each article as JsonObject
                    String headline = article.get("headline").getAsString(); // Get headline from JsonObject

                    statement.setString(1, name);
                    statement.setString(2, headline);

                    try (ResultSet rs = statement.executeQuery()) {
                        if (!rs.next()) { // If the article is not liked by the user
                            filteredArticles.add(article); // Add to filtered articles
                        }
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }

        return filteredArticles; // Return the final filtered JsonArray
    }
    public int loadarticlesController(String filepath) {
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

    private void displayArticle(int index) throws JSONException {
        if (filtered != null && index >= 0 && index < filtered.size()) {
            JsonObject article = filtered.get(index).getAsJsonObject();

            // Check for title field and handle JsonNull
            if (article.has("headline") && !article.get("headline").isJsonNull()) {
                title.setText(article.get("headline").getAsString());
                headline = article.get("headline").getAsString();
            } else {
                title.setText("Title not available");
            }


            // Check for content field and handle JsonNull
            if (article.has("short_description") && !article.get("short_description").isJsonNull()) {
                content.setText(article.get("short_description").getAsString());
                URL = article.get("link").getAsString();
                articlecontent = article.get("short_description").getAsString();
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


        return llmAPI.recieverequest(apiResponse);
    }



    public void viewarticles() {
        if (URL != null && !URL.isEmpty()) {
            Webarticles(URL);
        } else {
            System.out.println("No valid URL to display.");
        }
    }

    public void favarticle() throws ClassNotFoundException {
        if (headline != null && genre != null && URL != null) {
            name = User.getInstance().getFirstName();
            AddtoDB(name,headline,genre,URL);
            System.out.println("Article added to favorites: " + headline);
        } else {
            System.out.println("No valid article to add to favorites.");
        }
    }

    public int AddtoDB(String name, String headline, String genre, String URL) throws ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/truy";
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


    public void showNextArticle() throws JSONException {
        if (currentIndex < articles.size() - 1) {
            displayArticle(currentIndex + 1);

        }
    }


    public void showPreviousArticle() throws JSONException {
        if (currentIndex > 0) {
            displayArticle(currentIndex - 1);
        }
    }
}