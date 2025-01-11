package edu.pmdm.martinez_albertoimdbapp.ui.home;

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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.pmdm.martinez_albertoimdbapp.MovieDetailsActivity;
import edu.pmdm.martinez_albertoimdbapp.R;
import edu.pmdm.martinez_albertoimdbapp.api.IMDBApiService;
import edu.pmdm.martinez_albertoimdbapp.database.FavoritesManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

    private GridLayout gridLayout;
    private IMDBApiService imdbApiService;
    private Map<String, Bitmap> imageCache = new HashMap<>(); // Caché de imágenes
    private Map<String, String> titleCache = new HashMap<>(); // Caché de títulos
    private FavoritesManager favoritesManager;
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        gridLayout = root.findViewById(R.id.gridLayout);
        imdbApiService = new IMDBApiService();
        favoritesManager = new FavoritesManager(requireContext());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = (user != null) ? user.getUid() : null;

        loadTopMeterImages();
        return root;
    }

    private void loadTopMeterImages() {
        new Thread(() -> {
            try {
                String response = imdbApiService.getTopMeterTitles();
                List<String> tconstList = parseTconsts(response);

                for (String tconst : tconstList) {
                    String detailsResponse = imdbApiService.getTitleDetails(tconst);
                    String imageUrl = parseImageUrl(detailsResponse);
                    String movieTitle = parseMovieTitle(detailsResponse);

                    if (imageUrl != null && movieTitle != null) {
                        titleCache.put(tconst, movieTitle); // Guardar el título en caché
                        requireActivity().runOnUiThread(() -> addImageToGrid(imageUrl, tconst, movieTitle));
                    }
                }
            } catch (Exception e) {
                Log.e("IMDB_ERROR", "Error al cargar imágenes", e);
            }
        }).start();
    }

    private List<String> parseTconsts(String response) {
        List<String> tconsts = new ArrayList<>();
        String regex = "tt\\d{7,8}";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(response);
        while (matcher.find() && tconsts.size() < 10) {
            tconsts.add(matcher.group());
        }
        return tconsts;
    }

    private String parseImageUrl(String response) {
        String regex = "https://.*?\\.jpg";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(response);
        return matcher.find() ? matcher.group() : null;
    }

    private String parseMovieTitle(String response) {
        String regex = "\"titleText\":\\{\"text\":\"(.*?)\"";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(response);
        return matcher.find() ? matcher.group(1) : null;
    }

    private void addImageToGrid(String imageUrl, String tconst, String movieTitle) {
        ImageView imageView = new ImageView(getContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 500;
        params.height = 750;
        params.setMargins(16, 16, 16, 16);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (imageCache.containsKey(imageUrl)) {
            imageView.setImageBitmap(imageCache.get(imageUrl));
        } else {
            new Thread(() -> {
                Bitmap bitmap = getBitmapFromURL(imageUrl);
                if (bitmap != null) {
                    imageCache.put(imageUrl, bitmap);
                    requireActivity().runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                }
            }).start();
        }

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
            intent.putExtra("IMDB_ID", tconst);
            Toast.makeText(getContext(), "Mostrando detalles de la película: " + movieTitle, Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });

        imageView.setOnLongClickListener(v -> {
            if (userId == null) {
                Toast.makeText(getContext(), "Por favor, inicia sesión para añadir películas a favoritos.", Toast.LENGTH_LONG).show();
                return true;
            }

            boolean isFavorite = favoritesManager.isFavorite(tconst, userId);
            if (isFavorite) {
                Toast.makeText(getContext(), "La película '" + movieTitle + "' ya está en tus favoritos.", Toast.LENGTH_LONG).show();
            } else {
                favoritesManager.addFavorite(tconst, movieTitle, imageUrl, userId);
                Toast.makeText(getContext(), "Película añadida a favoritos: " + movieTitle, Toast.LENGTH_LONG).show();
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