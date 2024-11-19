package org.example.news_recommendation;

    public class member {
        private String firstName;
        private String lastName;
        private int age;
        private static String preference;

        // Constructor
        public member(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;

        }

        // Getters
        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }


}
