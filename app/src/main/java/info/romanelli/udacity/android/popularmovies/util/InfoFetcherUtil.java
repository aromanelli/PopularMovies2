package info.romanelli.udacity.android.popularmovies.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.CountDownTimer;
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

    static private boolean PENDING_PROGRESS_DIALOG = false;
    static private ProgressDialog PROGRESS_DIALOG;
    synchronized static public void showProgress(
            final Context owner, final String title, final String message) {
        Log.d(TAG, "showProgress() called with: owner = [" + owner + "], title = [" + title + "], message = [" + message + "]");
        Log.d(TAG, "showProgress: PENDING_PROGRESS_DIALOG: " + PENDING_PROGRESS_DIALOG +
                ", PROGRESS_DIALOG: " + PROGRESS_DIALOG);
        if (!PENDING_PROGRESS_DIALOG) {
            PENDING_PROGRESS_DIALOG = true;
            // Wait a bit before showing the progress dialog, don't show if no longer needed ...
            new CountDownTimer(800, 200) {
                public void onTick(long millisUntilFinished) {
                    Log.d(TAG, "onTick: PENDING_PROGRESS_DIALOG: " + PENDING_PROGRESS_DIALOG +
                            ", PROGRESS_DIALOG: " + PROGRESS_DIALOG);
                    if (!PENDING_PROGRESS_DIALOG) {
                        cancel();
                    }
                }
                public void onFinish() {
                    Log.d(TAG, "onFinish: PENDING_PROGRESS_DIALOG: " + PENDING_PROGRESS_DIALOG +
                            ", PROGRESS_DIALOG: " + PROGRESS_DIALOG);
                    PROGRESS_DIALOG = new ProgressDialog(owner);
                    PROGRESS_DIALOG.setMax(100);
                    PROGRESS_DIALOG.setTitle(title);
                    PROGRESS_DIALOG.setMessage(message);
                    PROGRESS_DIALOG.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    PROGRESS_DIALOG.show();
                }
            }.start();
        }
        else {
            //noinspection ConstantConditions
            Log.d(TAG, "showProgress: PENDING_PROGRESS_DIALOG: " + PENDING_PROGRESS_DIALOG +
                    ", PROGRESS_DIALOG: " + PROGRESS_DIALOG);
            Log.w(TAG, "showProgress: A progress dialog is already queued for displaying.");
        }
    }

    synchronized static public void closeProgress() {
        if (PENDING_PROGRESS_DIALOG) {
            if (PROGRESS_DIALOG != null) {
                PROGRESS_DIALOG.dismiss();
                PROGRESS_DIALOG = null;
            }
            PENDING_PROGRESS_DIALOG = false;
        }
    }

}
