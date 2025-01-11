package edu.pmdm.martinez_albertoimdbapp.models;

import java.io.Serializable;

public class TMDBMovie implements Serializable {
    private String id;          // TMDB ID
    private String title;       // Título de la película
    private String posterPath;  // URL del póster
    private String imdbId;      // ID de IMDB

    // Constructor
    public TMDBMovie(String id, String title, String posterPath) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
    }

    // Getter y Setter para el TMDB ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter y Setter para el título
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter y Setter para el póster
    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    // Getter y Setter para el IMDB ID
    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
}
