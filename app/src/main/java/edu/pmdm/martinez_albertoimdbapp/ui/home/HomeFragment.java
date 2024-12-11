package edu.pmdm.martinez_albertoimdbapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.pmdm.martinez_albertoimdbapp.R;
import edu.pmdm.martinez_albertoimdbapp.adapters.MovieAdapter;
import edu.pmdm.martinez_albertoimdbapp.api.IMDBApiService;
import edu.pmdm.martinez_albertoimdbapp.models.MovieResponse;
import edu.pmdm.martinez_albertoimdbapp.models.PopularMoviesResponse;
import edu.pmdm.martinez_albertoimdbapp.MovieDetailsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Llamar a la API para obtener las películas populares
        IMDBApiService apiService = IMDBApiService.getRetrofitInstance().create(IMDBApiService.class);
        Call<PopularMoviesResponse> call = apiService.getTopMovies();
        call.enqueue(new Callback<PopularMoviesResponse>() {
            @Override
            public void onResponse(Call<PopularMoviesResponse> call, Response<PopularMoviesResponse> response) {
                if (response.isSuccessful()) {
                    PopularMoviesResponse popularMovies = response.body();
                    if (popularMovies != null && popularMovies.getTitles() != null) {
                        // Mostrar un Toast con el número de películas obtenidas
                        Toast.makeText(getContext(), "Películas obtenidas: " + popularMovies.getTitles().size(), Toast.LENGTH_SHORT).show();

                        movieAdapter = new MovieAdapter(popularMovies.getTitles(), movie -> {
                            // Al hacer clic en una película, obtenemos más detalles
                            getMovieDetails(movie.getImdbId());
                        });
                        recyclerView.setAdapter(movieAdapter);
                    } else {
                        Toast.makeText(getContext(), "No se recibieron películas.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Mostrar un Toast con el código de error si la respuesta no es exitosa
                    Toast.makeText(getContext(), "Error en la respuesta: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PopularMoviesResponse> call, Throwable t) {
                // Mostrar un Toast con el mensaje de error si la solicitud falla
                Toast.makeText(getContext(), "Error al obtener las películas: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void getMovieDetails(String imdbId) {
        IMDBApiService apiService = IMDBApiService.getRetrofitInstance().create(IMDBApiService.class);
        Call<MovieResponse> call = apiService.getMovieDetails(imdbId);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    MovieResponse movieDetails = response.body();
                    // Pasar los detalles a una nueva actividad
                    Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
                    intent.putExtra("title", movieDetails.getTitle());
                    intent.putExtra("plot", movieDetails.getPlot());
                    intent.putExtra("releaseDate", movieDetails.getReleaseDate());
                    intent.putExtra("rating", movieDetails.getImdbRating());
                    intent.putExtra("poster", movieDetails.getPoster());
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                // Manejar el error
            }
        });
    }
}
