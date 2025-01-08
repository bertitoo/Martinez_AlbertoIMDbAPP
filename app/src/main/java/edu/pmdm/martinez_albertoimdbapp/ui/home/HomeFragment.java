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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import edu.pmdm.martinez_albertoimdbapp.MovieDetailsActivity;
import edu.pmdm.martinez_albertoimdbapp.R;
import edu.pmdm.martinez_albertoimdbapp.api.IMDBApiService;

public class HomeFragment extends Fragment {

    private GridLayout gridLayout;
    private IMDBApiService imdbApiService;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        gridLayout = root.findViewById(R.id.gridLayout);
        imdbApiService = new IMDBApiService();

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
                    if (imageUrl != null) {
                        requireActivity().runOnUiThread(() -> addImageToGrid(imageUrl, tconst));
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

    private void addImageToGrid(String imageUrl, String tconst) {
        ImageView imageView = new ImageView(getContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 500;
        params.height = 750;
        params.setMargins(16, 16, 16, 16);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        new Thread(() -> {
            Bitmap bitmap = getBitmapFromURL(imageUrl);
            if (bitmap != null) {
                requireActivity().runOnUiThread(() -> imageView.setImageBitmap(bitmap));
            }
        }).start();

        // Pasamos el imdbId y la URL de la imagen al hacer clic
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
            intent.putExtra("IMDB_ID", tconst); // Pasar IMDb ID
            intent.putExtra("IMAGE_URL", imageUrl); // Pasar la imagen
            startActivity(intent);
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