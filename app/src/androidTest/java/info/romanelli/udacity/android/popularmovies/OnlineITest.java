package info.romanelli.udacity.android.popularmovies;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import info.romanelli.udacity.android.popularmovies.util.AppUtil;
import info.romanelli.udacity.android.popularmovies.util.NetUtil;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class OnlineITest {

    private static final String TAG = AppUtil.class.getSimpleName();

    @Test
    public void isConnected() {

        TestUtil.setNetworkAccess(true);

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("info.romanelli.udacity.android.popularmovies", appContext.getPackageName());

        NetUtil.registerForNetworkMonitoring(appContext);

        Assert.assertTrue(NetUtil.isConnected());

        TestUtil.setNetworkAccess(false);

        Assert.assertFalse(NetUtil.isConnected());

        // TODO AOR Code Intent broadcast receiving
        // NetUtil.ifConnected // TODO AOR CODE THIS!

        TestUtil.setNetworkAccess(true);

        Assert.assertTrue(NetUtil.isConnected());
    }

}
