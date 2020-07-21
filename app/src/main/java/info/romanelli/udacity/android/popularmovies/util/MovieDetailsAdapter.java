package info.romanelli.udacity.android.popularmovies.util;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.romanelli.udacity.android.popularmovies.R;
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
            holder.getTvReviewAuthor().setText(info.getAuthor());
            holder.getTvReviewText().setText(info.getContent());
        }
    }

    private void configureVideosViewHolder(VideosViewHolder holder, int position) {
        Log.d(TAG, "configureVideosViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        MovieVideosInfo info = (MovieVideosInfo) listDetails.get(position);
        if (info != null) {
            holder.getTvVideoName().setText(info.getName());
            AppUtil.setVideoThumbnailToView(info, holder.getIvVideoThumbnail());
        }
    }

    private void configureTextViewHolder(TextViewHolder holder, int position) {
        Log.d(TAG, "configureTextViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        Object info = listDetails.get(position);
        if (info != null) {
            holder.getTvMovieText().setText(info.toString());
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

        @BindView(R.id.tvMovieText)
        TextView tvMovieText;

        TextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public TextView getTvMovieText() {
            return tvMovieText;
        }

        public void setTvMovieText(TextView tvMovieText) {
            this.tvMovieText = tvMovieText;
        }
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvReviewAuthor)
        TextView tvReviewAuthor;

        @BindView(R.id.tvReviewText)
        TextView tvReviewText;

        ReviewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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

        public TextView getTvReviewAuthor() {
            return tvReviewAuthor;
        }

        public void setTvReviewAuthor(TextView tvReviewAuthor) {
            this.tvReviewAuthor = tvReviewAuthor;
        }

        public TextView getTvReviewText() {
            return tvReviewText;
        }

        public void setTvReviewText(TextView tvReviewText) {
            this.tvReviewText = tvReviewText;
        }
    }

    public class VideosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ivVideoThumbnail)
        ImageView ivVideoThumbnail;

        @BindView(R.id.tvVideoName)
        TextView tvVideoName;

        VideosViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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

        public ImageView getIvVideoThumbnail() {
            return ivVideoThumbnail;
        }

        public void setIvVideoThumbnail(ImageView ivVideoThumbnail) {
            this.ivVideoThumbnail = ivVideoThumbnail;
        }

        public TextView getTvVideoName() {
            return tvVideoName;
        }

        public void setTvVideoName(TextView tvVideoName) {
            this.tvVideoName = tvVideoName;
        }
    }


}
