package info.romanelli.udacity.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.romanelli.udacity.android.popularmovies.database.MovieEntry;
import info.romanelli.udacity.android.popularmovies.database.MovieModel;
import info.romanelli.udacity.android.popularmovies.database.MovieModelFactory;
import info.romanelli.udacity.android.popularmovies.network.MovieInfo;
import info.romanelli.udacity.android.popularmovies.network.MovieReviewsFetcher;
import info.romanelli.udacity.android.popularmovies.network.MovieReviewsInfo;
import info.romanelli.udacity.android.popularmovies.network.MovieVideosFetcher;
import info.romanelli.udacity.android.popularmovies.network.MovieVideosInfo;
import info.romanelli.udacity.android.popularmovies.util.AppDatabase;
import info.romanelli.udacity.android.popularmovies.util.AppExecutors;
import info.romanelli.udacity.android.popularmovies.util.AppUtil;
import info.romanelli.udacity.android.popularmovies.util.MovieDetailsAdapter;

public class DetailActivity
        extends
            AppCompatActivity
        implements
            MovieVideosFetcher.Listener,
            MovieReviewsFetcher.Listener,
            MovieDetailsAdapter.OnClickHandler {

    final static private String TAG = DetailActivity.class.getSimpleName();

    final static String KEY_BUNDLE_MOVIEINFO = "keyBundleMovieInfo";

    @BindView(R.id.ivMoviePoster)
    ImageView mPoster;

    @BindView(R.id.tvTitle)
    TextView mTitle;

    @BindView(R.id.tvReleaseYear)
    TextView mReleaseYear;

    @BindView(R.id.tvRating)
    TextView mVoteAverage;

    @BindView(R.id.tbFavorite)
    ToggleButton mFavorite;

    @BindView(R.id.tvPlotSynopsis)
    TextView mPlotSynopsis;

    @BindView(R.id.rvVidsAndReviews)
    RecyclerView mVidsAndReviews;

    private MovieDetailsAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        // Postpone activity shown transition until after poster is loaded (see
        // method MoviesInfoFetcher.setPosterToView to restarting transition).
        supportPostponeEnterTransition();

        // Override the vertical scrolling for the RecyclerView, as it is
        // inside of a ScrollView for all views in the detail activity ...
        mVidsAndReviews.setLayoutManager(
                new LinearLayoutManager(this) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                }
        );

        mAdapter = new MovieDetailsAdapter(this);
        mVidsAndReviews.setAdapter(mAdapter);

        Intent intent = getIntent();
        if (intent.hasExtra(KEY_BUNDLE_MOVIEINFO)) {
            populateUI( (MovieInfo) intent.getParcelableExtra(KEY_BUNDLE_MOVIEINFO) );
        }
        else {
            AppUtil.hideToast();
            finish();
            Log.e(TAG, "onCreate: Expected to receive a MovieInfo object!");
        }

    }

    private void populateUI(final MovieInfo movieInfo) {

        Log.d(TAG, "populateUI() called with: movieInfo = [" + movieInfo + "]");

        // Set the URL for the movie poster to the ImageView showing the poster ...
        mPoster.setTransitionName(movieInfo.getPosterURL());

        // Make calls to get videos and reviews information ...
        movieInfo.setMovieReviewsInfo(null); // null is flag for fetch not done yet
        movieInfo.setMovieVideosInfo(null); // null is flag for fetch not done yet
        MovieVideosFetcher.fetchMovieVideosInfo(this, movieInfo, this);
        MovieReviewsFetcher.fetchMovieReviewsInfo(this, movieInfo, this);

        // Set the main info (not videos/reviews) to the UI widgets ...
        mTitle.setText(movieInfo.getTitle());
        mReleaseYear.setText(movieInfo.getReleaseDateYearText());
        mVoteAverage.setText(movieInfo.getVoteAverageText());
        mPlotSynopsis.setText(movieInfo.getOverview());

        // Set the favorites flag, as well as its click listener ...
        final MovieModel model = ViewModelProviders.of(
                DetailActivity.this,
                new MovieModelFactory(
                        AppDatabase.$(getApplicationContext()),
                        movieInfo.getId()
                )
            ).get(MovieModel.class);
        model.getMovieEntry().observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry movieEntry) {
                Log.d(TAG, "model.getMovieEntry().onChanged() called with: movieEntry = [" + movieEntry + "]["+ model.getMovieEntry().getValue() +"]");
                // We only save favorite'd recs to the db, so if no entry, not a favorite ...
                // (If coming from 'Favorites' view movieEntry will be null though record exists!)
                mFavorite.setChecked( (movieEntry != null) );
            }
        });
        // Listen for the user clicking on the favorites control ...
        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called: [" + mFavorite.isChecked() + "]");
                AppExecutors.$().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

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
                        if (mFavorite.isChecked()) {
                            entry.setJson(new GsonBuilder().create().toJson(movieInfo));
                            // @Insert is OnConflictStrategy.REPLACE so will not duplicate records,
                            Log.d(TAG, "onClick() called; inserting movieEntry = ["+ entry +"]["+ model.getMovieEntry().getValue() +"]");
                            long newId = AppDatabase.$(getApplicationContext()).movieDao()
                                    .insertMovie(entry);
                            Log.d(TAG, "onClick() called: newId: " + newId);
                            Log.d(TAG, "onClick() called; inserted movieEntry = ["+ entry +"]["+ model.getMovieEntry().getValue() +"]");
                        }

                    }
                });
            }
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
        listData.add(getString(R.string.label_trailers));
        listData.addAll(movieInfo.getMovieVideosInfo());
        listData.add(getString(R.string.label_reviews));
        listData.addAll(movieInfo.getMovieReviewsInfo());
        mAdapter.setData(listData);

        // Display of this activity is postponed until code inside of setPosterToView is called!
        AppUtil.setPosterToView(this, movieInfo, mPoster);
    }

    @Override
    public void onMovieDetailsClick(Object item) {
        Log.d(TAG, "onMovieDetailsClick() called with: item = [" + item + "]");
        if (item instanceof MovieVideosInfo) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://m.youtube.com/watch?v=" + ((MovieVideosInfo) item).getKey() ));
            startActivity(intent);
        }
    }

}
