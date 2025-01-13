package edu.pmdm.martinez_albertoimdbapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import edu.pmdm.martinez_albertoimdbapp.models.MovieResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final int CONTACTS_PERMISSION_CODE = 124;

    private TextView titleTextView, plotTextView, releaseDateTextView, ratingTextView;
    private ImageView posterImageView;
    private Button shareButton;
    private String imdbId;
    private String phoneNumber;
    private String messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        titleTextView = findViewById(R.id.titleTextView);
        plotTextView = findViewById(R.id.plotTextView);
        releaseDateTextView = findViewById(R.id.releaseDateTextView);
        ratingTextView = findViewById(R.id.ratingTextView);
        posterImageView = findViewById(R.id.posterImageView);
        shareButton = findViewById(R.id.smsButton);

        Intent intent = getIntent();
        imdbId = intent.getStringExtra("IMDB_ID");

        Log.d("MOVIE_DETAILS", "ID recibido: " + imdbId); // Para verificar el ID

        if (imdbId != null) {
            fetchMovieDetails(imdbId);
        } else {
            Toast.makeText(this, "Error: No se recibió el ID de la película.", Toast.LENGTH_SHORT).show();
        }

        shareButton.setOnClickListener(v -> shareMovieDetails());
    }

    private void fetchMovieDetails(String imdbId) {
        if (!imdbId.startsWith("tt")) {
            Toast.makeText(this, "ID de película inválido: " + imdbId, Toast.LENGTH_SHORT).show();
            return;
        }

        // Primera solicitud para obtener detalles básicos
        OkHttpClient client = new OkHttpClient();
        String detailsUrl = "https://imdb-com.p.rapidapi.com/title/get-overview?tconst=" + imdbId;

        Request detailsRequest = new Request.Builder()
                .url(detailsUrl)
                .addHeader("x-rapidapi-key", "05e35edbeamsh592a5eac8032a4bp1465d3jsn99264748a40f")
                .addHeader("x-rapidapi-host", "imdb-com.p.rapidapi.com")
                .build();

        client.newCall(detailsRequest).enqueue(new Callback() {
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
                    Log.d("MOVIE_DETAILS", "Respuesta JSON: " + responseBody);
                    runOnUiThread(() -> parseAndUpdateUI(responseBody));

                    // Llamada para obtener la descripción (plot)
                    fetchMoviePlot(imdbId);
                } else {
                    Log.e("MOVIE_DETAILS", "Error HTTP: " + response.code() + " " + response.message());
                    runOnUiThread(() -> Toast.makeText(MovieDetailsActivity.this, "Error al obtener los detalles de la película.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // Nueva solicitud para obtener el plot (descripción)
    private void fetchMoviePlot(String imdbId) {
        OkHttpClient client = new OkHttpClient();
        String plotUrl = "https://imdb-com.p.rapidapi.com/title/get-plot?tconst=" + imdbId;

        Request plotRequest = new Request.Builder()
                .url(plotUrl)
                .addHeader("x-rapidapi-key", "05e35edbeamsh592a5eac8032a4bp1465d3jsn99264748a40f")
                .addHeader("x-rapidapi-host", "imdb-com.p.rapidapi.com")
                .build();

        client.newCall(plotRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MovieDetailsActivity.this, "Error al obtener la descripción: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("MOVIE_PLOT", "Respuesta JSON (plot): " + responseBody);
                    runOnUiThread(() -> parsePlot(responseBody));
                } else {
                    Log.e("MOVIE_PLOT", "Error HTTP: " + response.code() + " " + response.message());
                    runOnUiThread(() -> Toast.makeText(MovieDetailsActivity.this, "Error al obtener la descripción de la película.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    // Método para procesar y mostrar la descripción
    private void parsePlot(String plotJson) {
        try {
            JSONObject jsonObject = new JSONObject(plotJson);

            // Inicializar el plot con un valor predeterminado
            String plot = "Descripción no disponible.";

            // Verificar la estructura y extraer el texto del plot
            if (jsonObject.has("data")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                if (dataObject.has("title")) {
                    JSONObject titleObject = dataObject.getJSONObject("title");
                    if (titleObject.has("plot")) {
                        JSONObject plotObject = titleObject.getJSONObject("plot");
                        if (plotObject.has("plotText")) {
                            JSONObject plotTextObject = plotObject.getJSONObject("plotText");
                            if (plotTextObject.has("plainText")) {
                                plot = plotTextObject.getString("plainText");
                            }
                        }
                    }
                }
            }

            // Actualizar el TextView con la descripción del plot
            plotTextView.setText(plot);

        } catch (Exception e) {
            Log.e("PLOT_PARSE_ERROR", "Error al procesar el JSON del plot", e);
            Toast.makeText(this, "Error al mostrar la descripción.", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseAndUpdateUI(String jsonResponse) {
        try {
            Gson gson = new Gson();
            MovieResponse movieResponse = gson.fromJson(jsonResponse, MovieResponse.class);

            String title = "Título no disponible.";
            String plot = "Descripción no disponible.";
            int releaseYear = 0;
            double rating = 0.0;
            String posterUrl = "";

            if (movieResponse.getData() != null) {
                MovieResponse.Data data = movieResponse.getData();

                // Obtener detalles del título
                if (data.getTitle() != null) {
                    MovieResponse.Title titleObject = data.getTitle();
                    title = titleObject.getTitleText() != null ? titleObject.getTitleText().getText() : title;
                    releaseYear = titleObject.getReleaseYear() != null ? titleObject.getReleaseYear().getYear() : releaseYear;
                    rating = titleObject.getRatingsSummary() != null ? titleObject.getRatingsSummary().getAggregateRating() : rating;
                    posterUrl = titleObject.getPrimaryImage() != null ? titleObject.getPrimaryImage().getUrl() : posterUrl;

                    // Obtener el plot desde el objeto "plot"
                    if (titleObject.getPlot() != null && titleObject.getPlot().getPlotText() != null) {
                        plot = titleObject.getPlot().getPlotText().getPlainText();
                    }
                }
            }

            // Actualizar la interfaz
            titleTextView.setText(title);
            plotTextView.setText(plot);
            releaseDateTextView.setText("Año de lanzamiento: " + releaseYear);
            ratingTextView.setText("Calificación: " + rating);
            Picasso.get().load(posterUrl).into(posterImageView);

        } catch (Exception e) {
            Log.e("PARSE_JSON_ERROR", "Error al procesar el JSON", e);
            Toast.makeText(this, "Error al mostrar los detalles de la película.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareMovieDetails() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION_CODE);
        } else {
            // Construir el mensaje con los detalles de la película
            String movieTitle = titleTextView.getText().toString();
            String movieRating = ratingTextView.getText().toString().replace("Calificación: ", ""); // Extraer solo el número del rating
            messageText = "Esta película te gustará: " + movieTitle + " Rating: " + movieRating;

            // Seleccionar el contacto
            seleccionarContacto();
        }
    }

    private void seleccionarContacto() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, CONTACTS_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CONTACTS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                seleccionarContacto();
            } else {
                Toast.makeText(this, "Permiso para acceder a contactos denegado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACTS_PERMISSION_CODE && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
            try (Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    // Enviar el mensaje al contacto seleccionado
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
                    smsIntent.putExtra("sms_body", messageText);
                    startActivity(Intent.createChooser(smsIntent, "Selecciona una aplicación de mensajería"));
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error al seleccionar el contacto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CONTACT_SELECTION", "Error al obtener el número de contacto", e);
            }
        }
    }
}