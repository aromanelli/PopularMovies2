package info.romanelli.udacity.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import info.romanelli.udacity.android.popularmovies.model.MovieInfo;
import info.romanelli.udacity.android.popularmovies.util.MovieInfoFetcher;

public class DetailActivity extends AppCompatActivity {

    final static private String TAG = MainActivity.class.getSimpleName();

    final static String KEY_BUNDLE_MOVIEINFO = "keyBundleMovieInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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
        MovieInfoFetcher.setPosterToView(mi, ((ImageView) findViewById(R.id.movie_poster_iv) ));
        ((TextView) findViewById(R.id.textTitle)).setText(mi.getTitle());
        ((TextView) findViewById(R.id.textReleaseDate)).setText(mi.getReleaseDate());
        ((TextView) findViewById(R.id.textVoteAvg)).setText(mi.getVoteAverage());
        ((TextView) findViewById(R.id.textPlotSynopsis)).setText(mi.getOverview());
    }

}
