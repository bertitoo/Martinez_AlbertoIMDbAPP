package edu.pmdm.martinez_albertoimdbapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

        // Referencias a las vistas de la interfaz
        titleTextView = findViewById(R.id.titleTextView);
        plotTextView = findViewById(R.id.plotTextView);
        releaseDateTextView = findViewById(R.id.releaseDateTextView);
        ratingTextView = findViewById(R.id.ratingTextView);
        posterImageView = findViewById(R.id.posterImageView);
        shareButton = findViewById(R.id.smsButton);

        // Obtener el IMDb ID de la película del Intent
        Intent intent = getIntent();
        imdbId = intent.getStringExtra("IMDB_ID");

        // Llamar al método para obtener detalles de la película
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

                    // Parsear la respuesta al objeto MovieResponse
                    Gson gson = new Gson();
                    MovieResponse movieDetailsResponse = gson.fromJson(responseBody, MovieResponse.class);

                    // Obtener los datos anidados correctamente
                    String title = movieDetailsResponse.getData().getTitle().getTitleText().getText();
                    String releaseYear = movieDetailsResponse.getData().getTitle().getReleaseYear().getYear();
                    String rating = movieDetailsResponse.getData().getTitle().getRatingsSummary().getAggregateRating();
                    String description = fetchDescription(movieDetailsResponse);

                    // Generar el mensaje para compartir
                    messageText = "Esta película te gustará: " + title + "\nRating: " + rating;

                    // Actualizar la UI con los detalles de la película
                    runOnUiThread(() -> {
                        titleTextView.setText(title);
                        plotTextView.setText(description);
                        releaseDateTextView.setText("Release Year: " + releaseYear);
                        ratingTextView.setText("Rating: " + rating);

                        // Cargar la imagen del póster usando Picasso
                        String posterUrl = movieDetailsResponse.getData().getTitle().getPrimaryImage().getUrl();
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
            return movieDetails.getData().getDescription().getValue().getPlainText();
        }
        return "Descripción no disponible."; // Valor por defecto si no hay descripción
    }

    private void shareMovieDetails() {
        // Verificar si se tiene permiso para acceder a los contactos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // Si no se tiene permiso, solicitarlo
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_PERMISSION_CODE);
        } else {
            // Si ya tenemos el permiso para contactos, proceder a seleccionar el contacto
            selectContact();
        }
    }

    private void selectContact() {
        // Intent para seleccionar un contacto
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, CONTACTS_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CONTACTS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectContact();  // Continuar con la selección de contactos
            } else {
                Toast.makeText(this, "Permiso de contactos denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACTS_PERMISSION_CODE && resultCode == RESULT_OK) {
            // Obtener el número de teléfono del contacto seleccionado
            Uri contactUri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
            try (Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    // En el onActivityResult, abrir la aplicación de SMS con un selector de aplicaciones
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
                    smsIntent.putExtra("sms_body", messageText); // El mensaje que deseas enviar

                    // Comprobar si hay alguna aplicación que pueda manejar este Intent
                    startActivity(Intent.createChooser(smsIntent, "Selecciona una aplicación de mensajería"));
                }
            }
        }
    }
}