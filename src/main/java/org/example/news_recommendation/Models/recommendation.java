package org.example.news_recommendation.Models;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.example.news_recommendation.jsonreader;
import org.json.JSONException;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
public class recommendation {

    private static JsonArray articles;

    private static JsonArray filtered;
    private static int currentIndex = 0;
    private static Map<String, Double> categories;
    private static String headline;
    private static String URL;
    private static String articlecontent = "";
    private List<String> gens = new ArrayList<>();
    static User user = new User();
    static Database sql = new Database();
    static LLMAPI ll = new LLMAPI();
    private String genre;
    String filePath = "src/main/java/org/example/news_recommendation/News_Category_Dataset_v3.json";
    jsonreader jsonread = new jsonreader(filePath);
    public int recommend() throws JSONException {
        // Use UserSession to get the full name
        gens.clear();

        String name = user.getInstance().getFirstName();// Get current user
        gens = sql.getDBdata(name); // get the genre into an arraylist
        System.out.println(gens);
        if (gens.contains("No data found for name: " + name)) {
            System.out.println("no articles");
            return 2; // Return 2 if the message is found
        }

        String genresString = String.join(", ", gens); // turn into String values for LLM processing
        categories = ll.LLMprobability(genresString);
        System.out.println("space1");
        if (categories == null || categories.isEmpty()) {
            System.err.println("LLM processing failed or returned no results.");
            return 0; // Return 0 for no results from LLM
        }//Process and recieve the arraylist storing category and probability

        articles = jsonread.readFile();
        filtered = sql.checkifliked(articles);
//        articles = newsInstance.getArticles();
//        filtered = sql.checkifliked(articles);
        System.out.println(name);
        return 1;
    }


    public  String PersonalizedArticles(JsonArray filteredArticles, Map<String, Double> categories) {
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
        genre = selectedCategory;

        System.out.println(selectedCategory);
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



    public JsonArray getArticles(){
        return filtered;
    }
    public  String gettitle() {

        return headline;
    }
    public  String getgenre(){
        return genre;
    }
    public  String getcontent(){

        return articlecontent;
    }
    public  Map<String,Double> getCategories(){

        return categories;
    }
    public String getURL(){
        return URL;
    }

    public int Recommendedarticles(int index) throws JSONException {

        // Get personalized headline
        String personalizedHeadline = PersonalizedArticles(filtered, categories);
        System.out.println(personalizedHeadline);
        if (personalizedHeadline== "No articles found for selected category."){
            return 1;

        }

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


            // Handle content
            if (selectedArticle.has("short_description") &&
                    !selectedArticle.get("short_description").isJsonNull()) {
                articlecontent = selectedArticle.get("short_description").getAsString();


                // Set URL if available
                URL = selectedArticle.has("link") ?
                        selectedArticle.get("link").getAsString() : "";

            }

            currentIndex = index;

        }
        return 0;
    }

}
