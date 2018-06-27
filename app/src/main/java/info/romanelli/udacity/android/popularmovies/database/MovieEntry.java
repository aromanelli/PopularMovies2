package info.romanelli.udacity.android.popularmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import static info.romanelli.udacity.android.popularmovies.database.MovieEntry.TABLE_NAME_FAV_MOVIES;

@Entity(tableName = TABLE_NAME_FAV_MOVIES)
public class MovieEntry {

    final static String TABLE_NAME_FAV_MOVIES = "fav_movies";

    @SuppressWarnings("DefaultAnnotationParam")
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false) // We supply value, same as MovieInfo.id
    private int id;

    @ColumnInfo(name = "json")
    private String json;

    public MovieEntry(int id, String json) {
        this.id = id;
        this.json = json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Ignore
    @Override
    public String toString() {
        return "MovieEntry{" +
                "id=" + id +
                ", json=" + json +
                '}';
    }
}
