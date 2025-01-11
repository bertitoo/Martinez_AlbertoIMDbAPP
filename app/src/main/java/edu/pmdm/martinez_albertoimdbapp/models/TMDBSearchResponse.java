package edu.pmdm.martinez_albertoimdbapp.models;

import java.util.List;

public class TMDBSearchResponse {

    private List<Movie> results;

    public List<Movie> getResults() {
        return results;
    }

    public static class Movie {
        private String id; // ID de la película
        private String title; // Título de la película
        private String poster_path; // Ruta del póster de la película

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getPosterPath() {
            return poster_path;
        }
    }
}

