package org.example.news_recommendation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class Database {
    private static List<String> genres = new ArrayList<>();
    private static List<String> headline = new ArrayList<>();
    public JsonArray checkifliked(JsonArray articles) {
        CompletableFuture<JsonArray> future = CompletableFuture.supplyAsync(() -> {
            String url = "jdbc:mysql://localhost:3306/news";
            String username = "root";
            String password = "";
            String query = "SELECT headline FROM preference WHERE name = ? AND headline = ?";
            String name = User.getInstance().getFirstName();
            JsonArray filteredArticles = new JsonArray();

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                for (int i = 0; i < articles.size(); i++) {
                    JsonObject article = articles.get(i).getAsJsonObject();
                    String headline = article.get("headline").getAsString();

                    statement.setString(1, name);
                    statement.setString(2, headline);

                    try (ResultSet rs = statement.executeQuery()) {
                        if (!rs.next()) {
                            filteredArticles.add(article);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return filteredArticles;
        });

        try {
            return future.get(); // Wait for the result
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonArray();
        }
    }
    public int AddtoDB(String name, String headline, String genre, String URL) throws ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/news";
        String username = "root";
        String password = "";

        // SQL query to check if the article already exists
        String checkSQL = "SELECT COUNT(*) FROM preference WHERE name = ? AND headline = ?";

        // SQL query to insert a new article if not already in the database
        String insertSQL = "INSERT INTO preference (name, headline, genre, URL) VALUES (?, ?, ?, ?)";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure MySQL driver is loaded

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement checkStmt = connection.prepareStatement(checkSQL)) {

                // Set parameters for the check query
                checkStmt.setString(1, name);
                checkStmt.setString(2, headline);

                // Execute the check query
                try (ResultSet resultSet = checkStmt.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        if (count > 0) {
                            // If an entry already exists with the same name and headline
                            System.out.println("Article already exists in the database.");
                            return 1; // Indicating that the article already exists
                        }
                    }
                }

                // If no existing article was found, proceed to insert the new article
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSQL)) {
                    // Set parameters for the insert
                    insertStmt.setString(1, name);
                    insertStmt.setString(2, headline);
                    insertStmt.setString(3, genre);
                    insertStmt.setString(4, URL);

                    // Execute the insert statement
                    int rowsAffected = insertStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Preference added successfully! " + rowsAffected + " row(s) affected.");
                        return 0; // Success
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL driver not included in path.");
            e.printStackTrace();
            return -1;
        } catch (SQLException e) {
            System.out.println("Database connection error or query issue.");
            e.printStackTrace();
            return -1;
        }

        return -1; // Default case (unexpected error)
    }


    public static List<String> getDBdata(String name) {
        String url = "jdbc:mysql://localhost:3306/news";
        String username = "root";
        String password = "";

        // Query to retrieve all genres for the given name
        String query = "SELECT genre FROM preference WHERE name = ?";


        try {
            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                // Set the parameter for the name in the query
                statement.setString(1, name);

                // Execute the query and process the result
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        // Add each genre to the list
                        genres.add(resultSet.getString("genre"));
                    }
                }
            }

            if (genres.isEmpty()) {
                genres.add("No data found for name: " + name);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving data: " + e.getMessage());
            e.printStackTrace();
            genres.add("Error retrieving data.");
        }

        return genres;
    }
    public List<String> Getlikedarticles(String name) throws ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/news";
        String username = "root";
        String password = "";

        // Query to retrieve all headlines for the given name
        String query = "SELECT headline FROM preference WHERE name = ?";


        try {
            // Load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                // Set the parameter for the name in the query
                statement.setString(1, name);

                // Execute the query and process the result
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        // Add each headline to the list
                        headline.add(resultSet.getString("headline"));
                    }
                }
            }

            if (headline.isEmpty()) {
                headline.add("No data found for name: " + name);
            }

        } catch (Exception e) {
            System.err.println("Error retrieving data: " + e.getMessage());
            e.printStackTrace();
            headline.add("Error retrieving data.");
        }

        return headline;
    }
}
