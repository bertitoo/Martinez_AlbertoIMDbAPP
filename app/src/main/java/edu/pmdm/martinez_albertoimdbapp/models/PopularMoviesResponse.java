package edu.pmdm.martinez_albertoimdbapp.models;

import java.util.List;

public class PopularMoviesResponse {
    private List<Movie> titles;

    public List<Movie> getTitles() {
        return titles;
    }

    public void setTitles(List<Movie> titles) {
        this.titles = titles;
    }
}
