package org.example.news_recommendation.Models;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.example.news_recommendation.Models.Database;
import org.example.news_recommendation.Models.LLMAPI;
import org.example.news_recommendation.jsonreader;
import org.json.JSONException;

public class news {

    private static JsonArray articles; // Holds all articles received from the API
    private static int currentIndex = 0; // Keeps track of the currently displayed article
    private static JsonArray filtered;

    private static String genre;
    private static String headline;
    private static String URL;

    private static String articlecontent = "";

    static Database sql = new Database();
    static LLMAPI ll = new LLMAPI();

    public static int loadarticlesController(String filepath) {

        try {
            jsonreader jsonArticleReader = new jsonreader(filepath);
            articles = jsonArticleReader.readFile(); // Fetch articles from the JSON file
            filtered= sql.checkifliked(articles);
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




    public  void displayArticle(int index) throws JSONException {
        if (filtered != null && index >= 0 && index < filtered.size()) {
            JsonObject article = filtered.get(index).getAsJsonObject();
            // Check for title field and handle JsonNull
            if (article.has("headline") && !article.get("headline").isJsonNull()) {
                URL = article.get("link").getAsString();


                headline = article.get("headline").getAsString();
                articlecontent = article.get("short_description").getAsString();
            } else {
                headline = "no title available";
            }
            genre = ll.LLM(articlecontent);
            System.out.println(genre);
            System.out.println(headline);
            currentIndex = index;
        }
    }


    public int getCurrentIndex(){
        return currentIndex;
    }
    public  String getURL(){

        return  URL;
    }
    public  String gettitle() {

        return headline;
    }
    public String getcontent(){
        return articlecontent;
    }
    public  String getgenre(){
        return genre;
    }
    public JsonArray getArticles(){

        return filtered;
    }

}