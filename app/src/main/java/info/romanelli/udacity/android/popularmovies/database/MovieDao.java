package info.romanelli.udacity.android.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static info.romanelli.udacity.android.popularmovies.database.MovieEntry.TABLE_NAME_FAV_MOVIES;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM "+ TABLE_NAME_FAV_MOVIES +" ORDER BY id")
    LiveData<List<MovieEntry>> getAllMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMovie(MovieEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieEntry taskEntry);

    @Delete
    void deleteMovie(MovieEntry taskEntry);

    @Query("SELECT * FROM "+ TABLE_NAME_FAV_MOVIES +" WHERE id = :id")
    LiveData<MovieEntry> getMovieById(int id);

}
