module org.example.news_recommendation {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.news_recommendation to javafx.fxml;
    exports org.example.news_recommendation;
}