package info.romanelli.udacity.android.popularmovies;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import info.romanelli.udacity.android.popularmovies.databinding.ActivityMainBinding;
import info.romanelli.udacity.android.popularmovies.network.MovieInfo;
import info.romanelli.udacity.android.popularmovies.network.MoviesInfoFetcher;
import info.romanelli.udacity.android.popularmovies.util.AppUtil;
import info.romanelli.udacity.android.popularmovies.util.MovieInfoAdapter;
import info.romanelli.udacity.android.popularmovies.util.NetUtil;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity
        extends AppCompatActivity
        implements
            MovieInfoAdapter.OnClickHandler,
            MoviesInfoFetcher.Listener {

    final static private String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

    private MovieInfoAdapter mAdapterMovieInfo;

    // Bundle.putParcelableArrayList requires ArrayList not List
    private ArrayList<MovieInfo> listMovieInfo;

    private MoviesInfoFetcher.MoviesInfoType typeMoviesList =
        MoviesInfoFetcher.MoviesInfoType.POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fullscreenContent.setLayoutManager(new GridLayoutManager(this, 2));
        binding.fullscreenContent.hasFixedSize();

        mAdapterMovieInfo = new MovieInfoAdapter(this);
        binding.fullscreenContent.setAdapter(mAdapterMovieInfo);

        NetUtil.registerForNetworkMonitoring(this);

        if (NetUtil.ifConnected(this.findViewById(R.id.fullscreen_content))) {
            fetchMoviesInfo(savedInstanceState);
        }
    }

    private void fetchMoviesInfo(final Bundle savedInstanceState) {
        // If first-time call, fetched movie info data ...
        if ( (savedInstanceState == null) ||
                (!savedInstanceState.containsKey(DetailActivity.KEY_BUNDLE_MOVIEINFO))) {
            MoviesInfoFetcher.fetchMoviesInfo(
                    this, this, typeMoviesList );
        }
        else {
            // Re-create from rotation, reload previously saved movie info data ...
            listMovieInfo = savedInstanceState.getParcelableArrayList(DetailActivity.KEY_BUNDLE_MOVIEINFO);
            typeMoviesList = MoviesInfoFetcher.MoviesInfoType.valueOf(
                    (String) savedInstanceState.get(MoviesInfoFetcher.MoviesInfoType.class.getSimpleName())
            );
            fetchedMoviesInfo(listMovieInfo);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(DetailActivity.KEY_BUNDLE_MOVIEINFO, listMovieInfo);
        outState.putString(MoviesInfoFetcher.MoviesInfoType.class.getSimpleName(), typeMoviesList.name());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMovieClick(final MovieInfo movieInfo, final ImageView ivPoster) {

        if (!NetUtil.ifConnected(this.findViewById(R.id.fullscreen_content))) {
            // finish();
            return;
        }

        // Call showToast early, as it has its own internal 'wait before show' timer ...
        AppUtil.showToast(this, getString(R.string.progress_retrieving), true);

        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_BUNDLE_MOVIEINFO, movieInfo);

        // Setup transition options for movie poster from/to main/detail activities ...
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
            this,
                ivPoster,
                // Set in DetailActivity.populateUI method ...
                ViewCompat.getTransitionName(ivPoster)
        );

        startActivity(intent, activityOptions.toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Disable menu that was selected, enable other menu ...
        switch(typeMoviesList) {
            // For getItem index value see activity_main_menu.xml's orderInCategory values
            case TOP_RATED:
                menu.getItem(0).setEnabled(false);
                menu.getItem(1).setEnabled(true);
                menu.getItem(2).setEnabled(true);
                break;
            case POPULAR:
                menu.getItem(0).setEnabled(true);
                menu.getItem(1).setEnabled(false);
                menu.getItem(2).setEnabled(true);
                break;
            case FAVORITES:
                menu.getItem(0).setEnabled(true);
                menu.getItem(1).setEnabled(true);
                menu.getItem(2).setEnabled(false);
                break;
            default:
                Log.e(TAG, "onPrepareOptionsMenu: Unknown movies list type! ["+ typeMoviesList +"]" );
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // https://sites.google.com/a/android.com/tools/tips/non-constant-fields
        int id = item.getItemId();
        if (id == R.id.menu_favorites) {
            typeMoviesList = MoviesInfoFetcher.MoviesInfoType.FAVORITES;
            MoviesInfoFetcher.fetchMoviesInfo(this, this, typeMoviesList);
        }
        else if (id == R.id.menu_popular) {
            if (NetUtil.ifConnected(this.findViewById(R.id.fullscreen_content))) {
                typeMoviesList = MoviesInfoFetcher.MoviesInfoType.POPULAR;
                MoviesInfoFetcher.fetchMoviesInfo(this, this, typeMoviesList);
            }
        }
        else if (id == R.id.menu_top_rated) {
            if (NetUtil.ifConnected(this.findViewById(R.id.fullscreen_content))) {
                typeMoviesList = MoviesInfoFetcher.MoviesInfoType.TOP_RATED;
                MoviesInfoFetcher.fetchMoviesInfo(this, this, typeMoviesList);
            }
        }
        else {
            Log.e(TAG, "onOptionsItemSelected: Unknown options item id! [" + item.getItemId() + "]");
            return true; // consume, don't continue
        }
        Log.d(TAG, "onOptionsItemSelected: typeMoviesList set to --> " + typeMoviesList);

        invalidateOptionsMenu(); // Android 3.0+ needs this to re-call onPrepareOptionsMenu
        return super.onOptionsItemSelected(item);
    }

    /**
     * <p>Called by {@link MoviesInfoFetcher} when it has completed fetching movie info.</p>
     * @param listMovieInfo An {@link ArrayList} of {@link MovieInfo} objects, containing movie info.
     */
    @Override
    public void fetchedMoviesInfo(ArrayList<MovieInfo> listMovieInfo) {
        Log.d(TAG, "moviesInfofetched() called with: listMovieInfo = ["+ typeMoviesList +"][" + listMovieInfo + "]");
        // Remember the list of MovieInfo objects ...
        this.listMovieInfo = listMovieInfo;
        // Tell the adapter to update ...
        binding.fullscreenContent.getLayoutManager().scrollToPosition(0);
        mAdapterMovieInfo.setData(this.listMovieInfo);
        // Set the title of the app to reflect the type of movies being showing ...
        switch(typeMoviesList) {
            case POPULAR:
                setTitle(getResources().getString(R.string.app_title_popular));
                break;
            case TOP_RATED:
                setTitle(getResources().getString(R.string.app_title_top_rated));
                break;
            case FAVORITES:
                setTitle(getResources().getString(R.string.app_title_favorites));
                break;
            default:
                Log.e(TAG, "fetched: Unknown movies list type! ["+ typeMoviesList +"]" );
                break;
        }
    }

}
