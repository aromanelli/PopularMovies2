package info.romanelli.udacity.android.popularmovies.model;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import info.romanelli.udacity.android.popularmovies.R;
import info.romanelli.udacity.android.popularmovies.util.MovieInfoFetcher;

public class MovieInfoAdapter extends RecyclerView.Adapter<MovieInfoAdapter.MovieInfoViewHolder> {

    @SuppressWarnings("unused")
    final static private String TAG = MovieInfoAdapter.class.getSimpleName();

    private List<MovieInfo> listMovieInfo;
    final private MovieInfoAdapterOnClickHandler clickHandler;

    public interface MovieInfoAdapterOnClickHandler {
        void onClick(final MovieInfo mi);
    }

    public MovieInfoAdapter(final MovieInfoAdapterOnClickHandler clickHandler ) {
        this.clickHandler = clickHandler;
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
        MovieInfoFetcher.setPosterToView(listMovieInfo.get(position), holder.ivMoviePoster);
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
