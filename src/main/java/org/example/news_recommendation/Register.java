package org.example.news_recommendation;

import java.sql.*;

public class Register {
    public static int register(String firstname, String lastName, int Age, String Preference) throws SQLException, ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306/truy";

        String selectSql = "SELECT * FROM members WHERE first_name = ? AND last_name = ?";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Ensure MySQL driver is loaded

            try (Connection connection = DriverManager.getConnection(url);
                 PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {

                if (connection != null) {
                    System.out.println("Connected to the database!");
                }
                selectStmt.setString(1, firstname);
                selectStmt.setString(2, lastName);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Name already exists!");
                        return 1;
                    } else {
                        System.out.println("Registering new user...");
                        String insrtSQL = "INSERT INTO members (first_name, last_name, age, preference) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(insrtSQL)) {
                            insertStmt.setString(1, firstname);
                            insertStmt.setString(2, lastName);
                            insertStmt.setInt(3, Age);
                            insertStmt.setString(4, Preference);

                            // Execute the insert statement
                            int rowsAffected = insertStmt.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Insert successful! " + rowsAffected + " row(s) affected.");
                            }
                        }
                    }
                }

            }
            return 0;

        } catch (ClassNotFoundException e) {
            System.out.println("mysql driver not included in path");
            e.printStackTrace();
            return -1;

        } catch (SQLException e) {
            System.out.println("Database connection error or query");
            e.printStackTrace();
            return -1;
        }
    }
}

