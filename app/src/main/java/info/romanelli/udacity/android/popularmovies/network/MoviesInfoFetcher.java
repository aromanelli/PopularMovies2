package info.romanelli.udacity.android.popularmovies.network;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.util.ArrayList;

import info.romanelli.udacity.android.popularmovies.BuildConfig;
import info.romanelli.udacity.android.popularmovies.util.FavMoviesObserver;
import info.romanelli.udacity.android.popularmovies.util.NetUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MoviesInfoFetcher extends AbstractFetcher {

    final static private String TAG = MoviesInfoFetcher.class.getSimpleName();

    static private FavMoviesObserver OBSERVER;

    synchronized static public void fetchMoviesInfo( final AppCompatActivity activity,
                                                     final MoviesInfoFetcher.Listener listener,
                                                     final MoviesInfoType moviesInfoType ) {

        Log.d(TAG, "fetchMoviesInfo() called with: activity = [" + activity + "], listener = [" +
                listener + "], moviesInfoType = [" + moviesInfoType + "]");

        if (OBSERVER != null) {
            // Update the observer for non-Favorites type viewing in progress, so that it
            // does not do favorites list building/setting for non-favorite views...
            OBSERVER.setMoviesInfoType(moviesInfoType);
        }

        if (MoviesInfoType.FAVORITES.equals(moviesInfoType)) {

            /////////////////////////////////////////
            // Need to use locally stored (in DB) ...
            /////////////////////////////////////////

            if (OBSERVER != null) {
                // Not sure if this stop() is needed to prevent memory leaks, or if the
                // Android Observer class garbage collects well after de-assignment??
                OBSERVER.stop();
            }
            OBSERVER = new FavMoviesObserver(listener, moviesInfoType);
            OBSERVER.start(activity);
            return;

        }

        ///////////////////////////////////////
        // Need to call out to the Internet ...
        ///////////////////////////////////////

        if (NetUtil.isConnected()) {
            Log.d(TAG, "fetchMoviesInfo(): Online; starting Retrofit process for list type [" +
                    moviesInfoType + "] ...");

            // Call instance can be exec'd only once!
            Call<MoviesInfoFetcher.Response> call =
                    RETROFIT.create(MoviesInfoFetcher.Service.class)
                            .fetchMoviesInfo(
                                    moviesInfoType.getURL(),
                                    // https://medium.com/code-better/hiding-api-keys-from-your-android-repository-b23f5598b906
                                    // https://stackoverflow.com/a/34021467/435519 (option #2)
                                    //    https://stackoverflow.com/questions/33134031/is-there-a-safe-way-to-manage-api-keys
                                    BuildConfig.ApiKey_TheMovieDB
                            );
            Log.d(TAG, "fetchMoviesInfo(): enqueue; Uri for list of movies: [" + call.request().url() + "]");
            call.enqueue(new Callback<MoviesInfoFetcher.Response>() {
                @Override
                public void onResponse(
                        @NonNull final Call<MoviesInfoFetcher.Response> call,
                        @NonNull final retrofit2.Response<MoviesInfoFetcher.Response> response) {
                    Log.d(TAG, "onResponse: body: " + response.body());
                    if (response.body() != null) {
                        listener.fetchedMoviesInfo(response.body().getMoviesInfo());
                    } else {
                        Log.e(TAG, "onResponse: No response body was returned!");
                    }
                }

                @Override
                public void onFailure(
                        @NonNull final Call<MoviesInfoFetcher.Response> call,
                        @NonNull final Throwable t) {
                    Log.e(TAG, "onFailure: ", t);
                }
            });

        }
        else {
            Log.e(TAG, "fetchMoviesInfo: Not online, so cannot do fetching; aborting!");
        }

    }

    public enum MoviesInfoType {
        POPULAR ("popular"),
        TOP_RATED ("top_rated"),
        FAVORITES (""); // No URL/call for favorites type
        final private String typeURLPartial;
        MoviesInfoType(String typeURLPartial) {
            this.typeURLPartial = typeURLPartial;
        }
        /** <p>Returns a partial {@link URL} for the movies list type.</p>
         * @return Either '{@code popular}' or '{@code top_rated}'. */
        String getURL() {
            return typeURLPartial;
        }
    }

    interface Service {
        @GET("3/movie/{moviesInfoTypeURL}")
        Call<MoviesInfoFetcher.Response> fetchMoviesInfo(
                @Path("moviesInfoTypeURL")
                String moviesInfoTypeURL,
                @Query("api_key")
                String apiKey
        );
    }

    static class Response {
        @SerializedName("results") // Name of attrib in json
        private ArrayList<MovieInfo> listResults;
        /** @return An {@link ArrayList} of {@link MovieInfo} objects, or {@code null}. */
        ArrayList<MovieInfo> getMoviesInfo() {
            return listResults;
        }
    }

    // https://stackoverflow.com/questions/12575068/how-to-get-the-result-of-onpostexecute-to-main-activity-because-asynctask-is-a
    public interface Listener {
        void fetchedMoviesInfo(ArrayList<MovieInfo> listMovieInfo);
    }

}
