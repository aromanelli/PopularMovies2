package info.romanelli.udacity.android.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import info.romanelli.udacity.android.popularmovies.model.MovieInfo;

public class MovieInfoFetcher {

    final static private String TAG = MovieInfoFetcher.class.getSimpleName();

    static public void fetchMoviesData( final Context context,
                                         final MovieInfoFetcherTask.MovieInfoFetchedListener listener,
                                         final MovieInfoFetcherTask.MoviesListType moviesListType ) {
        Log.d(TAG, "fetchMoviesData() called with: context = [" + context +
                "], moviesListType = [" + moviesListType + "]");
        if (isOnline(context)) {
            Log.d(TAG, "fetchMoviesData(): Online; starting AsyncTask for list type ["+ moviesListType +"] ...");
            new MovieInfoFetcherTask().execute(listener, moviesListType);
        } else {
            Log.w(TAG, "fetchMoviesData: Not online, so cannot do fetching; aborting!");
        }
    }

    @SuppressWarnings("WeakerAccess")
    static public boolean isOnline(final Context context) {
        Log.d(TAG, "isOnline() called with: context = [" + context + "]");
        final boolean flag;
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            flag = (netInfo != null) && netInfo.isConnectedOrConnecting();
        } else {
            Log.w(TAG, "isOnline: Unable to obtain a ConnectivityManager reference!");
            flag = false;
        }
        Log.d(TAG, "isOnline: Online? " + flag);
        return flag;
    }

    static public void setPosterToView(MovieInfo mi, ImageView iv) {

//        // Determine good size for posters (should call from MainActivity) ...
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        Log.d(TAG, "onCreate: DisplayMetrics: " + metrics);

        final Uri uri = Uri.parse("http://image.tmdb.org/t/p/")
                .buildUpon()
                // "w92", "w154", "w185", "w342", "w500", "w780", or "original"
                .appendEncodedPath("w342") // NOT appendPath!
                .appendEncodedPath(mi.getPosterURL()) // NOT appendPath!
                .build();
        Log.d(TAG, "onBindViewHolder: Uri for poster: ["+ uri +"]");
        Picasso.get().load(uri).into(iv);
    }

}
