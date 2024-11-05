package org.example.news_recommendation;

    public class member {
        private String firstName;
        private String lastName;
        private int age;
        private static String preference;

        // Constructor
        public member(String firstName, String lastName, int age, String preference) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.preference = preference;
        }

        // Getters
        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public int getAge() {
            return age;
        }

        public static String getPreference() {
            return preference;
        }
}
