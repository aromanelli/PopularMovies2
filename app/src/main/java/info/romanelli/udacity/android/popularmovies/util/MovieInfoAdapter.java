package info.romanelli.udacity.android.popularmovies.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import info.romanelli.udacity.android.popularmovies.databinding.MovieinfoItemBinding;
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
        this.listMovieInfo = (listMovieInfo != null) ? listMovieInfo : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                MovieinfoItemBinding.inflate(
                        LayoutInflater.from(parent.getContext())
                ).getRoot()
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final MovieInfo mi = listMovieInfo.get(position);

        // Set the transition name for the new poster being shown in the
        // common ImageView object/view ...
        ViewCompat.setTransitionName(holder.binding.moviePosterIv, mi.getPosterURL());

        AppUtil.setPosterToView(null, mi, holder.binding.moviePosterIv);
    }

    @Override
    public int getItemCount() {
        return listMovieInfo != null ? listMovieInfo.size() : 0;
    }

    public interface OnClickHandler {
        void onMovieClick(final MovieInfo mi, final ImageView ivPoster);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MovieinfoItemBinding binding;

        ViewHolder(View itemView) {
            super(itemView);
            binding = MovieinfoItemBinding.bind(itemView); // TODO Best way to get binding ref?
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                clickHandler.onMovieClick(
                        listMovieInfo.get( getAdapterPosition() ),
                        binding.moviePosterIv
                );
            }
        }

    }

}
