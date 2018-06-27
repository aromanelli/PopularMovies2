package info.romanelli.udacity.android.popularmovies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "movie")
public class MovieEntry {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "movie_id")
    private int movieId;

    @ColumnInfo(name = "favorite")
    private boolean favorite = false;

    @Ignore
    public MovieEntry(int movieId, boolean favorite) {
        // this.id = Integer.MIN_VALUE; BAD! Breaks Room; leave unassigned!
        this.movieId = movieId;
        this.favorite = favorite;
    }

    MovieEntry(int id, int movieId, boolean favorite) {
        this.id = id;
        this.movieId = movieId;
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Ignore
    @Override
    public String toString() {
        return "MovieEntry{" +
                "id=" + id +
                ", movieId=" + movieId +
                ", favorite=" + favorite +
                '}';
    }
}
