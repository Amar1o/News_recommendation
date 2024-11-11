package org.example.news_recommendation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ArticleCategorizer {
    private StanfordCoreNLP pipeline;

    // Constructor to set up the NLP pipeline
    public ArticleCategorizer() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, sentiment");
        this.pipeline = new StanfordCoreNLP(props);
    }

    // Method to read and process articles from a JSON file using Gson
    public void processArticles(String filePath) {
        try {
            // Read file content as a string
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));

            // Parse the JSON string using Gson
            JsonElement jsonTree = JsonParser.parseString(jsonContent);

            if (jsonTree.isJsonArray()) {
                JsonArray articlesArray = jsonTree.getAsJsonArray();

                for (JsonElement articleElement : articlesArray) {
                    JsonObject articleObject = articleElement.getAsJsonObject();
                    String articleText = articleObject.get("content").getAsString(); // Adjust key as needed
                    categorizeArticle(articleText);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to categorize an article based on its content
    private void categorizeArticle(String text) {
        // Create an Annotation object and annotate it
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        // Loop through sentences in the document and get sentiment information using SentimentAnnotatedTree
        for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {
            // Get the annotated parse tree of the sentence
            Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);

            // Get the sentiment score from the tree
            String sentiment = tree.toString(); // You can modify this to get more details from the tree

            // Print out the sentiment information
            System.out.println("Sentiment Tree: " + sentiment);
        }
    }

    public static void main(String[] args) {
        ArticleCategorizer categorizer = new ArticleCategorizer();
        categorizer.processArticles("src/main/java/org/example/news_recommendation/News_Category_Dataset_v3.json");
    }
}
