package org.example.news_recommendation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class jsonreader {

    private final String filePath;

    public jsonreader(String filePath) {
        this.filePath = filePath;
    }

    public JsonArray readFile() {
        JsonArray jsonArray = new JsonArray();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                JsonObject jsonObject = JsonParser.parseString(line).getAsJsonObject();
                jsonArray.add(jsonObject);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }


    public void readFileWithRateLimit() {
        JsonArray jsonArray = readFile();

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject article = jsonArray.get(i).getAsJsonObject();

            // Print or process the article (for demonstration purposes)
            System.out.println("Processed article: " + article);

            try {
                // Wait for 2 seconds to ensure the rate of 30 articles per minute
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread was interrupted");
            }
        }
    }

}
