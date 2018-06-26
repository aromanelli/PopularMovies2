package info.romanelli.udacity.android.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicBoolean;

import info.romanelli.udacity.android.popularmovies.R;
import info.romanelli.udacity.android.popularmovies.network.MovieInfo;

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

    static private Toast TOAST;
    static private AtomicBoolean PENDING_TOAST = new AtomicBoolean(false);

    static public void showToast(final Context owner, final String message) {
        Log.d(TAG, "showToast() called with: owner = [" + owner + "], message = [" + message +
                "], pending = ["+ PENDING_TOAST.get() +"]");
        if (PENDING_TOAST.compareAndSet(false, true)) {
            new CountDownTimer(200, 100) {
                public void onTick(long millisUntilFinished) {
                    Log.d(TAG, "onTick() called with: millisUntilFinished = [" + millisUntilFinished +
                            "], pending = ["+ PENDING_TOAST.get() +"]");
                    if (!PENDING_TOAST.get()) {
                        Log.d(TAG, "onTick() canceling timer!  millisUntilFinished = [" + millisUntilFinished +
                                "], pending = ["+ PENDING_TOAST.get() +"]");
                        cancel(); // Cancel timer, not toast
                    }
                }
                public void onFinish() {
                    Log.d(TAG, "onFinish() called, pending = ["+ PENDING_TOAST.get() +"]");
                    if (PENDING_TOAST.get()) {
                        cancelToast(); // In case a previous toast is showing, hide, NOT cancel!
                        TOAST = Toast.makeText(
                                owner,
                                (message != null) ? message : owner.getString(R.string.progress_retrieving),
                                Toast.LENGTH_LONG);
                        TOAST.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                        Log.d(TAG, "onFinish() called, SHOWING TOAST!  pending = ["+ PENDING_TOAST.get() +"]");
                        TOAST.show();
                        PENDING_TOAST.set(false);
                    }
                }
            }.start();
        }
    }

    static public void hideToast() {
        Log.d(TAG, "hideToast() called, pending = ["+ PENDING_TOAST.get() +"]");
        cancelToast();
        PENDING_TOAST.set(false);
    }

    static private void cancelToast() {
        Log.d(TAG, "cancelToast() called, TOAST = ["+ TOAST +"], pending = ["+ PENDING_TOAST.get() +"]");
        if (TOAST != null) {
            Log.d(TAG, "cancelToast: CANCELING TOAST!");
            TOAST.cancel();
            TOAST = null;
        }
    }

}
