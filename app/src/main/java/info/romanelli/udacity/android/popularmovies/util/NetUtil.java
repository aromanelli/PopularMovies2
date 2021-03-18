package info.romanelli.udacity.android.popularmovies.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import info.romanelli.udacity.android.popularmovies.R;

public class NetUtil {

    private static final String TAG = AppUtil.class.getSimpleName();

    public static final String INTENT_ACTION_CONNECTIVITY_CHANGE = "connectivity_change";

    private static boolean CONNECTED = false;
    private static ConnectivityManager.NetworkCallback NETCALLBACK;

    /**
     * @see #isConnected()
     */
    public static boolean ifConnected(final View view) throws IllegalStateException {
        if (!isConnected()) {
            AppUtil.showToast(view.getContext(), view.getResources().getString(R.string.msg_offline), false);
            return false;
        }
        return true;
    }

    public static boolean isConnected() throws IllegalStateException {
        if (NETCALLBACK != null) {
            return CONNECTED;
        }
        throw new IllegalStateException("Not yet registered for network monitoring!");
    }

    /**
     * <p>Once this method has been called, you can call {@link #isConnected()}
     * to determine if a valid connection to the Net exists or not.</p><br>
     *
     * <p>Also, when connectivity changes, a {@link Context#sendBroadcast(Intent)}
     * will be done, and the {@link Intent} will have an {@link Intent#getAction()} value
     * of {@link info.romanelli.udacity.android.popularmovies.util.NetUtil#INTENT_ACTION_CONNECTIVITY_CHANGE},
     * and an extra {@link Boolean} value that can be obtained via the
     * {@link Intent#getBooleanExtra(String, boolean)} key value of
     * {@link ConnectivityManager#EXTRA_NO_CONNECTIVITY}.</p>
     *
     * @param context Will be used to obtain the app context via {@link Context#getApplicationContext()}
     * @throws IllegalStateException if {@link Context#getSystemService(String)} returns
     *      {@code null} (which it should not)
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static synchronized void registerForNetworkMonitoring(final Context context) throws IllegalStateException {

        if (NETCALLBACK == null) {

            final Context appContext = context.getApplicationContext();

            // Configure initial network state ...
            ConnectivityManager cm =
                    (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm == null) {
                throw new IllegalStateException("Unable to obtain the CONNECTIVITY_SERVICE system service!");
            }

            // Set the initial connection state ...
            // https://stackoverflow.com/questions/53532406/activenetworkinfo-type-is-deprecated-in-api-level-28
            Network actnet = cm.getActiveNetwork();
            if (actnet != null) {
                NetworkInfo netInfo = cm.getNetworkInfo(actnet);
                CONNECTED = (netInfo != null) && netInfo.isConnected();
            } else {
                CONNECTED = false;
            }
            Log.d(TAG, "registerForNetworkMonitoring: Initial connection state: " + CONNECTED);

            // Configure callback/listener for network state changes ...
            NETCALLBACK = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    Log.d(TAG, "onAvailable() called with: network = [" + network + "]");
                    // https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onAvailable(android.net.Network)
                    CONNECTED = true;
                    appContext.sendBroadcast(getIntent());
                }
                @Override
                public void onLost(Network network) {
                    Log.d(TAG, "onLost() called with: network = [" + network + "]");
                    CONNECTED = false;
                    appContext.sendBroadcast(getIntent());
                }
                private Intent getIntent() {
                    Intent intent = new Intent();
                    intent.setAction(INTENT_ACTION_CONNECTIVITY_CHANGE);
                    intent.putExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, (!CONNECTED));
                    return intent;
                }
            };

            // Regiseter callback/listener for network state changes ...
            cm.registerNetworkCallback(new NetworkRequest.Builder().build(), NETCALLBACK);

        } else {
            Log.d(TAG, "registerForNetworkMonitoring: Already registered for network monitoring");
        }

    }

}
