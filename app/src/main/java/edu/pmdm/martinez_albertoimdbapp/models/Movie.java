package edu.pmdm.martinez_albertoimdbapp.models;

public class Movie {
    private String title;         // Título de la película
    private String imdbId;        // ID de IMDb
    private String posterUrl;     // URL de la imagen del póster

    // Constructor para inicializar la clase con los datos de la película
    public Movie(String imdbId, String posterUrl) {
        this.imdbId = imdbId;
        this.posterUrl = posterUrl;
    }

    // Getter para el título (aunque no lo estamos usando en este caso, podrías agregarlo si lo necesitas)
    public String getTitle() {
        return title;
    }

    // Setter para el título
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter para el IMDb ID
    public String getImdbId() {
        return imdbId;
    }

    // Setter para el IMDb ID
    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    // Getter para la URL del póster
    public String getPosterUrl() {
        return posterUrl;
    }

    // Setter para la URL del póster
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}