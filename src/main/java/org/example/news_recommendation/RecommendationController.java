package org.example.news_recommendation;

import com.google.gson.JsonArray;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecommendationController {
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
@FXML
private Button back;
    private String name;
    private String articlecontent = "";
    recommendation recom = new recommendation();

    @FXML
    private void switchtoarticle() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Article.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) back.getScene().getWindow();


            stage.setTitle("Articles");

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.show();




        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load Article.fxml.");
        }
    }

    public void displayArticleController() throws JSONException {




        int status = recommendation.recommend();
        if (status == 1) {
            recommendation.displayArticle(0); // Display the first article
            filtered= recommendation.getArticles();
            categories = recommendation.getCategories();
            String head=recommendation.gettitle();
            String cont=recommendation.getcontent();
            title.setText(head);
            content.setText(cont);
            articles=recommendation.getArticles();

            if (filtered == null || filtered.isEmpty()) {
                title.setText("No articles available");
                content.setText("");
                return;
            }
        } else if (status == 0) {
            title.setText("No articles found.");
            content.setText("");

        }
    }
    public void showNextArticle() throws JSONException {
        if (currentIndex < articles.size() - 1) {
            recommendation.displayArticle(currentIndex + 1);
           String head=recommendation.gettitle();
           String cont=recommendation.getcontent();
            title.setText(head);
            content.setText(cont);


        }
    }


    public void showPreviousArticle() throws JSONException {
        if (currentIndex > 0) {
            recommendation.displayArticle(currentIndex - 1);
            title.setText(headline);
            content.setText(articlecontent);
        }
    }
    public void viewarticles() {
        if (URL != null && !URL.isEmpty()) {
            recommendation.Webarticles(URL);
        } else {
            System.out.println("No valid URL to display.");
        }
    }
}
