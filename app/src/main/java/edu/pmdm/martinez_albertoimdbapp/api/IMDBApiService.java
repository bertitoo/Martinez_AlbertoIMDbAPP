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
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Error en la respuesta: " + response.code());
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
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Error en la respuesta: " + response.code());
        }
    }

    // Método para obtener la URL de la imagen (por ejemplo, del response de getTitleDetails)
    public String parseImageUrl(String response) {
        // Aquí deberías parsear la respuesta y extraer la URL de la imagen
        // Este es un ejemplo de cómo se puede hacer con regex para extraer una URL
        String regex = "https://.*?\\.jpg";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(response);
        return matcher.find() ? matcher.group() : null;
    }
}