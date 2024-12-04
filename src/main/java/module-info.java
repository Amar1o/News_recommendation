module org.example.news_recommendation {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires stanford.corenlp;
    requires org.json.chargebee;
    requires java.net.http;
    requires javafx.web;


    opens org.example.news_recommendation to javafx.fxml;
    exports org.example.news_recommendation;
    exports org.example.news_recommendation.Models;
    opens org.example.news_recommendation.Models to javafx.fxml;
}