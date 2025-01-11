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
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

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

        // Inicializar los elementos de la interfaz
        titleTextView = findViewById(R.id.titleTextView);
        plotTextView = findViewById(R.id.plotTextView);
        releaseDateTextView = findViewById(R.id.releaseDateTextView);
        ratingTextView = findViewById(R.id.ratingTextView);
        posterImageView = findViewById(R.id.posterImageView);
        shareButton = findViewById(R.id.smsButton);

        // Obtener el IMDb ID desde el Intent
        Intent intent = getIntent();
        imdbId = intent.getStringExtra("IMDB_ID");

        // Obtener los detalles de la película
        fetchMovieDetails(imdbId);

        // Configurar el botón para compartir
        shareButton.setOnClickListener(v -> shareMovieDetails());
    }

    private void fetchMovieDetails(String imdbId) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://imdb-com.p.rapidapi.com/title/get-overview?tconst=" + imdbId;

        // Crear la solicitud
        Request request = new Request.Builder()
                .url(url)
                .addHeader("x-rapidapi-key", "5a7ed5cfc0msh5ae8bfa861de4a8p1f3264jsn3d57cf64434a")
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

                    // Log para depuración
                    Log.d("IMDB_JSON_RESPONSE", "Respuesta JSON: " + responseBody);

                    // Procesar el JSON directamente
                    runOnUiThread(() -> parseAndUpdateUI(responseBody));
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MovieDetailsActivity.this, "Error al obtener los detalles de la película.", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void parseAndUpdateUI(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // Inicializar valores predeterminados
            String description = "Descripción no disponible.";
            String title = "Título no disponible.";
            int releaseYear = 0;
            double rating = 0.0;
            String posterUrl = "";

            // Acceder al objeto "data"
            if (jsonObject.has("data")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");

                // Título y otros detalles
                if (dataObject.has("title")) {
                    JSONObject titleObject = dataObject.getJSONObject("title");
                    title = titleObject.getJSONObject("titleText").getString("text");
                    releaseYear = titleObject.getJSONObject("releaseYear").getInt("year");
                    rating = titleObject.getJSONObject("ratingsSummary").getDouble("aggregateRating");
                    posterUrl = titleObject.getJSONObject("primaryImage").getString("url");
                }

                // Descripción
                if (dataObject.has("description")) {
                    JSONObject descriptionObject = dataObject.getJSONObject("description");
                    if (descriptionObject.has("value")) {
                        JSONObject valueObject = descriptionObject.getJSONObject("value");
                        if (valueObject.has("plainText")) {
                            description = valueObject.getString("plainText");
                        }
                    }
                }
            }

            // Actualizar la interfaz
            titleTextView.setText(title);
            plotTextView.setText(description);
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

                    Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
                    smsIntent.putExtra("sms_body", messageText);
                    startActivity(Intent.createChooser(smsIntent, "Selecciona una aplicación de mensajería"));
                }
            }
        }
    }
}