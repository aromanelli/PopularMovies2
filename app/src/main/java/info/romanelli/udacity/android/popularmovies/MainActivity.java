package info.romanelli.udacity.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.romanelli.udacity.android.popularmovies.model.MovieInfo;
import info.romanelli.udacity.android.popularmovies.model.MovieInfoAdapter;
import info.romanelli.udacity.android.popularmovies.util.MovieInfoFetcher;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity
        extends AppCompatActivity
        implements
            MovieInfoAdapter.MovieInfoAdapterOnClickHandler,
            MovieInfoFetcher.MoviesInfoFetchedListener {

    final static private String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.fullscreen_content)
    RecyclerView mContentView;

    private MovieInfoAdapter mAdapterMovieInfo;

    // Bundle.putParcelableArrayList requires ArrayList not List
    private ArrayList<MovieInfo> listMovieInfo;

    private MovieInfoFetcher.MoviesInfoType typeMoviesList =
        MovieInfoFetcher.MoviesInfoType.POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContentView.setLayoutManager(new GridLayoutManager(this, 2));
        mContentView.hasFixedSize();

        mAdapterMovieInfo = new MovieInfoAdapter(this);
        mContentView.setAdapter(mAdapterMovieInfo);

        fetchMoviesInfo(savedInstanceState);
    }

    private void fetchMoviesInfo(final Bundle savedInstanceState) {
        // If first-time call, fetched movie info data ...
        if ( (savedInstanceState == null) ||
                (!savedInstanceState.containsKey(DetailActivity.KEY_BUNDLE_MOVIEINFO))) {
            MovieInfoFetcher.fetchMoviesInfo(
                    this, this, MovieInfoFetcher.MoviesInfoType.POPULAR
            );
        }
        else {
            // Re-create from rotation, reload previously saved movie info data ...
            listMovieInfo = savedInstanceState.getParcelableArrayList(DetailActivity.KEY_BUNDLE_MOVIEINFO);
            mAdapterMovieInfo.setDataMovieInfo(listMovieInfo);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(DetailActivity.KEY_BUNDLE_MOVIEINFO, listMovieInfo);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(MovieInfo mi) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_BUNDLE_MOVIEINFO, mi);
        startActivity(intent);
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
            // For getItem index value see menu xml's orderInCategory values
            case POPULAR:
                menu.getItem(0).setEnabled(true);
                menu.getItem(1).setEnabled(false);
                break;
            case TOP_RATED:
                menu.getItem(0).setEnabled(false);
                menu.getItem(1).setEnabled(true);
                break;
            default:
                Log.e(TAG, "onPrepareOptionsMenu: Unknown movies list type! ["+ typeMoviesList +"]" );
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_top_rated:
                typeMoviesList = MovieInfoFetcher.MoviesInfoType.TOP_RATED;
                MovieInfoFetcher.fetchMoviesInfo(
                        this, this, typeMoviesList);
                break;
            case R.id.menu_popular:
                typeMoviesList = MovieInfoFetcher.MoviesInfoType.POPULAR;
                MovieInfoFetcher.fetchMoviesInfo(
                        this, this, typeMoviesList);
                break;
            default:
                Log.e(TAG, "onOptionsItemSelected: Unknown options item id! ["+ id +"]" );
        }

        invalidateOptionsMenu(); // Android 3.0+ needs this to re-call onPrepareOptionsMenu
        return super.onOptionsItemSelected(item);
    }

    /**
     * <p>Called by {@link MovieInfoFetcher} when it has completed fetching movie info.</p>
     * @param listMovieInfo An {@link ArrayList} of {@link MovieInfo} objects, containing movie info.
     */
    @Override
    public void moviesInfofetched(ArrayList<MovieInfo> listMovieInfo) {
        Log.d(TAG, "moviesInfofetched() called with: listMovieInfo = [" + listMovieInfo + "]");
        // Remember the list of MovieInfo objects ...
        this.listMovieInfo = listMovieInfo;
        // Tell the adapter to update ...
        mContentView.getLayoutManager().scrollToPosition(0);
        mAdapterMovieInfo.setDataMovieInfo(this.listMovieInfo);
        // Set the title of the app to reflect the type of movies being showing ...
        switch(typeMoviesList) {
            case POPULAR:
                setTitle(getResources().getString(R.string.app_title_popular));
                break;
            case TOP_RATED:
                setTitle(getResources().getString(R.string.app_title_top_rated));
                break;
            default:
                Log.e(TAG, "fetched: Unknown movies list type! ["+ typeMoviesList +"]" );
                break;
        }
    }

}
