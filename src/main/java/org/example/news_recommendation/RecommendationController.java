package org.example.news_recommendation;

import com.google.gson.JsonArray;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.example.news_recommendation.Models.Database;
import org.example.news_recommendation.Models.User;
import org.example.news_recommendation.Models.news;
import org.example.news_recommendation.Models.recommendation;
import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

public class RecommendationController {
    private JsonArray filtered;
    @FXML
    private WebView web;
    private int currentIndex = 0;
    private static WebEngine eng;
    private Map<String, Double> categories;
    @FXML
    private TextArea title;
    @FXML
    private TextArea content;
    private String headline;
    private String URL;
    @FXML
    private Button back;

    private String name;

    private String genre;



    private String articlecontent = "";
   static recommendation recom = new recommendation();
   news newsInstance = new news();

   User user = new User();

   Database sql = new Database();

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
        this.title.setText("Loading personalized articles...");

        int status = recom.recommend();
        switch (status) {
            case 1:
                int check = recom.Recommendedarticles(0); // Display the first article
                if (check==0) {
                    filtered = recom.getArticles();
                    categories = recom.getCategories();
                    String head = recom.gettitle();
                    String cont = recom.getcontent();
                    title.setText(head);
                    content.setText(cont);
                }else {
                    title.setText("No articles found for selected category.");
                }
                break;

            case 0:
                title.setText("No articles found.");
                content.setText("");
                break;

            case 2:
                title.setText("Like an article to start");
                content.setText("");
                break;
        }
    }
    public void showNextArticle() throws JSONException {
        if (currentIndex < filtered.size() - 1) {
            int check =recom.Recommendedarticles(currentIndex + 1);
           if(check == 0) {
               String head = recom.gettitle();
               String cont = recom.getcontent();
               title.setText(head);
               content.setText(cont);

           }else{
               title.setText("No articles found for selected category.");
                content.setText("");
           }
        }
    }

    public void favarticle() throws ClassNotFoundException {
        URL=recom.getURL();
        headline=recom.gettitle();
        genre=recom.getgenre();
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


    public  void Webarticles(String url) {
        eng = web.getEngine();
        eng.load(url);

    }
    public void viewarticles() {
        URL=recom.getURL();
        if (URL != null && !URL.isEmpty()) {
            Webarticles(URL);
        } else {
            System.out.println("No valid URL to display.");
        }
    }
}
