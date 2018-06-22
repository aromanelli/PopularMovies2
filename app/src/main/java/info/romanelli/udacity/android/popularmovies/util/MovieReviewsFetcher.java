package info.romanelli.udacity.android.popularmovies.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import info.romanelli.udacity.android.popularmovies.BuildConfig;
import info.romanelli.udacity.android.popularmovies.model.MovieInfo;
import info.romanelli.udacity.android.popularmovies.model.MovieReviewsInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MovieReviewsFetcher extends AbstractFetcher {

    final static private String TAG = MovieReviewsFetcher.class.getSimpleName();

    synchronized static public void fetchMovieReviewsInfo (final Context context,
                                                          final MovieInfo movieInfo,
                                                          final Listener listener ) {

        Log.d(TAG, "fetchMovieReviewsInfo() called with: context = [" + context + "], listener = [" +
                listener + "]");

        if (InfoFetcherUtil.isOnline(context)) {
            Log.d(TAG, "fetchMovieReviewsInfo(): Online; starting Retrofit process...");

            // Call instance can be exec'd only once!
            Call<MovieReviewsFetcher.Response> call =
                    RETROFIT.create(MovieReviewsFetcher.Service.class)
                            .fetchMovieReviewsInfo(
                                    Integer.toString(movieInfo.getId()),
                                    "reviews",
                                    // https://medium.com/code-better/hiding-api-keys-from-your-android-repository-b23f5598b906
                                    // https://stackoverflow.com/a/34021467/435519 (option #2)
                                    //    https://stackoverflow.com/questions/33134031/is-there-a-safe-way-to-manage-api-keys
                                    BuildConfig.ApiKey_TheMovieDB
                            );
            Log.d(TAG, "fetchMovieReviewsInfo(): enqueue; Uri for list of movies: ["+ call.request().url() +"]");
            call.enqueue(new Callback<MovieReviewsFetcher.Response>() {
                @Override
                public void onResponse(
                        @NonNull final Call<MovieReviewsFetcher.Response> call,
                        @NonNull final retrofit2.Response<MovieReviewsFetcher.Response> response) {
                    Log.d(TAG, "onResponse: body: " + response.body());
                    if (response.body() != null) {
                        //noinspection ConstantConditions
                        notifyListener(response.body().getMovieReviewsInfo());
                    }
                    else {
                        Log.e(TAG, "onResponse: No response body was returned!");
                        notifyListener(null);
                    }
                }
                @Override
                public void onFailure(
                        @NonNull final Call<MovieReviewsFetcher.Response> call,
                        @NonNull final Throwable t) {
                    Log.e(TAG, "onFailure: ", t);
                    notifyListener(null);
                }
                private void notifyListener(ArrayList<MovieReviewsInfo> list) {
                    if (list == null) {
                        // Set empty list, so thaat DetailActivity will continue (non-null used as flag) ...
                        list = new ArrayList<>(0);
                    }
                    listener.fetchedMovieReviewsInfo( movieInfo, list );
                }
            });

        } else {
            Log.w(TAG, "fetchMovieReviewsInfo: Not online, so cannot do fetching; aborting!");
        }
    }

    interface Service {
        @GET("3/movie/{movieId}/{movieReviewsInfoURL}")
        Call<MovieReviewsFetcher.Response> fetchMovieReviewsInfo(
                @Path("movieId")
                        String movieId,
                @Path("movieReviewsInfoURL")
                        String movieReviewsInfoURL,
                @Query("api_key")
                        String apiKey
        );
    }

    static class Response {
        @SuppressWarnings("unused")
        @SerializedName("results") // Name of attrib in json
        private ArrayList<MovieReviewsInfo> listResults;
        /** @return An {@link ArrayList} of {@link MovieReviewsInfo} objects, or {@code null}. */
        ArrayList<MovieReviewsInfo> getMovieReviewsInfo() {
            return listResults;
        }
    }

    // https://stackoverflow.com/questions/12575068/how-to-get-the-result-of-onpostexecute-to-main-activity-because-asynctask-is-a
    public interface Listener {
        void fetchedMovieReviewsInfo(MovieInfo movieInfo,
                                     ArrayList<MovieReviewsInfo> listMovieReviewsInfo);
    }

}
