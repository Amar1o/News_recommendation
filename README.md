News Recommendation App
Overview:

The News Recommendation App provides users with personalized news articles based on their preferences. The application allows users to log in, view their favorite genres, and receive tailored recommendations.

Key Features:
User authentication and preference management.
Personalized article recommendations.
A database-backed system for storing user details and preferences.
Intuitive UI built using JavaFX.

Dataset:
https://www.kaggle.com/datasets/rmisra/news-category-dataset/data

Programming Language: Java (with JavaFX for UI)
Database: MySQL
JSON Parsing: Gson library (JsonArray, JsonObject, JsonParser) for efficient JSON handling.
Development Tools: IntelliJ IDEA, Maven

Prerequisite to using the code:

To set up and use the application, execute the following SQL commands. No pre existing members are saved; Run the application-> register an account -> Login. 
Proceed to like and view articles to populate the tables.

Database Schema
To set up the required database tables, use the following SQL commands:

CREATE TABLE members (
    first_name VARCHAR(50) PRIMARY KEY,
    last_name VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL
);


CREATE TABLE preference (
    name VARCHAR(100) NOT NULL,
    headline TEXT NOT NULL,
    genre VARCHAR(50) NOT NULL,
    url TEXT  NOT NULL
);
