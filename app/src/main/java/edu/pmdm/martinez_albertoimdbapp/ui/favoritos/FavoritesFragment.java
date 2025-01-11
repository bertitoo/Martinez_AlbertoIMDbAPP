package edu.pmdm.martinez_albertoimdbapp.ui.favoritos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.pmdm.martinez_albertoimdbapp.MovieDetailsActivity;
import edu.pmdm.martinez_albertoimdbapp.R;
import edu.pmdm.martinez_albertoimdbapp.database.FavoritesManager;
import edu.pmdm.martinez_albertoimdbapp.models.Movie;

public class FavoritesFragment extends Fragment {

    private GridLayout gridLayout;
    private FavoritesManager favoritesManager;
    private String currentUserId;
    private Map<String, Bitmap> imageCache = new HashMap<>(); // Caché de imágenes

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favoritos, container, false);
        gridLayout = root.findViewById(R.id.gridLayout);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        } else {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return root;
        }

        favoritesManager = new FavoritesManager(getContext());
        loadFavorites();

        return root;
    }

    private void loadFavorites() {
        List<Movie> favorites = favoritesManager.getFavoritesForUser(currentUserId);
        gridLayout.removeAllViews();
        if (favorites.isEmpty()) {
            Toast.makeText(getContext(), "No tienes películas favoritas.", Toast.LENGTH_SHORT).show();
        } else {
            for (Movie movie : favorites) {
                addImageToGrid(movie);
            }
        }
    }

    private void addImageToGrid(Movie movie) {
        ImageView imageView = new ImageView(getContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 500;
        params.height = 750;
        params.setMargins(16, 16, 16, 16);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (imageCache.containsKey(movie.getPosterUrl())) {
            imageView.setImageBitmap(imageCache.get(movie.getPosterUrl()));
        } else {
            new Thread(() -> {
                Bitmap bitmap = getBitmapFromURL(movie.getPosterUrl());
                if (bitmap != null) {
                    imageCache.put(movie.getPosterUrl(), bitmap);
                    requireActivity().runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                }
            }).start();
        }

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
            intent.putExtra("IMDB_ID", movie.getImdbId());
            Toast.makeText(getContext(), "Mostrando detalles de la película: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });

        imageView.setOnLongClickListener(v -> {
            favoritesManager.removeFavorite(movie.getImdbId(), currentUserId);
            Toast.makeText(getContext(), "Película eliminada de favoritos: " + movie.getTitle(), Toast.LENGTH_LONG).show();
            loadFavorites();
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