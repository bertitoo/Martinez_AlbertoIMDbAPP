package edu.pmdm.martinez_albertoimdbapp.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class IMDBApiService {

    private static final String API_KEY = "5a7ed5cfc0msh5ae8bfa861de4a8p1f3264jsn3d57cf64434a";
    private static final String HOST = "imdb-com.p.rapidapi.com";

    // Método para obtener los títulos más populares (top meter)
    public String getTopMeterTitles() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://imdb-com.p.rapidapi.com/title/get-top-meter?topMeterTitlesType=ALL")
                .get()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", HOST)
                .build();

        Response response = client.newCall(request).execute();
        try {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Error en la respuesta: " + response.code());
            }
        } finally {
            // Cerrar el cuerpo de la respuesta
            if (response.body() != null) {
                response.body().close();
            }
        }
    }

    // Método para obtener los detalles de una película usando el tconst
    public String getTitleDetails(String tconst) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://imdb-com.p.rapidapi.com/title/get-overview?tconst=" + tconst)
                .get()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", HOST)
                .build();

        Response response = client.newCall(request).execute();
        try {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Error en la respuesta: " + response.code());
            }
        } finally {
            // Cerrar el cuerpo de la respuesta
            if (response.body() != null) {
                response.body().close();
            }
        }
    }
}