package info.romanelli.udacity.android.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import info.romanelli.udacity.android.popularmovies.BuildConfig;
import info.romanelli.udacity.android.popularmovies.model.MovieInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MovieInfoFetcher {

    final static private String TAG = MovieInfoFetcher.class.getSimpleName();

    static private Retrofit RETROFIT;

    // https://medium.com/code-better/hiding-api-keys-from-your-android-repository-b23f5598b906
    // https://stackoverflow.com/a/34021467/435519 (option #2)
    //    https://stackoverflow.com/questions/33134031/is-there-a-safe-way-to-manage-api-keys
    final static private String API_KEY_TMDB = BuildConfig.ApiKey_TheMovieDB;

    synchronized static public void fetchMoviesInfo( final Context context,
                                                     final MoviesInfoFetchedListener listener,
                                                     final MoviesInfoType moviesInfoType ) {

        Log.d(TAG, "fetchMoviesInfo() called with: context = [" + context + "], listener = [" +
                listener + "], moviesInfoType = [" + moviesInfoType + "]");

        if (isOnline(context)) {
            Log.d(TAG, "fetchMoviesInfo(): Online; starting Retrofit process for list type ["+
                    moviesInfoType +"] ...");

            if (RETROFIT == null) {
                RETROFIT = new Retrofit.Builder()
                        .baseUrl("https://api.themoviedb.org")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }

            // Call instance can be exec'd only once!
            Call<MovieInfoFetcherResponse> call =
                    RETROFIT.create(MovieInfoFetcherService.class)
                            .fetchMoviesInfo( moviesInfoType.getURL(), API_KEY_TMDB );
            call.enqueue(new Callback<MovieInfoFetcherResponse>() {
                @Override
                public void onResponse(
                        @NonNull final Call<MovieInfoFetcherResponse> call,
                        @NonNull final Response<MovieInfoFetcherResponse> response) {
                    Log.d(TAG, "onResponse: body: " + response.body());
                    if (response.body() != null) {
                        //noinspection ConstantConditions
                        listener.moviesInfofetched( response.body().getMoviesInfo() );
                    }
                    else {
                        Log.e(TAG, "onResponse: No response body was returned!");
                    }
                }
                @Override
                public void onFailure(
                        @NonNull final Call<MovieInfoFetcherResponse> call,
                        @NonNull final Throwable t) {
                    Log.e(TAG, "onFailure: ", t);
                }
            });

        } else {
            Log.w(TAG, "fetchMoviesInfo: Not online, so cannot do fetching; aborting!");
        }
    }

    public enum MoviesInfoType {
        POPULAR ("popular"),
        TOP_RATED ("top_rated");
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

    interface MovieInfoFetcherService {
        @GET("3/movie/{moviesInfoTypeURL}")
        Call<MovieInfoFetcherResponse> fetchMoviesInfo(
                @Path("moviesInfoTypeURL")
                String moviesInfoTypeURL,
                @Query("api_key")
                String apiKey
        );
    }

    static class MovieInfoFetcherResponse {
        @SuppressWarnings("unused")
        @SerializedName("results") // Name of attrib in json
        private ArrayList<MovieInfo> listResults;
        /** @return An {@link ArrayList} of {@link MovieInfo} objects, or {@code null}. */
        ArrayList<MovieInfo> getMoviesInfo() {
            return listResults;
        }
    }

    // https://stackoverflow.com/questions/12575068/how-to-get-the-result-of-onpostexecute-to-main-activity-because-asynctask-is-a
    public interface MoviesInfoFetchedListener {
        void moviesInfofetched(ArrayList<MovieInfo> listMovieInfo);
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

    static public void setPosterToView(final AppCompatActivity activity, MovieInfo mi, ImageView iv) {

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
        Picasso.get().load(uri).into(
                iv,
                new com.squareup.picasso.Callback() {
                    // Null checks are because this shared code is called from both
                    // the detail activity, and the movie info RecyclerView.Adapter,
                    // but only needs to be done from detail activity, since it has
                    // a supportPostponeEnterTransition() on its onCreate method,
                    // which pauses, but the adapter does not make that same call,
                    // and hence does not postpone its transition animation.
                    @Override
                    public void onSuccess() {
                        if (activity != null) {
                            // Start up previously postpone transition to detail activity
                            activity.supportStartPostponedEnterTransition();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (activity != null) {
                            // Start up previously postpone transition to detail activity
                            activity.supportStartPostponedEnterTransition();
                        }
                    }
                }
        );
    }

}
