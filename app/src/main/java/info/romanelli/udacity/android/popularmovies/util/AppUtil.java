package info.romanelli.udacity.android.popularmovies.util;

import android.content.Context;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.concurrent.atomic.AtomicBoolean;

import info.romanelli.udacity.android.popularmovies.R;
import info.romanelli.udacity.android.popularmovies.network.MovieInfo;
import info.romanelli.udacity.android.popularmovies.network.MovieVideosInfo;

public class AppUtil {

    final static private String TAG = AppUtil.class.getSimpleName();

    static public void setPosterToView(final AppCompatActivity activity, MovieInfo mi, ImageView iv) {

//        // Determine good size for posters (should call from MainActivity) ...
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        Log.d(TAG, "onCreate: DisplayMetrics: " + metrics);

        final Uri uri = Uri.parse("https://image.tmdb.org/t/p/")
                .buildUpon()
                // "w92", "w154", "w185", "w342", "w500", "w780", or "original"
                .appendEncodedPath("w342") // NOT appendPath!
                .appendEncodedPath(mi.getPosterURL()) // NOT appendPath!
                .build();
        Log.d(TAG, "setPosterToView: Uri for poster: ["+ uri +"]");
        Picasso.get().load(uri).into(
                iv,
                new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        // Null checks are because this shared code is called from both
                        // the detail activity, and the movie info RecyclerView.Adapter,
                        // but only needs to be done from detail activity, since it has
                        // a supportPostponeEnterTransition() on its onCreate method,
                        // which pauses, but the adapter does not make that same call,
                        // and hence does not postpone its transition animation.
                        if (activity != null) {
                            // Start up previously postpone transition to detail activity
                            activity.supportStartPostponedEnterTransition();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "ERROR! setPosterToView: Uri for poster: ["+ uri +"]", e);
                        // Null checks are because this shared code is called from both
                        // the detail activity, and the movie info RecyclerView.Adapter,
                        // but only needs to be done from detail activity, since it has
                        // a supportPostponeEnterTransition() on its onCreate method,
                        // which pauses, but the adapter does not make that same call,
                        // and hence does not postpone its transition animation.
                        if (activity != null) {
                            // Start up previously postpone transition to detail activity
                            activity.supportStartPostponedEnterTransition();
                        }
                    }
                }
        );
    }

    static public void setVideoThumbnailToView(MovieVideosInfo info, ImageView imageView) {
        Log.d(TAG, "setVideoThumbnailToView() called with: info = [" + info + "], imageView = [" + imageView + "]");
        if ("YouTube".equals(info.getSite())) {
            final Uri uri = Uri.parse("https://img.youtube.com/vi/"+ info.getKey() +"/0.jpg");
            Log.d(TAG, "setVideoThumbnailToView: Uri for video thumbnail: ["+ uri +"]");
            Picasso.get().load(uri).placeholder(R.drawable.ic_launcher_background).into(imageView);
        }
        else {
            Log.w(TAG, "setVideoThumbnailToView: Video is NOT from YouTube! ["+ info.getSite() +"]");
        }
    }

    static private Toast TOAST;
    static private AtomicBoolean PENDING_TOAST = new AtomicBoolean(false);
    static private CountDownTimer TIMER;

    static public void showToast(final Context owner, final String message, final boolean delayShowing) {
        Log.d(TAG, "showToast() called with: owner = [" + owner + "], message = [" +
                message + "], pending = ["+ PENDING_TOAST.get() +"] " + Thread.currentThread().getName());

        if (PENDING_TOAST.compareAndSet(false, true)) {
            if (TIMER != null) {
                TIMER.cancel();
            }
            cancelToast();
        }

        if (delayShowing) {
            TIMER = new CountDownTimer(200, 100) {
                public void onTick(long millisUntilFinished) {
                    Log.d(TAG, "onTick() called with: millisUntilFinished = [" + millisUntilFinished +
                            "], pending = [" + PENDING_TOAST.get() + "] " + Thread.currentThread().getName());
                    if (!PENDING_TOAST.get()) {
                        Log.d(TAG, "onTick() canceling timer!  millisUntilFinished = [" + millisUntilFinished +
                                "], pending = [" + PENDING_TOAST.get() + "] " + Thread.currentThread().getName());
                        cancel(); // Cancel timer, not toast
                    }
                }
                public void onFinish() {
                    Log.d(TAG, "onFinish() called, pending = [" + PENDING_TOAST.get() + "] " + Thread.currentThread().getName());
                    if (PENDING_TOAST.get()) {
                        drawToast(owner, message);
                        PENDING_TOAST.set(false);
                    }
                }
            }.start();
        }
        else {
            drawToast(owner, message);
            PENDING_TOAST.set(false);
        }

    }

    static public void hideToast() {
        Log.d(TAG, "hideToast() called, pending = ["+ PENDING_TOAST.get() +"] " + Thread.currentThread().getName());
        cancelToast();
        PENDING_TOAST.set(false);
    }

    static private void cancelToast() {
        Log.d(TAG, "cancelToast() called, TOAST = ["+ TOAST +"], pending = ["+ PENDING_TOAST.get() +"] " + Thread.currentThread().getName());
        if (TOAST != null) {
            Log.d(TAG, "cancelToast: CANCELING TOAST! " + Thread.currentThread().getName());
            TOAST.cancel();
            TOAST = null;
        }
    }

    static private void drawToast(final Context owner, final String message) {
        cancelToast(); // In case a previous toast is showing, hide, NOT cancel!
        TOAST = Toast.makeText(
                owner,
                (message != null) ? message : owner.getString(R.string.progress_retrieving),
                Toast.LENGTH_LONG);
        TOAST.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        Log.d(TAG, "onFinish() called, SHOWING TOAST!  pending = [" + PENDING_TOAST.get() + "] " + Thread.currentThread().getName());
        TOAST.show();
    }

}
