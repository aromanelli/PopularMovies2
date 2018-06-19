package info.romanelli.udacity.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.romanelli.udacity.android.popularmovies.model.MovieInfo;
import info.romanelli.udacity.android.popularmovies.util.MovieInfoFetcher;

public class DetailActivity extends AppCompatActivity {

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
        // method MovieInfoFetcher.setPosterToView to restarting transition).
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

        MovieInfoFetcher.setPosterToView(this, mi, mPoster);
        mTitle.setText(mi.getTitle());
        mReleaseDate.setText(mi.getReleaseDate());
        mVoteAverage.setText(mi.getVoteAverage());
        mPlotSynopsis.setText(mi.getOverview());
    }

}
