package com.example.android.popularmovies.discovery;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.DataChangedListener;
import com.example.android.popularmovies.model.DiscoveryDataCache;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Adapter class for Discovery screen. Creates subviews for the Recycler view, loads movie posters and listens for clicking on them.
 */
class DiscoveryAdapter extends RecyclerView.Adapter<DiscoveryViewHolder> implements DataChangedListener {
    private static final String TAG = DiscoveryAdapter.class.getSimpleName();

    private boolean firstLoading;

    private int columns;
    private double imageHeightToWidthRatio;
    private DiscoveryDataCache discoveryData;
    private Context context;
    private RecyclerView discoveryRecyclerView;
    private OnFirstLoadingFinishedListener onFirstLoadingFinishedListener;
    private OnPosterClickedListener onPosterClickedListener;

    /**
     * @param discoveryRecyclerView Recycler view connected to the adapter.
     * @param columns Number of columns in the grid.
     * @param imageHeightToWidthRatio Image height/width ratio used to compute height of the posters.
     * @param discoveryData Data cache with discovery data.
     * @param context Activity context
     */
    DiscoveryAdapter(RecyclerView discoveryRecyclerView, int columns, double imageHeightToWidthRatio, DiscoveryDataCache discoveryData, Context context) {
        this.columns = columns;

        this.imageHeightToWidthRatio = imageHeightToWidthRatio;
        this.discoveryData = discoveryData;
        this.context = context;
        this.discoveryRecyclerView = discoveryRecyclerView;

        firstLoading = true;
    }


    @Override
    public DiscoveryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout itemView = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.discovery_item, parent, false);

        if (parent.getWidth() != 0) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (parent.getWidth() / columns * imageHeightToWidthRatio));
            itemView.setLayoutParams(params);
        }

        Log.v(MainDiscoveryScreen.TAG, "onCreateViewHolder");

        DiscoveryViewHolder holder = new DiscoveryViewHolder(itemView);
        holder.setOnPosterClickedListener(onPosterClickedListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(DiscoveryViewHolder holder, int position) {
        final DiscoveryViewHolder h = holder;
        Log.d(MainDiscoveryScreen.TAG, "onBindHolder: " + position);
        if (discoveryData.getCount() > position) {
            h.loadingNextPageSpinner.setVisibility(View.VISIBLE);
            h.errorLoadingImageView.setVisibility(View.INVISIBLE);

            JSONObject item = discoveryData.getItem(position);
            if (item != null) {
                String posterUrl = item.optString("poster_path");
                if (posterUrl != null && !"null".equals(posterUrl)) {
                    Uri imageUri = NetworkUtils.buildImageUri(posterUrl);
                    Log.d(TAG, "Loading image " + imageUri.toString());
                    Picasso.with(context)
                            .load(imageUri)
                            .into(holder.movieImageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    h.loadingNextPageSpinner.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onError() {
                                    h.loadingNextPageSpinner.setVisibility(View.INVISIBLE);
                                    h.errorLoadingImageView.setVisibility(View.VISIBLE);
                                }
                            });
                } else {
                    Picasso.with(context).cancelRequest(holder.movieImageView);
                    Log.v(TAG, "Null image url");
                }
            } else {
                holder.loadingNextPageSpinner.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public int getItemCount() {
        if (discoveryData == null)
            return 0;
        else return discoveryData.getCount();
    }

    @Override
    public void onDataChanged() {
        if (firstLoading && discoveryData.getCount() > 0) {
            firstLoading = false;

            if(onFirstLoadingFinishedListener != null)
                onFirstLoadingFinishedListener.onAfterFirstLoad();
        }
        discoveryRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    /**
     * Sets the listener that is triggered after loading first data from API.
     * @param onFirstLoadingFinishedListener Listener
     */
    void setOnFirstLoadingFinishedListener(OnFirstLoadingFinishedListener onFirstLoadingFinishedListener) {
        this.onFirstLoadingFinishedListener = onFirstLoadingFinishedListener;
    }

    /**
     * Sets the listener for clicking on the posters.
     * @param onPosterClickedListener Listener
     */
    void setOnPosterClickedListener(OnPosterClickedListener onPosterClickedListener) {
        this.onPosterClickedListener = onPosterClickedListener;
    }
}
