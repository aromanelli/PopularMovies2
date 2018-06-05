package info.romanelli.udacity.android.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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

    static private boolean isOnline(final Context context) {
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

}
