package info.romanelli.udacity.android.popularmovies.util;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import info.romanelli.udacity.android.popularmovies.database.MovieDao;
import info.romanelli.udacity.android.popularmovies.database.MovieEntry;

@Database(entities = {MovieEntry.class}, version = 1, exportSchema = false)
abstract public class AppDatabase extends RoomDatabase {

    final static private String TAG = AppDatabase.class.getSimpleName();

    final static private String DATABASE_NAME = "movies";

    static private AppDatabase REF;

    static public AppDatabase $(final Context context) {
        if (REF == null) {
            synchronized (TAG) {
                if (REF == null) {
                    Log.d(TAG, "$: Creating Room DB for Context [" + context + "]!");
                    REF = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class, AppDatabase.DATABASE_NAME
                    ).build();
                }
            }
        }
        return REF;
    }

    abstract public MovieDao movieDao();

}
