package edu.pmdm.martinez_albertoimdbapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.pmdm.martinez_albertoimdbapp.database.FavoritesManager;
import edu.pmdm.martinez_albertoimdbapp.models.TMDBMovie;

public class FilteredMoviesActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private FavoritesManager favoritesManager;
    private String currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_movies);

        gridLayout = findViewById(R.id.gridLayout);
        favoritesManager = new FavoritesManager(this);

        // Obtener el ID del usuario autenticado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = (user != null) ? user.getUid() : null;

        // Obtener la lista de películas desde el Intent
        String moviesJson = getIntent().getStringExtra("MOVIES_LIST");
        if (moviesJson != null) {
            Type listType = new TypeToken<List<TMDBMovie>>() {}.getType();
            List<TMDBMovie> movies = new Gson().fromJson(moviesJson, listType);
            displayResults(movies);
        } else {
            Toast.makeText(this, "No se encontraron películas para mostrar.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayResults(List<TMDBMovie> movies) {
        gridLayout.removeAllViews();

        if (movies.isEmpty()) {
            Toast.makeText(this, "No se encontraron resultados.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (TMDBMovie movie : movies) {
            addImageToGrid(movie);
        }
    }

    private void addImageToGrid(TMDBMovie movie) {
        ImageView imageView = new ImageView(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 500;
        params.height = 750;
        params.setMargins(16, 16, 16, 16);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        new Thread(() -> {
            try {
                Bitmap bitmap = getBitmapFromURL(movie.getPosterPath());
                if (bitmap != null) {
                    runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                }
            } catch (Exception e) {
                Log.e("IMAGE_ERROR", "Error al cargar imagen", e);
            }
        }).start();

        // Acción al hacer clic: Mostrar detalles de la película
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra("IMDB_ID", movie.getImdbId()); // Corrección: asegurarte que 'movie.getImdbId()' sea válido
            Toast.makeText(this, "Mostrando detalles de: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });

        // Acción al mantener presionado: Añadir a favoritos
        imageView.setOnLongClickListener(v -> {
            if (currentUserId == null) {
                Toast.makeText(this, "Por favor, inicia sesión para gestionar favoritos.", Toast.LENGTH_LONG).show();
                return true;
            }

            boolean isFavorite = favoritesManager.isFavorite(movie.getImdbId(), currentUserId);
            if (isFavorite) {
                Toast.makeText(this, "La película '" + movie.getTitle() + "' ya está en tus favoritos.", Toast.LENGTH_LONG).show();
            } else {
                favoritesManager.addFavorite(movie.getImdbId(), movie.getTitle(), movie.getPosterPath(), currentUserId);
                Toast.makeText(this, "Película añadida a favoritos: " + movie.getTitle(), Toast.LENGTH_LONG).show();
            }
            return true;
        });

        gridLayout.addView(imageView);
    }

    private Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            Log.e("IMAGE_ERROR", "Error al descargar la imagen", e);
            return null;
        }
    }
}