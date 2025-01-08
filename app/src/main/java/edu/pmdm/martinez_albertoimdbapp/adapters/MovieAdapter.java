package edu.pmdm.martinez_albertoimdbapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import edu.pmdm.martinez_albertoimdbapp.R;
import edu.pmdm.martinez_albertoimdbapp.models.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;

    // Constructor que solo recibe la lista de películas
    public MovieAdapter(List<Movie> movieList) {
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflar el layout de cada ítem (solo se necesita el ImageView para mostrar la portada)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        // Cargar la imagen del póster usando Picasso
        Picasso.get().load(movie.getPosterUrl()).into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // ViewHolder que contiene la referencia al ImageView para las portadas
    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView poster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            // Inicializar el ImageView donde se mostrará la portada
            poster = itemView.findViewById(R.id.movie_poster);
        }
    }
}