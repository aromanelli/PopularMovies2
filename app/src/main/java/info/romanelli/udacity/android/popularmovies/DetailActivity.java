package info.romanelli.udacity.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

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

    @BindView(R.id.movie_poster_iv)
    ImageView mPoster;

    @BindView(R.id.textTitle)
    TextView mTitle;

    @BindView(R.id.textReleaseDate)
    TextView mReleaseDate;

    @BindView(R.id.textVoteAvg)
    TextView mVoteAverage;

    @BindView(R.id.textPlotSynopsis)
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
            finish();
            Log.e(TAG, "onCreate: Expected to receive a MovieInfo object!");
        }

    }

    private void populateUI(final MovieInfo mi) {
        Log.d(TAG, "populateUI() called with: movieInfo = [" + mi + "]");

        // Set the URL for the movie poster to the ImageView showing the poster ...
        mPoster.setTransitionName(mi.getPosterURL());

        InfoFetcherUtil.setPosterToView(this, mi, mPoster);
        mTitle.setText(mi.getTitle());
        mReleaseDate.setText(mi.getReleaseDate());
        mVoteAverage.setText(
                String.format(Locale.getDefault(), "%s", mi.getVoteAverage())
        );
        mPlotSynopsis.setText(mi.getOverview());

        // Make calls to get videos and reviews information ...
        MovieVideosFetcher.fetchMovieVideosInfo(this, mi.getId(), this);
        MovieReviewsFetcher.fetchMovieReviewsInfo(this, mi.getId(), this);
    }

    @Override
    public void fetchedMovieVideosInfo(ArrayList<MovieVideosInfo> listMovieVideosInfo) {
        Log.d(TAG, "fetchedMovieVideosInfo() called with: listMovieVideosInfo = [" + listMovieVideosInfo + "]");
    }

    @Override
    public void fetchedMovieReviewsInfo(ArrayList<MovieReviewsInfo> listMovieReviewsInfo) {
        Log.d(TAG, "fetchedMovieReviewsInfo() called with: listMovieReviewsInfo = [" + listMovieReviewsInfo + "]");
    }
}
