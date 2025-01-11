package edu.pmdm.martinez_albertoimdbapp.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TMDBApiService {

    private static final String API_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String BEARER_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmOTEyOGUzMDUyMmVkOWQ3MDcyMjExYzkxNjY1NTU5MiIsIm5iZiI6MTczNjYyNDA4OS4wNDgsInN1YiI6IjY3ODJjN2Q5YmQ3OTNjMDM1NDRlNzZlNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.krzhLkfIyxWlbV7tlyUSBzCncsl5zbGA8kSFBGZx1Co";

    // Método para obtener los géneros de películas
    public String getGenres() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_BASE_URL + "genre/movie/list?language=en")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", BEARER_TOKEN)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Error en la respuesta: " + response.code());
        }
    }

    // Método para buscar películas por género y año
    public String searchMovies(String query, int page) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String url = API_BASE_URL + "search/movie?include_adult=false&language=en-US&page=" + page + "&query=" + query;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", BEARER_TOKEN)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Error en la respuesta: " + response.code());
        }
    }
}