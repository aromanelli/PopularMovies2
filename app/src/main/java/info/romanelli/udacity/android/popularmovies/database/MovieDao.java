package info.romanelli.udacity.android.popularmovies.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
