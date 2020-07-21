package info.romanelli.udacity.android.popularmovies.util;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import info.romanelli.udacity.android.popularmovies.R;
import info.romanelli.udacity.android.popularmovies.databinding.MovieReviewsItemBinding;
import info.romanelli.udacity.android.popularmovies.databinding.MovieTextItemBinding;
import info.romanelli.udacity.android.popularmovies.databinding.MovieVideosItemBinding;
import info.romanelli.udacity.android.popularmovies.network.MovieReviewsInfo;
import info.romanelli.udacity.android.popularmovies.network.MovieVideosInfo;

public class MovieDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final static private String TAG = MovieDetailsAdapter.class.getSimpleName();

    final static private int REVIEW = 100;
    final static private int VIDEO = 200;
    final static private int TEXT = 300;

    private List<Object> listDetails;
    final private OnClickHandler clickHandler;

    public MovieDetailsAdapter(final OnClickHandler clickHandler) {
        Log.d(TAG, "MovieDetailsAdapter() called with: clickHandler = [" + clickHandler + "]");
        this.clickHandler = clickHandler;
    }

    public void setData(final List<Object> listDetails) {
        Log.d(TAG, "setData() called with: listDetails = [" + listDetails + "]");
        this.listDetails = (listDetails != null) ? listDetails : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder() called with: parent = [" + parent + "], viewType = [" + viewType + "]");

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch(viewType) {
            case REVIEW:
                viewHolder = new ReviewsViewHolder(
                        inflater.inflate(R.layout.movie_reviews_item, parent, false)
                );
                break;
            case VIDEO:
                viewHolder = new VideosViewHolder(
                        inflater.inflate(R.layout.movie_videos_item, parent, false)
                );
                break;
            case TEXT:
                viewHolder = new TextViewHolder(
                        inflater.inflate(R.layout.movie_text_item, parent, false)
                );
                break;
            default:
                Log.e(TAG, "onCreateViewHolder: " + viewType);
                throw new IllegalStateException("onCreateViewHolder");
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        switch(holder.getItemViewType()) {
            case REVIEW:
                configureReviewsViewHolder( ((ReviewsViewHolder) holder), position );
                break;
            case VIDEO:
                configureVideosViewHolder( ((VideosViewHolder) holder), position );
                break;
            case TEXT:
                configureTextViewHolder( ((TextViewHolder) holder), position);
                break;
            default:
                Log.e(TAG, "onBindViewHolder: " + holder.getItemViewType() );
                throw new IllegalStateException("onBindViewHolder");
        }
    }

    private void configureReviewsViewHolder(ReviewsViewHolder holder, int position) {
        Log.d(TAG, "configureReviewsViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        MovieReviewsInfo info = (MovieReviewsInfo) listDetails.get(position);
        if (info != null) {
            holder.binding.tvReviewAuthor.setText(info.getAuthor());
            holder.binding.tvReviewText.setText(info.getContent());
        }
    }

    private void configureVideosViewHolder(VideosViewHolder holder, int position) {
        Log.d(TAG, "configureVideosViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        MovieVideosInfo info = (MovieVideosInfo) listDetails.get(position);
        if (info != null) {
            holder.binding.tvVideoName.setText(info.getName());
            AppUtil.setVideoThumbnailToView(info, holder.binding.ivVideoThumbnail);
        }
    }

    private void configureTextViewHolder(TextViewHolder holder, int position) {
        Log.d(TAG, "configureTextViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        Object info = listDetails.get(position);
        if (info != null) {
            holder.binding.tvMovieText.setText(info.toString());
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount() called ["+ ((listDetails != null) ? listDetails.size() : 0) +"]");
        return (listDetails != null) ? listDetails.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        final Object o = listDetails.get(position);
        if (o instanceof MovieReviewsInfo) {
            return REVIEW;
        }
        else if (o instanceof MovieVideosInfo) {
            return VIDEO;
        }
        else {
            return TEXT;
        }
    }

    public interface OnClickHandler {
        void onMovieDetailsClick(final Object item);
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {

        private MovieTextItemBinding binding;

        TextViewHolder(View itemView) {
            super(itemView);
            binding = MovieTextItemBinding.bind(itemView); // TODO Best way to get binding ref?
        }

    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MovieReviewsItemBinding binding;

        ReviewsViewHolder(View itemView) {
            super(itemView);
            binding = MovieReviewsItemBinding.bind(itemView); // TODO Best way to get binding ref?
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Log.d(TAG, "onClick() called with: pos = ["+ pos +"], v = [" + v + "]");
            if (pos != RecyclerView.NO_POSITION) {
                clickHandler.onMovieDetailsClick(
                        listDetails.get( getAdapterPosition() )
                );
            }
        }

    }

    public class VideosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private MovieVideosItemBinding binding;

        VideosViewHolder(View itemView) {
            super(itemView);
            binding = MovieVideosItemBinding.bind(itemView); // TODO Best way to get binding ref?
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Log.d(TAG, "onClick() called with: pos = ["+ pos +"], v = [" + v + "]");
            if (pos != RecyclerView.NO_POSITION) {
                clickHandler.onMovieDetailsClick(
                        listDetails.get( getAdapterPosition() )
                );
            }
        }

    }


}
