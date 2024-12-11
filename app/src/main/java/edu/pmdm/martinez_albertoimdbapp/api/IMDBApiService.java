package edu.pmdm.martinez_albertoimdbapp.api;

import edu.pmdm.martinez_albertoimdbapp.models.MovieResponse;
import edu.pmdm.martinez_albertoimdbapp.models.PopularMoviesResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface IMDBApiService {

    // URL base para Retrofit
    String BASE_URL = "https://imdb-com.p.rapidapi.com/";

    // Endpoint para obtener las películas o series más populares (top meter)
    @Headers({
            "x-rapidapi-host: imdb-com.p.rapidapi.com",
            "x-rapidapi-key: e5efe6197emsh45e6357203aa4a1p1c3cd9jsnaaad2991ccab"
    })
    @GET("title/get-top-meter")
    Call<PopularMoviesResponse> getTopMovies();

    // Endpoint para obtener detalles de una película o serie
    @Headers({
            "x-rapidapi-host: imdb-com.p.rapidapi.com",
            "x-rapidapi-key: e5efe6197emsh45e6357203aa4a1p1c3cd9jsnaaad2991ccab"
    })
    @GET("title/get-overview")
    Call<MovieResponse> getMovieDetails(@Query("imdb_id") String imdbId);

    // Método para obtener la instancia de Retrofit
    static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())  // Conversión de JSON a objetos Java
                .build();
    }
}
