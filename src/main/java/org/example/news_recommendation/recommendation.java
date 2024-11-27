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
//    private static List<String> genres = new ArrayList<>();
    private static JsonArray articles;
    private static JsonArray filtered;
    private JsonArray finals;
    @FXML
    private static WebView web;
    private static int currentIndex = 0;
    private static WebEngine eng;
private static Map<String, Double> categories;

    @FXML
    private TextArea title;

    @FXML
    private static TextArea content;
    private String genre;
    private static String headline;
    private static String URL;

    private String name;
    private static String articlecontent = "";

    static Database sql = new Database();
    static LLMAPI ll = new LLMAPI();
    public static void printCategories(Map<String, Double> categories) {
        if (categories != null && !categories.isEmpty()) {
            // Iterate over the map and print each key-value pair
            for (Map.Entry<String, Double> entry : categories.entrySet()) {
                System.out.println("Category: " + entry.getKey() + ", Probability: " + entry.getValue());
            }
        } else {
            System.out.println("Categories map is empty or null.");
        }
    }

    public static int recommend() throws JSONException {
        // Use UserSession to get the full name
        String name = User.getInstance().getFirstName();// Get current user
        List<String> gens = sql.getDBdata(name); // get the genre into an arraylist
        System.out.println(gens);


        String genresString = String.join(", ", gens); // turn into String values for LLM processing
        categories = ll.LLMprobability(genresString);
        printCategories(categories);
        System.out.println("space1");
        if (categories == null || categories.isEmpty()) {
            System.err.println("LLM processing failed or returned no results.");
            return 0; // Return 0 for no results from LLM
        }//Process and recieve the arraylist storing category and probability

        String filePath = "src/main/java/org/example/news_recommendation/News_Category_Dataset_v3.json";
        jsonreader jsonArticleReader = new jsonreader(filePath);
        articles = jsonArticleReader.readFile();
        filtered = sql.checkifliked(articles);

        System.out.println(name);
        return 1;
    }

    public static void Webarticles(String url) {
        eng = web.getEngine();
        String disablevideo = "document.querySelectorAll('video, audio').forEach(function(media) { media.autoplay = false; });";

        String disableImages = "document.querySelectorAll('img').forEach(function(img) { img.style.display = 'none'; });";
        eng.executeScript(disableImages);
        eng.executeScript(disablevideo);
        eng.load(url);
        setzoom(1.0);
    }

    private static void setzoom(double zoomFactor) {
        String zoomScript = "document.body.style.zoom = '" + zoomFactor + "';";
        eng.executeScript(zoomScript);
    }


    public static String PersonalizedArticles(JsonArray filteredArticles, Map<String, Double> categories) {
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


    public static JsonArray getArticles(){

        return filtered;
    }
    public static String gettitle() {

        return headline;
    }
    public static String getcontent(){

        return articlecontent;
    }
    public static Map<String,Double> getCategories(){

        return categories;
    }
public static void Recommendedarticles(int index) throws JSONException {

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
//        title.setText(headline);

        // Handle content
        if (selectedArticle.has("short_description") &&
                !selectedArticle.get("short_description").isJsonNull()) {
            articlecontent = selectedArticle.get("short_description").getAsString();
//            content.setText(articlecontent);

            // Set URL if available
            URL = selectedArticle.has("link") ?
                    selectedArticle.get("link").getAsString() : "";

        }

        currentIndex = index;

    }
}

}
