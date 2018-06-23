package info.romanelli.udacity.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.romanelli.udacity.android.popularmovies.model.MovieInfo;
import info.romanelli.udacity.android.popularmovies.model.MovieReviewsInfo;
import info.romanelli.udacity.android.popularmovies.model.MovieVideosInfo;
import info.romanelli.udacity.android.popularmovies.util.InfoFetcherUtil;
import info.romanelli.udacity.android.popularmovies.util.MovieReviewsFetcher;
import info.romanelli.udacity.android.popularmovies.util.MovieVideosFetcher;

public class DetailActivity
        extends AppCompatActivity
        implements MovieVideosFetcher.Listener, MovieReviewsFetcher.Listener {

    final static private String TAG = MainActivity.class.getSimpleName();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        // Postpone activity shown transition until after poster is loaded (see
        // method MoviesInfoFetcher.setPosterToView to restarting transition).
        supportPostponeEnterTransition();

        Intent intent = getIntent();
        if (intent.hasExtra(KEY_BUNDLE_MOVIEINFO)) {
            populateUI((MovieInfo) intent.getParcelableExtra(KEY_BUNDLE_MOVIEINFO));
        }
        else {
            InfoFetcherUtil.hideToast();
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

        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called: [" + mFavorite.isChecked() + "]");
                if (mFavorite.isChecked()) {
                    // TODO AOR Implement 'Favorites' functionality!
                }
            }
        });

        mPlotSynopsis.setText(movieInfo.getOverview());
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

        InfoFetcherUtil.hideToast();

        // TODO AOR Set the videos and reviews info into the UI

        // Display of this activity is postponed until code inside of setPosterToView is called!
        InfoFetcherUtil.setPosterToView(this, movieInfo, mPoster);

    }

}
