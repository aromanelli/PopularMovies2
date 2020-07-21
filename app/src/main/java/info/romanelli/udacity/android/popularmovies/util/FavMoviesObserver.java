package info.romanelli.udacity.android.popularmovies.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import info.romanelli.udacity.android.popularmovies.database.MovieEntry;
import info.romanelli.udacity.android.popularmovies.network.MovieInfo;
import info.romanelli.udacity.android.popularmovies.network.MoviesInfoFetcher;

public class FavMoviesObserver implements Observer<List<MovieEntry>> {

    final static private String TAG = FavMoviesObserver.class.getSimpleName();

    final private MoviesInfoFetcher.Listener listener;
    private LiveData<List<MovieEntry>> ld;

    private MoviesInfoFetcher.MoviesInfoType moviesInfoType;

    public FavMoviesObserver( final MoviesInfoFetcher.Listener listener,
                              final MoviesInfoFetcher.MoviesInfoType moviesInfoType) {
        Log.d(TAG, "FavMoviesObserver() called with: listener = [" + listener + "], moviesInfoType = [" + moviesInfoType + "]");

        this.listener = listener;
        this.moviesInfoType = moviesInfoType;
    }

    @Override
    public void onChanged(@Nullable final List<MovieEntry> movieEntries) {
        Log.d(TAG, "onChanged() called with: movieEntries = [" + movieEntries + "]");

        // If user changes favorite from within favorites view ...
        Log.d(TAG, "onChanged: moviesInfoType: " + moviesInfoType);
        if (MoviesInfoFetcher.MoviesInfoType.FAVORITES.equals(moviesInfoType)) {

            // Convert received data into MovieInfo objects ...
            AppExecutors.$().diskIO().execute(() -> {
                // Deserialize the previously saved MovieInfo(MovieEntry) entries ...
                // (Serialization done in DetailActivity, off the favorites button.)
                final ArrayList<MovieInfo> list;
                if (movieEntries != null && (movieEntries.size() >= 1)) {
                    final Gson gson = new GsonBuilder().create();
                    list = new ArrayList<>(movieEntries.size());
                    for (MovieEntry me : movieEntries) {
                        list.add(gson.fromJson(me.getJson(), MovieInfo.class));
                    }
                } else {
                    // No favorites, so set the model for the view to an empty list ...
                    list = new ArrayList<>(0);
                }
                AppExecutors.$().mainThread().execute(() -> listener.fetchedMoviesInfo(list));
            });

        }
    }

    synchronized public void start(AppCompatActivity activity) throws IllegalStateException {
        if (ld != null) {
            throw new IllegalStateException("You must call stop() before calling start(Activity)!");
        }
        Log.d(TAG, "start() called");
        ld = AppDatabase.$(activity).movieDao().getAllMovies();
        ld.observe(activity,this);
    }

    synchronized public void stop() {
        Log.d(TAG, "stop() called");
        ld.removeObserver(this);
        ld = null;
    }

    public void setMoviesInfoType(final MoviesInfoFetcher.MoviesInfoType type) {
        Log.d(TAG, "setMoviesInfoType() called with: type = [" + type + "]");
        this.moviesInfoType = type;
    }

}
