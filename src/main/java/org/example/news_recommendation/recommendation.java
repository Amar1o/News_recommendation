package org.example.news_recommendation;
import javafx.fxml.FXML;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
public class recommendation {
    private List<String> genres = new ArrayList<>();
    private JsonArray articles;
    private JsonArray filtered;
    private JsonArray finals;
    @FXML
    private WebView web;
    private int currentIndex = 0;
    private WebEngine eng;
private Map<String, Double> categories;

    @FXML
    private TextArea title;

    @FXML
    private TextArea content;
    private String genre;
    private String headline;
    private String URL;

    private String name;
    private String articlecontent = "";
    // Assuming categories is a Map<String, Double>
    public void printCategories(Map<String, Double> categories) {
        if (categories != null && !categories.isEmpty()) {
            // Iterate over the map and print each key-value pair
            for (Map.Entry<String, Double> entry : categories.entrySet()) {
                System.out.println("Category: " + entry.getKey() + ", Probability: " + entry.getValue());
            }
        } else {
            System.out.println("Categories map is empty or null.");
        }
    }

    public int recommend() throws JSONException {
        // Use UserSession to get the full name
        String name = User.getInstance().getFirstName();// Get current user
        List<String> gens = getDBdata(name); // get the genre into an arraylist
        System.out.println(gens);


        String genresString = String.join(", ", gens); // turn into String values for LLM processing
        categories = LLMprobability(genresString);
        printCategories(categories);
        System.out.println("space1");
        if (categories == null || categories.isEmpty()) {
            System.err.println("LLM processing failed or returned no results.");
            return 0; // Return 0 for no results from LLM
        }//Process and recieve the arraylist storing category and probability

        String filePath = "src/main/java/org/example/news_recommendation/News_Category_Dataset_v3.json";
        jsonreader jsonArticleReader = new jsonreader(filePath);
        articles = jsonArticleReader.readFile();
        filtered = news.checkifliked(articles);
//        FilteredLikedArticles(finals);
//        String head = PersonalizedArticles(filtered, categories);
//        System.out.println(head);
       // addtoDB(finals, name);//process arraylist and add to database
        System.out.println(name);
        return 1;
    }

    public void Webarticles(String url) {
        eng = web.getEngine();
        String disablevideo = "document.querySelectorAll('video, audio').forEach(function(media) { media.autoplay = false; });";

        String disableImages = "document.querySelectorAll('img').forEach(function(img) { img.style.display = 'none'; });";
        eng.executeScript(disableImages);
        eng.executeScript(disablevideo);
        eng.load(url);
        setzoom(1.0);
    }

    public void viewarticles() {
        if (URL != null && !URL.isEmpty()) {
            Webarticles(URL);
        } else {
            System.out.println("No valid URL to display.");
        }
    }

    private void setzoom(double zoomFactor) {
        String zoomScript = "document.body.style.zoom = '" + zoomFactor + "';";
        eng.executeScript(zoomScript);
    }


    public String PersonalizedArticles(JsonArray filteredArticles, Map<String, Double> categories) {
        if (filteredArticles == null || filteredArticles.isEmpty() || categories == null || categories.isEmpty()) {
            return "No articles available.";
        }

        // Create weighted category selection
        List<String> weightedCategories = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categories.entrySet()) {
            String category = entry.getKey();
            double probability = entry.getValue();
            int weight = (int) (probability * 100); // Scale probability

            for (int i = 0; i < weight; i++) {
                weightedCategories.add(category);
            }
        }

        // Ensure we have weighted categories
        if (weightedCategories.isEmpty()) {
            return "No weighted categories found.";
        }

        // Randomly select category based on weights
        Random random = new Random();
        String selectedCategory = weightedCategories.get(random.nextInt(weightedCategories.size()));

        // Find all articles matching selected category
        List<JsonObject> matchingArticles = new ArrayList<>();
        for (int i = 0; i < filteredArticles.size(); i++) {
            JsonObject article = filteredArticles.get(i).getAsJsonObject();

            if (article.has("category") && article.has("headline")) {
                String articleCategory = article.get("category").getAsString();
                System.out.println(selectedCategory);
                System.out.println("---------------------------------------");
                System.out.println(articleCategory);


                if (articleCategory.equalsIgnoreCase(selectedCategory)) {
                    matchingArticles.add(article);
                }
            }
        }

        // Return random headline from matching articles
        if (!matchingArticles.isEmpty()) {
            JsonObject randomArticle = matchingArticles.get(random.nextInt(matchingArticles.size()));
            return randomArticle.get("headline").getAsString();
        }

        return "No articles found for selected category.";
    }

    public void displayArticleController() throws JSONException {
        int status = recommend();
        if (status == 1) {
            displayArticle(0); // Display the first article
        } else if (status == 0) {
            title.setText("No articles found.");
            content.setText("");

        }
    }

    public Map<String, Double> LLMprobability(String genres) throws JSONException {
        LLMAPI llmAPI = new LLMAPI();
        String requestBody = llmAPI.generaterecommend(genres);
        String apiResponse = llmAPI.sendingRequest(requestBody);
        Map<String, Double> result = llmAPI.extractedProbabilities(apiResponse);


        return result;
    }

    public void addtoDB(Map<String, Double> result, String currentUser) {
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/truy";
        String username = "root";
        String password = "";

        // Start constructing the update query
        StringBuilder updateQuery = new StringBuilder("UPDATE genres_table SET ");

        // Loop through the result map to dynamically add each genre and its probability
        for (Map.Entry<String, Double> entry : result.entrySet()) {
            String genre = entry.getKey();
            double probability = entry.getValue();

            // Append the genre column and its corresponding probability to the query
            updateQuery.append(genre).append(" = ?, ");
        }

        // Remove the last comma and space
        updateQuery.setLength(updateQuery.length() - 2);

        // Add the WHERE condition to specify the current user
        updateQuery.append(" WHERE first_name = ?");

        // Print the query for debugging purposes (optional)
        System.out.println("Generated SQL: " + updateQuery.toString());

        // Execute the query
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery.toString())) {

            // Set the probabilities for each genre in the statement
            int index = 1;
            for (Map.Entry<String, Double> entry : result.entrySet()) {
                updateStmt.setDouble(index++, entry.getValue());
            }

            // Set the user name in the WHERE clause
            updateStmt.setString(index, currentUser);

            // Execute the update statement
            int rowsUpdated = updateStmt.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("No matching record found for user: " + currentUser);
            } else {
                System.out.println("Successfully updated probabilities for user: " + currentUser);
            }

        } catch (SQLException e) {
            System.err.println("Error updating probabilities: " + e.getMessage());
            e.printStackTrace();
        }
    }


//    private void displayArticle(int index) throws JSONException {
//        if (filtered != null && index >= 0 && index < filtered.size()) {
//            JsonObject article = filtered.get(index).getAsJsonObject();
//            String head = PersonalizedArticles(filtered, categories);
//            // Check for title field and handle JsonNull
//            if (article.has("headline") && !article.get("headline").isJsonNull()) {
//                title.setText(article.get("headline").getAsString());
//                headline = article.get("headline").getAsString();
//            } else {
//                title.setText("Title not available");
//            }
//
//
//            // Check for content field and handle JsonNull
//            if (article.has("short_description") && !article.get("short_description").isJsonNull()) {
//                content.setText(article.get("short_description").getAsString());
//                URL = article.get("link").getAsString();
//                articlecontent = article.get("short_description").getAsString();
//            } else {
//                content.setText("Content not available");
//            }
//
//            currentIndex = index;
//
//        }
//    }
public void displayArticle(int index) throws JSONException {
    if (filtered == null || filtered.isEmpty()) {
        title.setText("No articles available");
        content.setText("");
        return;
    }

    // Ensure categories is populated before PersonalizedArticles call
    if (categories == null || categories.isEmpty()) {
        title.setText("No user preferences found");
        content.setText("");
        return;
    }

    // Get personalized headline
    String personalizedHeadline = PersonalizedArticles(filtered, categories);
    System.out.println(personalizedHeadline);

    // Find matching article
    JsonObject selectedArticle = null;
    for (int i = 0; i < filtered.size(); i++) {
        JsonObject article = filtered.get(i).getAsJsonObject();
        if (article.has("headline") &&
                article.get("headline").getAsString().equals(personalizedHeadline)) {
            selectedArticle = article;
            break;
        }
    }

    // Display article details
    if (selectedArticle != null) {
        headline = selectedArticle.get("headline").getAsString();
        title.setText(headline);

        // Handle content
        if (selectedArticle.has("short_description") &&
                !selectedArticle.get("short_description").isJsonNull()) {
            articlecontent = selectedArticle.get("short_description").getAsString();
            content.setText(articlecontent);

            // Set URL if available
            URL = selectedArticle.has("link") ?
                    selectedArticle.get("link").getAsString() : "";
        } else {
            content.setText("Content not available");
        }

        currentIndex = index;
    } else {
        title.setText("No personalized article found");
        content.setText("No matching article available.");
    }
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

    public List<String> getDBdata(String name) {
        String url = "jdbc:mysql://localhost:3306/truy";
        String username = "root";
        String password = "";

        // Query to retrieve all genres for the given name
        String query = "SELECT genre FROM preference WHERE name = ?";


        try {
            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                // Set the parameter for the name in the query
                statement.setString(1, name);

                // Execute the query and process the result
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        // Add each genre to the list
                        genres.add(resultSet.getString("genre"));
                    }
                }
            }

            if (genres.isEmpty()) {
                genres.add("No data found for name: " + name);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving data: " + e.getMessage());
            e.printStackTrace();
            genres.add("Error retrieving data.");
        }

        return genres;
    }

}
