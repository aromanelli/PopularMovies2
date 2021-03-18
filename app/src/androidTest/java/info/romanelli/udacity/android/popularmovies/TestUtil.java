package info.romanelli.udacity.android.popularmovies;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import info.romanelli.udacity.android.popularmovies.util.AppUtil;

public class TestUtil {

    private static final String TAG = AppUtil.class.getSimpleName();

    public static void setNetworkAccess(final boolean enabled) {
        final String cmd = enabled ? "enable" : "disable";
        TestUtil.executeShellCommand("svc wifi " + cmd);
        TestUtil.executeShellCommand("svc data " + cmd);
    }

    @SuppressWarnings("WeakerAccess")
    public static String executeShellCommand(final String cmd) {
        return executeShellCommand(cmd, 2000);
    }

    @SuppressWarnings("WeakerAccess")
    public static String executeShellCommand(final String cmd, final long delay) {
        Log.d(TAG, "executeShellCommand() called with: cmd = [" + cmd + "], delay = [" + delay + "]");
        // https://developer.android.com/reference/android/app/UiAutomation#executeShellCommand(java.lang.String)
        StringBuffer sb = new StringBuffer();
        try (ParcelFileDescriptor pfdW =
                     InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(cmd)) {
            if (pfdW.canDetectErrors()) {
                pfdW.checkError();
            }
            new BufferedReader(new InputStreamReader( new FileInputStream(pfdW.getFileDescriptor())))
                    .lines().forEach(s -> {
                Log.d(TAG, String.format("executeShellCommand: [%s]", s));
                sb.append(s);
                sb.append('\n');
            });
        } catch (final IOException ioe) {
            Log.e(TAG, "executeShellCommand: ", ioe);
            Assert.fail(ioe.getMessage());
        }

        try {
            Thread.sleep(delay); // TODO AOR A better way than just delaying/sleeping?
        } catch (InterruptedException ie) {
            Log.w(TAG, "executeShellCommand: ", ie);
        }

        final String output = sb.toString();
        Log.d(TAG, "executeShellCommand: output: [" + output + "]");
        return output;
    }

}
