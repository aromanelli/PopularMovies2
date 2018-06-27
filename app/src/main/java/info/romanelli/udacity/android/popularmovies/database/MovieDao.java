package info.romanelli.udacity.android.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie ORDER BY movie_id")
    LiveData<List<MovieEntry>> getAllMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMovie(MovieEntry taskEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieEntry taskEntry);

    @Delete
    void deleteMovie(MovieEntry taskEntry);

    @Query("SELECT * FROM movie WHERE id = :id")
    LiveData<MovieEntry> getMovieById(int id);

    @Query("SELECT * FROM movie WHERE movie_id = :movieId")
    LiveData<MovieEntry> getMovieByMovieId(int movieId);

}
