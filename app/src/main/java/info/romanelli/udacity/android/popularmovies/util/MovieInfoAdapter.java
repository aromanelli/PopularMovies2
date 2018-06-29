package info.romanelli.udacity.android.popularmovies.util;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.romanelli.udacity.android.popularmovies.R;
import info.romanelli.udacity.android.popularmovies.network.MovieInfo;

public class MovieInfoAdapter extends RecyclerView.Adapter<MovieInfoAdapter.ViewHolder> {

    @SuppressWarnings("unused")
    final static private String TAG = MovieInfoAdapter.class.getSimpleName();

    private List<MovieInfo> listMovieInfo;
    final private OnClickHandler clickHandler;

    public MovieInfoAdapter(final OnClickHandler clickHandler ) {
        this.clickHandler = clickHandler;
    }

    public void setData(final List<MovieInfo> listMovieInfo) {
        this.listMovieInfo = (listMovieInfo != null) ? listMovieInfo : new ArrayList<MovieInfo>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.movieinfo_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final MovieInfo mi = listMovieInfo.get(position);

        // Set the transition name for the new poster being shown in the
        // common ImageView object/view ...
        ViewCompat.setTransitionName(holder.ivMoviePoster, mi.getPosterURL());

        AppUtil.setPosterToView(null, mi, holder.ivMoviePoster);
    }

    @Override
    public int getItemCount() {
        return listMovieInfo != null ? listMovieInfo.size() : 0;
    }

    public interface OnClickHandler {
        void onMovieClick(final MovieInfo mi, final ImageView ivPoster);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.movie_poster_iv)
        ImageView ivMoviePoster;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                clickHandler.onMovieClick(
                        listMovieInfo.get( getAdapterPosition() ),
                        ivMoviePoster
                );
            }
        }

    }

}
