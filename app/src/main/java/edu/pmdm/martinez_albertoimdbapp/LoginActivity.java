package edu.pmdm.martinez_albertoimdbapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1001;  // Código de solicitud

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Tu client ID de Google
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Configurar el botón de Sign-In
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(v -> signIn());

        // Cambiar el texto dentro del SignInButton
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View child = signInButton.getChildAt(i);
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                textView.setText("Sign in with Google"); // Establece tu texto personalizado
            }
        }
    }

    private void signIn() {
        // Lanzar el intento de inicio de sesión de Google
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado de la actividad de GoogleSignIn
        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // La autenticación fue exitosa, autenticar con Firebase
                            GoogleSignInAccount account = task.getResult();
                            firebaseAuthWithGoogle(account);
                        } else {
                            // Si hay un error, manejarlo aquí
                        }
                    });
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null))
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Si la autenticación es exitosa, navegar a la pantalla principal
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Cerrar LoginActivity
                    } else {
                        // Manejar el error de autenticación
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Cerrar sesión de Firebase
        FirebaseAuth.getInstance().signOut();

        // Cerrar sesión de Google
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                .addOnCompleteListener(this, task -> {
                    // Sesión cerrada con éxito
                });
    }
}