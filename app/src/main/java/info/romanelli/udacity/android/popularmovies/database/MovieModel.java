package info.romanelli.udacity.android.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import info.romanelli.udacity.android.popularmovies.util.AppDatabase;

public class MovieModel extends ViewModel {

    private static final String TAG = MovieModel.class.getSimpleName();

    private final LiveData<MovieEntry> movieEntry;

    MovieModel(AppDatabase appDb, int id) {
        Log.d(TAG, "MovieModel() called with: appDb = [" + appDb + "], id = [" + id + "]");
        this.movieEntry = appDb.movieDao().getMovieById(id);
        Log.d(TAG, "MovieModel: movieEntry: " + movieEntry);
        Log.d(TAG, "MovieModel: movieEntry.value: " + movieEntry.getValue());
    }

    public LiveData<MovieEntry> getMovieEntry() {
        return movieEntry;
    }

}
