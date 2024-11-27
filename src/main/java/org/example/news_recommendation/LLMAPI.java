package org.example.news_recommendation;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
public class LLMAPI {
    private String apiKey= "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";
    private HttpClient client;
    private String apiurl= "https://api.groq.com/openai/v1/chat/completions";

    public LLMAPI() {
        this.client = HttpClient.newHttpClient(); // Initialize HttpClient
    }


//
public String createRequestBody(String newsDescription) {
    return "{\n" +
            "    \"model\": \"llama3-8b-8192\",\n" +
            "    \"messages\": [\n" +
            "        {\n" +
            "            \"role\": \"user\",\n" +
            "            \"content\": \"Analyze this news article and determine its category: " +
            "POLITICS, WELLNESS, ENTERTAINMENT, TRAVEL, STYLE & BEAUTY, PARENTING, " +
            "HEALTHY LIVING, QUEER VOICES, FOOD & DRINK, BUSINESS, COMEDY, SPORTS, " +
            "BLACK VOICES, HOME & LIVING, PARENTS. " +
            "Respond with category only in one word:  " +
            newsDescription.replace("\"", "\\\"") + "\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
}
    public Map<String, Double> LLMprobability(String genres) throws JSONException {
        LLMAPI llmAPI = new LLMAPI();
        String requestBody = llmAPI.generaterecommend(genres);
        String apiResponse = llmAPI.sendingRequest(requestBody);
        Map<String, Double> result = llmAPI.extractedProbabilities(apiResponse);


        return result;
    }


    public String LLM(String articlecontent) throws JSONException {
        LLMAPI llmAPI = new LLMAPI();
        String requestBody = llmAPI.createRequestBody(articlecontent);
        String apiResponse = llmAPI.sendingRequest(requestBody);


        return llmAPI.recieverequest(apiResponse);
    }

    public String generaterecommend(String genre) {

        return "{\n" +
                "    \"model\": \"llama3-8b-8192\",\n" +
                "    \"messages\": [\n" +
                "        {\n" +
                "            \"role\": \"user\",\n" +
                "            \"content\": \"Analyze the genres and create a probability weight for frequency of appearance in this : " + genre+ " and list each probability from 0 to 1 with the category like [\\n  {\\n    \\\"category\\\": \\\"HEALTH\\\",\\n    \\\"probability\\\": 0.3\\n  }] for easy extraction in JSON format. Do not add any other text in the response. " +
                 "\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }


    public String sendingRequest(String requestBody) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiurl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while sending request: " + e.getMessage();
        }
    }

    public String recieverequest(String response) throws JSONException {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray choices = jsonResponse.getJSONArray("choices");
        JSONObject message=choices.getJSONObject(0).getJSONObject("message");
//        JSONObject firstChoice = choices.getJSONObject(0);
//        JSONObject messageContent = firstChoice.getJSONObject("message");

        // Extracting the category from the 'content' field
        String content = message.getString("content");
        return content;
    }

    public static Map<String, Double> extractedProbabilities(String apiResponse) {
        Map<String, Double> categoriesWithProbabilities = new HashMap<>();

        try {
            // Parse the API response
            JSONObject responseJson = new JSONObject(apiResponse);
            JSONArray choicesArray = responseJson.getJSONArray("choices");

            // Iterate over each choice to extract category and probability
            for (int i = 0; i < choicesArray.length(); i++) {
                JSONObject choice = choicesArray.getJSONObject(i);
                String content = choice.getJSONObject("message").getString("content");

                // Parse the content as JSON
                JSONArray contentArray = new JSONArray(content);
                for (int j = 0; j < contentArray.length(); j++) {
                    JSONObject categoryObject = contentArray.getJSONObject(j);
                    String category = categoryObject.getString("category");
                    double probability = categoryObject.getDouble("probability");

                    // Add category and probability to the map
                    categoriesWithProbabilities.put(category, probability);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoriesWithProbabilities;
    }

    }


