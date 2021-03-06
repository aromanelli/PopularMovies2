package info.romanelli.udacity.android.popularmovies.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import info.romanelli.udacity.android.popularmovies.BuildConfig;
import info.romanelli.udacity.android.popularmovies.util.NetUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MovieVideosFetcher extends AbstractFetcher {

    final static private String TAG = MovieVideosFetcher.class.getSimpleName();

    synchronized static public void fetchMovieVideosInfo (final Context context,
                                                          final MovieInfo movieInfo,
                                                          final Listener listener ) {

        Log.d(TAG, "fetchMovieVideosInfo() called with: context = [" + context + "], listener = [" +
                listener + "]");

        if (NetUtil.isConnected()) {
            Log.d(TAG, "fetchMovieVideosInfo(): Online; starting Retrofit process...");

            // Call instance can be exec'd only once!
            Call<MovieVideosFetcher.Response> call =
                    RETROFIT.create(MovieVideosFetcher.Service.class)
                            .fetchMovieVideosInfo(
                                    Integer.toString(movieInfo.getId()),
                                    "videos",
                                    // https://medium.com/code-better/hiding-api-keys-from-your-android-repository-b23f5598b906
                                    // https://stackoverflow.com/a/34021467/435519 (option #2)
                                    //    https://stackoverflow.com/questions/33134031/is-there-a-safe-way-to-manage-api-keys
                                    BuildConfig.ApiKey_TheMovieDB
                            );
            Log.d(TAG, "fetchMovieVideosInfo(): enqueue; Uri for list of movies: ["+ call.request().url() +"]");
            call.enqueue(new Callback<MovieVideosFetcher.Response>() {
                @Override
                public void onResponse(
                        @NonNull final Call<MovieVideosFetcher.Response> call,
                        @NonNull final retrofit2.Response<MovieVideosFetcher.Response> response) {
                    Log.d(TAG, "onResponse: body: " + response.body());
                    if (response.body() != null) {
                        notifyListener(response.body().getMovieVideosInfo());
                    }
                    else {
                        Log.e(TAG, "onResponse: No response body was returned!");
                        notifyListener(null);
                    }
                }
                @Override
                public void onFailure(
                        @NonNull final Call<MovieVideosFetcher.Response> call,
                        @NonNull final Throwable t) {
                    Log.e(TAG, "onFailure: ", t);
                    notifyListener(null);
                }
                private void notifyListener(ArrayList<MovieVideosInfo> list) {
                    if (list == null) {
                        // Set empty list, so thaat DetailActivity will continue (non-null used as flag) ...
                        list = new ArrayList<>(0);
                    }
                    listener.fetchedMovieVideosInfo( movieInfo, list );
                }
            });

        } else {
            Log.e(TAG, "fetchMovieVideosInfo: Not online, so cannot do fetching; aborting!");
        }
    }

    interface Service {
        @GET("3/movie/{movieId}/{movieVideosInfoURL}")
        Call<MovieVideosFetcher.Response> fetchMovieVideosInfo(
                @Path("movieId")
                        String movieId,
                @Path("movieVideosInfoURL")
                        String movieVideosInfoURL,
                @Query("api_key")
                        String apiKey
        );
    }

    static class Response {
        @SerializedName("results") // Name of attrib in json
        private ArrayList<MovieVideosInfo> listResults;
        /** @return An {@link ArrayList} of {@link MovieVideosInfo} objects, or {@code null}. */
        ArrayList<MovieVideosInfo> getMovieVideosInfo() {
            return listResults;
        }
    }

    // https://stackoverflow.com/questions/12575068/how-to-get-the-result-of-onpostexecute-to-main-activity-because-asynctask-is-a
    public interface Listener {
        void fetchedMovieVideosInfo(MovieInfo movieInfo,
                                    ArrayList<MovieVideosInfo> listMovieVideosInfo);
    }

}
