package info.romanelli.udacity.android.popularmovies.model;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import info.romanelli.udacity.android.popularmovies.R;

public class MovieInfoAdapter extends RecyclerView.Adapter<MovieInfoAdapter.MovieInfoViewHolder> {

    final static private String TAG = MovieInfoAdapter.class.getSimpleName();

    private List<MovieInfo> listMovieInfo;
    final private Context context;
    final private MovieInfoAdapterOnClickHandler clickHandler;

    public interface MovieInfoAdapterOnClickHandler {
        void onClick(final MovieInfo mi);
    }

    public MovieInfoAdapter(final Context context,
                            final MovieInfoAdapterOnClickHandler clickHander ) {
        this.context = context;
        this.clickHandler = clickHander;
    }

    public void setDataMovieInfo(final List<MovieInfo> listMovieInfo) {
        this.listMovieInfo = listMovieInfo;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieInfoViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.movieinfo_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MovieInfoViewHolder holder, int position) {
        final Uri uri = Uri.parse("http://image.tmdb.org/t/p/")
                .buildUpon()
                .appendEncodedPath("original")
                .appendEncodedPath(listMovieInfo.get(position).getPosterURL())
                .build();
        Log.d(TAG, "onBindViewHolder: Uri for poster: ["+ uri +"]");
        new Picasso.Builder(context)
                .build()
                .load(uri)
                .into(holder.ivMoviePoster);
    }

    @Override
    public int getItemCount() {
        return listMovieInfo != null ? listMovieInfo.size() : 0;
    }

    public class MovieInfoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final private ImageView ivMoviePoster;

        MovieInfoViewHolder(View itemView) {
            super(itemView);
            ivMoviePoster = itemView.findViewById(R.id.movie_poster_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                clickHandler.onClick( listMovieInfo.get( getAdapterPosition() ) );
            }
        }
    }

}
