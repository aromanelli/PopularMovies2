package info.romanelli.udacity.android.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import info.romanelli.udacity.android.popularmovies.BuildConfig;

public class MovieInfoFetcher {

    final static private String TAG = MovieInfoFetcher.class.getSimpleName();

    final static private String API_KEY_PARAM_NAME = "api_key";
    // https://medium.com/code-better/hiding-api-keys-from-your-android-repository-b23f5598b906
    // https://stackoverflow.com/a/34021467/435519 (option #2)
    //    https://stackoverflow.com/questions/33134031/is-there-a-safe-way-to-manage-api-keys
    final static private String API_KEY_TMDB = BuildConfig.ApiKey_TheMovieDB;

    final static private String URL_TMDB_MOVIES_BASE = "https://api.themoviedb.org/3/movie";

    final static private String URL_TMDB_MOVIES_POPULAR = "popular";

    final static private String URL_TMDB_MOVIES_TOPRATED = "top_rated";

    final static private String URL_TMDB_IMAGES = "http://image.tmdb.org/t/p/";

    static public void fetchPopularMovies(final Context context) {
        Log.d(TAG, "fetchPopularMovies() called with: context = [" + context + "]");
        fetchMoviesData(context, URL_TMDB_MOVIES_POPULAR);
        Log.d(TAG, "fetchPopularMovies: fetchMoviesData(["+ context +"], ["+ URL_TMDB_MOVIES_POPULAR +"]) called.");
    }

    static public void fetchTopRatedMovies(final Context context) {
        Log.d(TAG, "fetchTopRatedMovies() called with: context = [" + context + "]");
        fetchMoviesData(context, URL_TMDB_MOVIES_TOPRATED);
        Log.d(TAG, "fetchTopRatedMovies: fetchMoviesData(["+ context +"], ["+ URL_TMDB_MOVIES_TOPRATED +"]) called.");
    }

    static private void fetchMoviesData(final Context context, final String urlMovieType) {
        Log.d(TAG, "fetchMoviesData() called with: context = [" + context + "], urlMovieType = [" + urlMovieType + "]");
        if (isOnline(context)) {

            AsyncTask<String, Integer, String> taskFetchMovies = new AsyncTask<String, Integer, String>() {
                @Override
                protected String doInBackground(String... movieType) {
                    Log.d(TAG, "doInBackground() called with: movieType = [" + movieType[0] + "]");
                    try {
                        publishProgress(0);
                        // TODO AOR Code fetching all pages of list, not just first one!
                        String response = getResponse( buildURL(movieType[0]) );
                        // TODO AOR Code processing of JSON data!
                        publishProgress(100);
                        return response;
                    } catch (MalformedURLException e) {
                        Log.e(TAG, "doInBackground: Error while building URL to fetch movies data!", e);
                    } catch (IOException e) {
                        Log.e(TAG, "doInBackground: Error while fetching movies data!", e);
                    }
                    return null;
                }
                @Override
                protected void onProgressUpdate(Integer... progress) {
                    Log.d(TAG, "onProgressUpdate() called with: progress = [" + progress[0] + "]");
                }
                @Override
                protected void onPostExecute(String results) {
                    Log.d(TAG, "onPostExecute() called with: results = [" + results + "]");
                }
            };

            Log.d(TAG, "fetchMoviesData(): Online; starting AsyncTask ...");
            taskFetchMovies.execute(urlMovieType);

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
        }
        else {
            Log.w(TAG, "isOnline: Unable to obtain a ConnectivityManager reference!");
            flag = false;
        }
        Log.d(TAG, "isOnline: Online? " + flag );
        return flag;
    }

    static private URL buildURL(String typeOfMovies) throws MalformedURLException {
        Log.d(TAG, "buildURL() called with: typeOfMovies = [" + typeOfMovies + "]");
        // Assemble and build the Uri ...
        Uri uri = Uri.parse(URL_TMDB_MOVIES_BASE)
                .buildUpon()
                .appendPath(typeOfMovies)
                .appendQueryParameter(API_KEY_PARAM_NAME, API_KEY_TMDB)
                .build();
        // Create the URL from the Uri ...
        return new URL(uri.toString());
    }

    // Below method/code was taken from Udacity Sunshine app ...
    static private String getResponse(URL url) throws IOException {
        if (url != null) {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                Scanner scanner = new Scanner(urlConnection.getInputStream());
                scanner.useDelimiter("\\A");
                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                }
            } finally {
                urlConnection.disconnect();
            }
        }
        return null;
    }

}
