package info.romanelli.udacity.android.popularmovies.database;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class MovieModelFactory extends ViewModelProvider.NewInstanceFactory {

    private AppDatabase appDb;
    private int movieId;

    public MovieModelFactory(AppDatabase appDb, int movieId) { // NOT by 'id' !
        this.appDb = appDb;
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieModel(appDb, movieId);
    }

}
