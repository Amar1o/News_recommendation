package org.example.news_recommendation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.json.JSONException;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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

    static Database sql = new Database();
    static LLMAPI ll = new LLMAPI();
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


    public JsonArray getArticles(){
        return filtered;
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
                this.title.setText("Title not available");
            }
            genre = ll.LLM(articlecontent);
            System.out.println(genre);
            System.out.println(headline);
            currentIndex = index;
        }
    }

    public static int getCurrentIndex(){
        return currentIndex;
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


}