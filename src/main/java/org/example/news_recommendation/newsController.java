package org.example.news_recommendation;

import com.google.gson.JsonArray;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class newsController {

    @FXML
    private WebView web;

    private WebEngine eng;

    @FXML
    private Button submit;

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
    private ExecutorService executorService = Executors.newFixedThreadPool(3);
    private CompletableFuture<Void> webLoadingFuture;
    private ExecutorService webExecutor = Executors.newSingleThreadExecutor();

    static Database sql = new Database();
    news newsInstance = new news();
    public void favarticle() throws ClassNotFoundException {
        URL=news.getURL();
        headline=news.gettitle();
        genre=news.getgenre();
        if (headline != null && genre != null && URL != null) {
            name = User.getInstance().getFirstName();
            sql.AddtoDB(name,headline,genre,URL);
            System.out.println("Article added to favorites: " + headline);
        } else {
            System.out.println("No valid article to add to favorites.");
            System.out.println(headline);
            System.out.println(genre);
            System.out.println(URL);

        }
    }
    @FXML
    private void switchtorecommended(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("personalizedarticles.fxml"));
            Parent root = loader.load();

            // Get the current stage (window)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene on the stage
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

        new Thread(() -> {
            try {
                int status = news.loadarticlesController(filePath);

                Platform.runLater(() -> {
                    if (status == 0) {
                        try {
                            newsInstance.displayArticle(0);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        String titl = newsInstance.gettitle();
                        String contnt = newsInstance.getcontent();

                        this.content.setText(contnt);
                        this.title.setText(titl);
                        articles = newsInstance.getArticles();
                    } else if (status == 1) {
                        this.title.setText("No articles found.");
                        this.content.setText("");
                    } else {
                        this.title.setText("Error fetching articles.");
                        this.content.setText("Unable to load articles. Please check the file format and content.");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    this.title.setText("Error");
                    this.content.setText("An unexpected error occurred.");
                });
            }
        }).start();
    }
    public void showNextArticle() throws JSONException {
        if (news.getCurrentIndex() < articles.size() - 1) {
            newsInstance.displayArticle(currentIndex ++);
            String titl= newsInstance.gettitle();
            String contnt= newsInstance.getcontent();

            this.content.setText(contnt);
            this.title.setText(titl);

        }
    }


    public void showPreviousArticle() throws JSONException {
        if (news.getCurrentIndex() > 0) {
            newsInstance.displayArticle(currentIndex --);
            String titl= newsInstance.gettitle();
            String contnt= newsInstance.getcontent();

            this.content.setText(contnt);
            this.title.setText(titl);
        }
    }
    @FXML
    public void viewarticles() {
        if (URL != null && !URL.isEmpty()) {
            // If content is still loading, wait for it to complete
            if (webLoadingFuture != null && !webLoadingFuture.isDone()) {
                webLoadingFuture.thenRun(() -> {
                    Platform.runLater(() -> {
                        web.setVisible(true);  // Show the WebView when content is ready
                    });
                });
            } else {
                // If not already loading, start loading now
                news.Webarticles(URL);
                web.setVisible(true);
            }
        } else {
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

            // Optional: Disable resizing of the new window
            newStage.setResizable(false);

            // Show the new stage
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load ViewLiked.fxml.");
        }
    }

}
