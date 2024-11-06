package org.example.news_recommendation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class jsonreader {

        private final   String filePath;

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
    }


