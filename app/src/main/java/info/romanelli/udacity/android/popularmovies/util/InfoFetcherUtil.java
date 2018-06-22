package info.romanelli.udacity.android.popularmovies.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import info.romanelli.udacity.android.popularmovies.model.MovieInfo;

public class InfoFetcherUtil {

    final static private String TAG = InfoFetcherUtil.class.getSimpleName();

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

    static private ProgressDialog PROGRESS_DIALOG;
    synchronized static public void showProgress(
            final Context owner, final String title, final String message) {
        if (PROGRESS_DIALOG == null) {
            PROGRESS_DIALOG = new ProgressDialog(owner);
            PROGRESS_DIALOG.setMax(100);
            PROGRESS_DIALOG.setTitle(title);
            PROGRESS_DIALOG.setMessage(message);
            PROGRESS_DIALOG.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            PROGRESS_DIALOG.show();
        }
    }

    synchronized static public void closeProgress() {
        if (PROGRESS_DIALOG != null) {
            PROGRESS_DIALOG.dismiss();
            PROGRESS_DIALOG = null;
        }
        else {
            Log.w(TAG, "closeProgress: Trying to close a progress dialog when one was not shown!");
        }
    }

}
