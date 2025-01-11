package edu.pmdm.martinez_albertoimdbapp.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.martinez_albertoimdbapp.models.Movie;

public class FavoritesManager {

    private FavoritesDatabaseHelper dbHelper;

    public FavoritesManager(Context context) {
        dbHelper = new FavoritesDatabaseHelper(context);
    }

    // Verifica si una película ya está en los favoritos de un usuario
    public boolean isFavorite(String id, String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(FavoritesDatabaseHelper.TABLE_FAVORITES,
                null,
                FavoritesDatabaseHelper.COLUMN_ID + "=? AND " + FavoritesDatabaseHelper.COLUMN_USER_ID + "=?",
                new String[]{id, userId},
                null,
                null,
                null);

        boolean isFavorite = cursor.moveToFirst();
        cursor.close();
        db.close();

        return isFavorite;
    }

    // Agrega una película a la lista de favoritos de un usuario
    public void addFavorite(String id, String title, String imageUrl, String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FavoritesDatabaseHelper.COLUMN_ID, id);
        values.put(FavoritesDatabaseHelper.COLUMN_TITLE, title);
        values.put(FavoritesDatabaseHelper.COLUMN_IMAGE_URL, imageUrl);
        values.put(FavoritesDatabaseHelper.COLUMN_USER_ID, userId);

        db.insert(FavoritesDatabaseHelper.TABLE_FAVORITES, null, values);
        db.close();
    }


    public void removeFavorite(String id, String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(FavoritesDatabaseHelper.TABLE_FAVORITES,
                FavoritesDatabaseHelper.COLUMN_ID + "=? AND " + FavoritesDatabaseHelper.COLUMN_USER_ID + "=?",
                new String[]{id, userId});
        db.close();
    }

    /**
     * Carga las películas favoritas de un usuario desde la base de datos.
     *
     * @param userId El ID del usuario autenticado.
     * @return Una lista de objetos Movie que representan las películas favoritas.
     */
    public List<Movie> getFavoritesForUser(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Movie> movies = new ArrayList<>();

        Cursor cursor = db.query(FavoritesDatabaseHelper.TABLE_FAVORITES,
                null,
                FavoritesDatabaseHelper.COLUMN_USER_ID + "=?",
                new String[]{userId},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String imdbId = cursor.getString(cursor.getColumnIndex(FavoritesDatabaseHelper.COLUMN_ID));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(FavoritesDatabaseHelper.COLUMN_TITLE));
                @SuppressLint("Range") String posterUrl = cursor.getString(cursor.getColumnIndex(FavoritesDatabaseHelper.COLUMN_IMAGE_URL));

                // Crear un objeto Movie y agregarlo a la lista
                Movie movie = new Movie(imdbId, posterUrl);
                movie.setTitle(title);
                movies.add(movie);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return movies;
    }

}