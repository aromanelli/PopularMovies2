package info.romanelli.udacity.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.romanelli.udacity.android.popularmovies.database.MovieEntry;
import info.romanelli.udacity.android.popularmovies.database.MovieModel;
import info.romanelli.udacity.android.popularmovies.database.MovieModelFactory;
import info.romanelli.udacity.android.popularmovies.databinding.ActivityDetailBinding;
import info.romanelli.udacity.android.popularmovies.network.MovieInfo;
import info.romanelli.udacity.android.popularmovies.network.MovieReviewsFetcher;
import info.romanelli.udacity.android.popularmovies.network.MovieReviewsInfo;
import info.romanelli.udacity.android.popularmovies.network.MovieVideosFetcher;
import info.romanelli.udacity.android.popularmovies.network.MovieVideosInfo;
import info.romanelli.udacity.android.popularmovies.util.AppDatabase;
import info.romanelli.udacity.android.popularmovies.util.AppExecutors;
import info.romanelli.udacity.android.popularmovies.util.AppUtil;
import info.romanelli.udacity.android.popularmovies.util.MovieDetailsAdapter;
import info.romanelli.udacity.android.popularmovies.util.NetUtil;

public class DetailActivity
        extends
            AppCompatActivity
        implements
            MovieVideosFetcher.Listener,
            MovieReviewsFetcher.Listener,
            MovieDetailsAdapter.OnClickHandler {

    final static private String TAG = DetailActivity.class.getSimpleName();

    final static String KEY_BUNDLE_MOVIEINFO = "keyBundleMovieInfo";
    final static private String KEY_SCROLL_POS = "SCROLL_POSITION";

    static private int SCROLL_X = 0;
    static private int SCROLL_Y = 0;

    private ActivityDetailBinding binding;

    private MovieDetailsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        NetUtil.registerForNetworkMonitoring(this);

        if (!NetUtil.ifConnected(this.findViewById(R.id.rvVidsAndReviews))) {
            // finish();
            return;
        }

        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Postpone activity shown transition until after poster is loaded
        // (see method AppUtil.setPosterToView to restarting transition).
        supportPostponeEnterTransition();

        // Override the vertical scrolling for the RecyclerView, as it is
        // inside of a ScrollView for all views in the detail activity ...
        binding.rvVidsAndReviews.setLayoutManager(
                new LinearLayoutManager(this) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                }
        );

        mAdapter = new MovieDetailsAdapter(this);
        binding.rvVidsAndReviews.setAdapter(mAdapter);

        Intent intent = getIntent();
        if (intent.hasExtra(KEY_BUNDLE_MOVIEINFO)) {
            populateUI((MovieInfo) intent.getParcelableExtra(KEY_BUNDLE_MOVIEINFO));
        } else {
            Log.e(TAG, "onCreate: Expected to receive a MovieInfo object!");
            AppUtil.hideToast();
            finish();
        }

    }

    private void populateUI(final MovieInfo movieInfo) {

        Log.d(TAG, "populateUI() called with: movieInfo = [" + movieInfo + "]");

        // Set the URL for the movie poster to the ImageView showing the poster ...
        binding.ivMoviePoster.setTransitionName(movieInfo.getPosterURL());

        // Make calls to get videos and reviews information ...
        movieInfo.setMovieReviewsInfo(null); // null is flag for fetch not done yet
        movieInfo.setMovieVideosInfo(null); // null is flag for fetch not done yet
        MovieVideosFetcher.fetchMovieVideosInfo(this, movieInfo, this);
        MovieReviewsFetcher.fetchMovieReviewsInfo(this, movieInfo, this);

        // Set the main info (not videos/reviews) to the UI widgets ...
        binding.tvTitle.setText(movieInfo.getTitle());
        binding.tvReleaseYear.setText(movieInfo.getReleaseDateYearText());
        binding.tvRating.setText(movieInfo.getVoteAverageText());
        binding.tvPlotSynopsis.setText(movieInfo.getOverview());

        // Set the favorites flag, as well as its click listener ...
        final MovieModel model = new ViewModelProvider(
                DetailActivity.this,
                new MovieModelFactory(
                        AppDatabase.$(getApplicationContext()),
                        movieInfo.getId()
                )
            ).get(MovieModel.class);
        model.getMovieEntry().observe(this, movieEntry -> {
            Log.d(TAG, "model.getMovieEntry().onChanged() called with: movieEntry = [" + movieEntry + "]["+ model.getMovieEntry().getValue() +"]");
            // We only save favorite'd recs to the db, so if no entry, not a favorite ...
            // (If coming from 'Favorites' view movieEntry will be null though record exists!)
            binding.tbFavorite.setChecked( (movieEntry != null) );
        });
        // Listen for the user clicking on the favorites control ...
        binding.tbFavorite.setOnClickListener(v -> {
            Log.d(TAG, "onClick() called: [" + binding.tbFavorite.isChecked() + "]");
            AppExecutors.$().diskIO().execute(() -> {

                MovieEntry entry = new MovieEntry(
                        movieInfo.getId(), // MovieInfo id, NOT MovieEntry id!
                        null
                );

                // We only save favorites to the db, to conserve resources,
                // so first, we delete the old record, no matter what ...
                Log.d(TAG, "onClick() called; deleting movieEntry = [" + entry + "]");
                AppDatabase.$(getApplicationContext()).movieDao()
                        .deleteMovie(entry);

                // ... and then if the user has pressed the favorite, we add it to the db ...
                if (binding.tbFavorite.isChecked()) {
                    entry.setJson(new GsonBuilder().create().toJson(movieInfo));
                    // @Insert is OnConflictStrategy.REPLACE so will not duplicate records,
                    Log.d(TAG, "onClick() called; inserting movieEntry = ["+ entry +"]["+ model.getMovieEntry().getValue() +"]");
                    long newId = AppDatabase.$(getApplicationContext()).movieDao()
                            .insertMovie(entry);
                    Log.d(TAG, "onClick() called: newId: " + newId);
                    Log.d(TAG, "onClick() called; inserted movieEntry = ["+ entry +"]["+ model.getMovieEntry().getValue() +"]");
                }

            });
        });

    }

    @Override
    public void fetchedMovieVideosInfo(MovieInfo movieInfo,
                                       ArrayList<MovieVideosInfo> listMovieVideosInfo) {
        Log.d(TAG, "fetchedMovieVideosInfo() called with: movieInfo = [" + movieInfo +
                "], listMovieVideosInfo = [" + listMovieVideosInfo + "]");

        // Update the MovieInfo reference with the videos info ...
        movieInfo.setMovieVideosInfo(listMovieVideosInfo);

        // If both videos and reviews data has been fetched, show the detail activity UI ...
        if ((movieInfo.getMovieReviewsInfo() != null) && (movieInfo.getMovieVideosInfo() != null)) {
            finalizeUI(movieInfo);
        }
    }

    @Override
    public void fetchedMovieReviewsInfo(MovieInfo movieInfo,
                                        ArrayList<MovieReviewsInfo> listMovieReviewsInfo) {
        Log.d(TAG, "fetchedMovieReviewsInfo() called with: movieInfo = [" + movieInfo +
                "], listMovieReviewsInfo = [" + listMovieReviewsInfo + "]");

        // Update the MovieInfo reference with the reviews info ...
        movieInfo.setMovieReviewsInfo(listMovieReviewsInfo);

        // If both videos and reviews data has been fetched, show the detail activity UI ...
        if ((movieInfo.getMovieReviewsInfo() != null) && (movieInfo.getMovieVideosInfo() != null)) {
            finalizeUI(movieInfo);
        }
    }

    private void finalizeUI(final MovieInfo movieInfo) {

        Log.d(TAG, "finalizeUI() called with: movieInfo = [" + movieInfo + "]");

        AppUtil.hideToast();

        // We have the videos and reviews, so add them to their adapter ...
        List<Object> listData = new ArrayList<>(
                movieInfo.getMovieVideosInfo().size() +
                        movieInfo.getMovieReviewsInfo().size()
        );
        if (movieInfo.getMovieVideosInfo().size() >= 1) {
            listData.add(getString(R.string.label_trailers));
            listData.addAll(movieInfo.getMovieVideosInfo());
        }
        if (movieInfo.getMovieReviewsInfo().size() >= 1) {
            listData.add(getString(R.string.label_reviews));
            listData.addAll(movieInfo.getMovieReviewsInfo());
        }
        mAdapter.setData(listData);

        // Display of this activity is postponed until code inside of setPosterToView is called!
        AppUtil.setPosterToView(this, movieInfo, binding.ivMoviePoster);

        // Restore the previous scroll location ...
        binding.detailscrollview.post(() -> binding.detailscrollview.scrollTo(SCROLL_X, SCROLL_Y));

    }

    @Override
    public void onMovieDetailsClick(Object item) {
        Log.d(TAG, "onMovieDetailsClick() called with: item = [" + item + "]");
        if (item instanceof MovieVideosInfo) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://m.youtube.com/watch?v=" + ((MovieVideosInfo) item).getKey() ));

            // Verify Intent can actually be resolved/started, and start it ...
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            else {
                Log.w(TAG, "No Intent available to handle action");
                AppUtil.showToast(this, getString(R.string.msg_no_intent), false);
            }

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d(TAG, "onSaveInstanceState() called with: outState = [" + outState + "]["+
                binding.detailscrollview.getScrollX() +"]["+ binding.detailscrollview.getScrollY() +"]");
        super.onSaveInstanceState(outState);
        outState.putIntArray(
                KEY_SCROLL_POS,
                new int[]{
                        binding.detailscrollview.getScrollX(),
                        binding.detailscrollview.getScrollY()
                }
        );
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray(KEY_SCROLL_POS);
        Log.d(TAG, "onRestoreInstanceState: position: " + Arrays.toString(position));
        if(position != null) {
            SCROLL_X = position[0];
            SCROLL_Y = position[1];
        }
    }

}
