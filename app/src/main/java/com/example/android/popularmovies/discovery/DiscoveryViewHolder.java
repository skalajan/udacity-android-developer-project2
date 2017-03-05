package com.example.android.popularmovies.discovery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.android.popularmovies.R;

/**
 * View holder class for the poster images in the RecyclerView.
 */
class DiscoveryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ProgressBar loadingNextPageSpinner;
    ImageView movieImageView;
    ImageView errorLoadingImageView;

    private OnPosterClickedListener onPosterClickedListener;


    DiscoveryViewHolder(View itemView) {
        super(itemView);

        loadingNextPageSpinner = (ProgressBar) itemView.findViewById(R.id.pb_loading_next_page);
        movieImageView = (ImageView) itemView.findViewById(R.id.iv_movie_image);
        errorLoadingImageView = (ImageView) itemView.findViewById(R.id.iv_loading_poster_error);

        itemView.setOnClickListener(this);
    }

    void setOnPosterClickedListener(OnPosterClickedListener onPosterClickedListener) {
        this.onPosterClickedListener = onPosterClickedListener;
    }

    @Override
    public void onClick(View v) {
        int itemPosition = getAdapterPosition();

        if (onPosterClickedListener != null)
            onPosterClickedListener.onPosterClicked(itemPosition);
    }
}
