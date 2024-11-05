module org.example.news_recommendation {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.news_recommendation to javafx.fxml;
    exports org.example.news_recommendation;
}