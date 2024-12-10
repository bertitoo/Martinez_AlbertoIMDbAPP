package edu.pmdm.martinez_albertoimdbapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import edu.pmdm.martinez_albertoimdbapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });

        // Acceder al DrawerLayout y al NavigationView
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Inflar el encabezado del NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Referencias a los componentes del encabezado
        TextView userName = headerView.findViewById(R.id.textView);
        TextView userEmail = headerView.findViewById(R.id.user_email);
        ImageView userProfilePic = headerView.findViewById(R.id.imageView);
        Button logoutButton = headerView.findViewById(R.id.logout_button); // Obtener el botón de logout

        // Inicializar Firebase Auth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Actualizar el nombre, correo y foto del perfil en el encabezado
            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
            Picasso.get().load(user.getPhotoUrl()).into(userProfilePic);  // Usamos Picasso para cargar la foto de perfil
        }

        // Configuración del NavigationView con el DrawerLayout
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Configurar el botón de logout
        logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {
        // Cerrar sesión en Firebase
        FirebaseAuth.getInstance().signOut();

        // Volver a la pantalla de inicio de sesión (WelcomeScreen)
        Intent intent = new Intent(MainActivity.this, WelcomeScreen.class);
        startActivity(intent);
        finish(); // Cerrar MainActivity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}