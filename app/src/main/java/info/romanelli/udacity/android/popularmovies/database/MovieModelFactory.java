package info.romanelli.udacity.android.popularmovies.database;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import info.romanelli.udacity.android.popularmovies.util.AppDatabase;

public class MovieModelFactory extends ViewModelProvider.NewInstanceFactory {

    private AppDatabase appDb;
    private int id; // Not auto-generated; we supply same value as MOvieInfo.id!

    // We supply 'id' so that it is the same value as MovieInfo.id!
    public MovieModelFactory(AppDatabase appDb, int id) {
        this.appDb = appDb;
        this.id = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieModel(appDb, id);
    }

}
