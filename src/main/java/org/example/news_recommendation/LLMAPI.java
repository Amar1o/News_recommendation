package org.example.news_recommendation;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LLMAPI {
    private String apiKey= "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";
    private HttpClient client;
    private String apiurl= "https://api.groq.com/openai/v1/chat/completions";

    public LLMAPI() {
        this.client = HttpClient.newHttpClient(); // Initialize HttpClient
    }
    public String createRequestBody(String newsDescription) {
        return String.format("""
                {
                    "model": "llama3-8b-8192",
                    "messages": [
                        {
                            "role": "user",
                            "content": "Analyze this news article and determine its category: Business, Technology, Politics, Entertainment, Sports, Health, Science, or World News. Respond in JSON format with category and confidence only in one word."
                        }
                    ]
                }""", newsDescription.replace("\"", "\\\""));
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
//    public String receiveCategory(String responseBody) {
//        try {
//            JSONObject jsonResponse = new JSONObject(responseBody);  // Parse the response as JSON
//            // Assuming the category is in the "choices" array in the "message" object
//            String category = jsonResponse.getJSONArray("choices")
//                    .getJSONObject(0)
//                    .getJSONObject("message")
//                    .getString("content");
//            return category.trim();  // Return the category after trimming spaces
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error occurred while parsing response: " + e.getMessage();
//        }
//    }
    public static void main(String[] args) {
        // Create an instance of the LLMAPI class
        LLMAPI api = new LLMAPI();

        // Sample news article description for testing
        String sampleNewsDescription = "The recent advancements in AI and machine learning are revolutionizing industries, making processes more efficient and creating new opportunities for businesses.";

        // Create request body
        String requestBody = api.createRequestBody(sampleNewsDescription);
        System.out.println("Request Body: \n" + requestBody);

        // Send the request and get the response
        String response = api.sendingRequest(requestBody);
        System.out.println("API Response: \n" + response);

        // Attempt to extract and print the category from the response
        try {
            String category = api.recieverequest(response);
            System.out.println("Category: " + category);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}