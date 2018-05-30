package info.romanelli.udacity.android.popularmovies.util;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import info.romanelli.udacity.android.popularmovies.BuildConfig;
import info.romanelli.udacity.android.popularmovies.model.MovieInfo;

/**
 * <p>An {@link AsyncTask} that will make JSON calls to TMDB and return a {@link List}
 * of {@link MovieInfo} objects.</p>
 *
 * <p>Use "{@code new MovieInfoFetcherTask().execute(MoviesListType)}" to run this task,
 * being sure to pass in what kind of list you want, {@link MoviesListType#POPULAR} or
 * '{@link MoviesListType#TOP_RATED}'.</p>
 */
public class MovieInfoFetcherTask
        extends AsyncTask<MovieInfoFetcherTask.MoviesListType, Integer, List<MovieInfo>> {

    final static private String TAG = MovieInfoFetcherTask.class.getSimpleName();

    final static private String URL_TMDB_MOVIES_BASE = "https://api.themoviedb.org/3/movie";

    final static private String API_KEY_PARAM_NAME = "api_key";
    // https://medium.com/code-better/hiding-api-keys-from-your-android-repository-b23f5598b906
    // https://stackoverflow.com/a/34021467/435519 (option #2)
    //    https://stackoverflow.com/questions/33134031/is-there-a-safe-way-to-manage-api-keys
    final static private String API_KEY_TMDB = BuildConfig.ApiKey_TheMovieDB;

    // final static private String URL_TMDB_IMAGES = "http://image.tmdb.org/t/p/";

    public enum MoviesListType {

        POPULAR ("popular"),
        TOP_RATED ("top_rated");

        final private String typeURLPartial;
        MoviesListType(String typeURLPartial) {
            this.typeURLPartial = typeURLPartial;
        }
        /** <p>Returns a partial {@link URL} for the movies list type.</p>
         * @return Either '{@code popular}' or '{@code top_rated}'. */
        public String getURLForType() {
            return typeURLPartial;
        }
    }

    @Override
    protected List<MovieInfo> doInBackground(MovieInfoFetcherTask.MoviesListType... moviesType) {

        if ((moviesType == null) || (moviesType.length == 0)) {
            Log.w(TAG, "doInBackground: No movies list type was specified; aborting!");
            return Collections.emptyList();
        }
        else {
            Log.d(TAG, "doInBackground() called with: movieType = [" + moviesType[0] + "]");
        }

        try {
            return parseJSON( getResponse( buildURL(moviesType[0]) ) );
        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: Error while building URL to fetch movies data!", e);
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: Error while fetching movies data!", e);
        }

        return null;
    }

    // Below method/code was taken from Udacity Sunshine app ...
    private String getResponse(URL url) throws IOException {
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

    private URL buildURL(MoviesListType moviesListType) throws MalformedURLException {
        Log.d(TAG, "buildURL() called with: moviesListType = [" + moviesListType + "]");
        if (moviesListType == null) {
            Log.w(TAG, "buildURL: Was called with a 'null' list type!");
            return null;
        }
        // Assemble and build the Uri ...
        Uri uri = Uri.parse(URL_TMDB_MOVIES_BASE)
                .buildUpon()
                .appendPath(moviesListType.getURLForType())
                .appendQueryParameter(API_KEY_PARAM_NAME, API_KEY_TMDB)
                .build();
        // Create the URL from the Uri ...
        return new URL(uri.toString());
    }

    // See project sandwich-club-starter-code, classes Jsonutils, strings.xml, and Sandwich
    // TODO AOR Code fetching all pages of list, not just first one!
    // TODO AOR Use page requesting specific URL, not generic link.
    private List<MovieInfo> parseJSON(final String json) {
        try {
            final JSONObject jsonObjMoviePage = new JSONObject(json);
            // final int page = jsonObjMoviePage.getInt("page");
            // final int totalResults = jsonObjMoviePage.getInt("total_results");
            // final int totalPages = jsonObjMoviePage.getInt("total_pages");

            final JSONArray jsonArrResults = jsonObjMoviePage.getJSONArray("results");

            final List<MovieInfo> listMovies = new ArrayList<>(); // totalResults);
            for (int i = 0; i < jsonArrResults.length(); i++) {
                JSONObject jsonObjMovieInfo = new JSONObject(jsonArrResults.getString(i));
                listMovies.add(
                    new MovieInfo(
                            jsonObjMovieInfo.getString("title"),
                            jsonObjMovieInfo.getString("poster_path"),
                            jsonObjMovieInfo.getString("overview"),
                            jsonObjMovieInfo.getString("vote_average"),
                            jsonObjMovieInfo.getString("release_date")
                    )
                );
            }
            return listMovies;
        } catch (JSONException e) {
            Log.e(TAG, "parseJson: " + e.getLocalizedMessage(), e);
            return Collections.emptyList();
        }

    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        Log.d(TAG, "onProgressUpdate() called with: progress = [" + progress[0] + "]");
    }

    @Override
    protected void onPostExecute(List<MovieInfo> listMovies) {
        Log.d(TAG, "onPostExecute() called with: listMovies = [" + listMovies + "]");
    }
}
