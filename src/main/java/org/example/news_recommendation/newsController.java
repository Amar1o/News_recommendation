package org.example.news_recommendation;

import com.google.gson.JsonArray;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.example.news_recommendation.Models.Database;
import org.example.news_recommendation.Models.User;
import org.example.news_recommendation.Models.news;
import org.json.JSONException;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class newsController {




    @FXML
    private TextArea title;

    @FXML
    private TextArea content;

    private JsonArray articles; // Holds all articles received from the API
    private int currentIndex = 0; // Keeps track of the currently displayed article
    private String genre;
    private String headline;
    private String URL;

    private String name;
    @FXML
    private WebView web;



    private static WebEngine eng;

   // private ExecutorService executorService = Executors.newFixedThreadPool(3);
    private CompletableFuture<Void> webLoadingFuture;
    private ExecutorService executorService  = Executors.newSingleThreadExecutor();
    static User user = new User();
    static Database sql = new Database();
    news newsInstance = new news();
    public void favarticle() throws ClassNotFoundException {
        URL=newsInstance.getURL();
        headline=newsInstance.gettitle();
        genre=newsInstance.getgenre();
        if (headline != null && genre != null && URL != null) {
            name = user.getInstance().getFirstName();
            sql.AddtoDB(name,headline,genre,URL);
        } else {
            System.out.println("No valid article to add to favorites.");
            this.content.setText("No valid article to add to favourites");
            System.out.println(headline);
            System.out.println(genre);
            System.out.println(URL);

        }
    }

    @FXML
    private void switchtorecommended(ActionEvent event) {

        // Proceed to load the new scene
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("personalizedarticles.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Personalized Articles");
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load personalizedarticles.fxml.");
        }
    }


    @FXML
    public void loadArticlesbutton() {
        String filePath = "src/main/java/org/example/news_recommendation/News_Category_Dataset_v3.json";
        this.title.setText("Loading articles...");

        executorService.submit(() -> {
            try {
                int status = news.loadarticlesController(filePath);

                Platform.runLater(() -> {
                    if (status == 0) {
                        try {
                            newsInstance.displayArticle(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            this.title.setText("Error");
                            this.content.setText("An error occurred while displaying the article.");
                            return;
                        }

                        String titl = newsInstance.gettitle();
                        String contnt = newsInstance.getcontent();

                        this.content.setText(contnt);
                        this.title.setText(titl);
                        articles = newsInstance.getArticles();
                    } else {
                        this.title.setText("Error fetching articles.");
                        this.content.setText("Unable to load articles. Please check the file format and content.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    this.title.setText("Error");
                    this.content.setText("An unexpected error occurred.");
                    e.printStackTrace();
                });
            }
        });
    }


    public void showNextArticle() throws JSONException {
        if (newsInstance.getCurrentIndex() < articles.size() - 1) {
            newsInstance.displayArticle(currentIndex ++);
            String titl= newsInstance.gettitle();
            String contnt= newsInstance.getcontent();

            this.content.setText(contnt);
            this.title.setText(titl);

        }
    }


    public void showPreviousArticle() throws JSONException {
        if (newsInstance.getCurrentIndex() > 0) {
            newsInstance.displayArticle(currentIndex --);
            String titl= newsInstance.gettitle();
            String contnt= newsInstance.getcontent();

            this.content.setText(contnt);
            this.title.setText(titl);
        }
    }

    public  void Webarticles(String url) {
        eng = web.getEngine();
        eng.load(url);
    }
    @FXML
    public void viewarticles() {
        URL=newsInstance.getURL();

        if (URL != null && !URL.isEmpty()) {

            Webarticles(URL);
            web.setVisible(true);  // Show the WebView when content is ready
        }
        else {
            System.out.println("No valid URL to display.");
        }
    }

    @FXML
    private void Viewliked(ActionEvent event) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewLiked.fxml"));
            Parent root = loader.load();

            // Create a new Stage (window)
            Stage newStage = new Stage();
            newStage.setTitle("View Liked");

            // Set the scene for the new stage
            Scene scene = new Scene(root);
            newStage.setScene(scene);

            newStage.setResizable(false);

            // Show the new stage
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load ViewLiked.fxml.");
        }
    }

}
