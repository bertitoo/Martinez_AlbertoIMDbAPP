package edu.pmdm.martinez_albertoimdbapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import edu.pmdm.martinez_albertoimdbapp.models.MovieResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private TextView titleTextView, plotTextView, releaseDateTextView, ratingTextView;
    private ImageView posterImageView;
    private String imdbId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Referencias a las vistas de la interfaz
        titleTextView = findViewById(R.id.titleTextView);
        plotTextView = findViewById(R.id.plotTextView);
        releaseDateTextView = findViewById(R.id.releaseDateTextView);
        ratingTextView = findViewById(R.id.ratingTextView);
        posterImageView = findViewById(R.id.posterImageView);

        // Obtener el IMDb ID de la película del Intent
        Intent intent = getIntent();
        imdbId = intent.getStringExtra("IMDB_ID");

        // Llamar al método para obtener detalles de la película
        fetchMovieDetails(imdbId);
    }

    private void fetchMovieDetails(String imdbId) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://imdb-com.p.rapidapi.com/title/get-overview?tconst=" + imdbId;

        // Crear la solicitud
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-rapidapi-key", "e5efe6197emsh45e6357203aa4a1p1c3cd9jsnaaad2991ccab")
                .addHeader("x-rapidapi-host", "imdb-com.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MovieDetailsActivity.this, "Error al obtener los detalles: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    // Verificar la respuesta completa para asegurarnos de que estamos recibiendo los datos correctos
                    Log.d("MOVIE_DETAILS_RESPONSE", responseBody);

                    // Parsear la respuesta al objeto MovieResponse
                    Gson gson = new Gson();
                    MovieResponse movieDetails = gson.fromJson(responseBody, MovieResponse.class);

                    // Obtener los datos anidados correctamente
                    String title = movieDetails.getData().getTitle().getTitleText().getText();
                    String releaseYear = movieDetails.getData().getTitle().getReleaseYear().getYear();
                    String rating = movieDetails.getData().getTitle().getRatingsSummary().getAggregateRating();

                    // Verificar si description no es null y contiene un valor
                    String description = fetchDescription(movieDetails);

                    // Actualizar la UI con los detalles de la película
                    runOnUiThread(() -> {
                        titleTextView.setText(title);
                        plotTextView.setText(description);
                        releaseDateTextView.setText("Release Year: " + releaseYear);
                        ratingTextView.setText("Rating: " + rating);

                        // Cargar la imagen del póster usando Picasso
                        String posterUrl = movieDetails.getData().getTitle().getPrimaryImage().getUrl();
                        Picasso.get().load(posterUrl).into(posterImageView);
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MovieDetailsActivity.this, "Error al obtener detalles de la película.", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private String fetchDescription(MovieResponse movieDetails) {
        // Asegurarse de que description y value no son null antes de acceder al texto
        if (movieDetails.getData().getDescription() != null
                && movieDetails.getData().getDescription().getValue() != null
                && movieDetails.getData().getDescription().getValue().getPlainText() != null) {
            String description = movieDetails.getData().getDescription().getValue().getPlainText();
            if (!description.trim().isEmpty()) {
                return description; // Si la descripción no está vacía
            }
        }
        return "Descripción no disponible."; // Valor por defecto si no hay descripción
    }
}