package com.example.android.popularmovies.discovery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.android.popularmovies.Constants;
import com.example.android.popularmovies.PopularMoviesApplication;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.cache.DataCache;
import com.example.android.popularmovies.model.RequestFailedEvent;
import com.example.android.popularmovies.model.RequestFailedListener;
import com.example.android.popularmovies.moviedetail.MovieDetailActivity;
import com.example.android.popularmovies.utilities.CommonUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Generalization of the discovery grid fragments.
 */
public abstract class DiscoveryGridFragment extends Fragment implements OnPosterClickedListener, OnFirstLoadingFinishedListener, RequestFailedListener {
    private static final String TAG = "DiscoveryGridFragment";

    private static final String DISCOVERY_DATA_CACHE_SAVE_STATE_KEY = "DISCOVERY_CACHE_SAVE_STATE_KEY";
    private static final String SCROLL_POSITION_SAVE_STATE_KEY = "SCROLL_POSITION_SAVE_STATE_KEY";
    private static final String SORT_BY_SAVE_STATE_KEY = "SORT_BY_SAVE_STATE_KEY";

    /**
     * Sorting orders
     */
    private enum SortBy{
        POPULARITY, RATING
    }

    protected SortBy sortBy;

    /**
     * Poster height/width ratio to set dynamically height of the posters.
     */
    public static final double IMAGE_HEIGHT_TO_WIDTH_RATIO = 1.5;

    protected int columns;



    @BindView(R.id.rv_discovery_list)
    protected RecyclerView mDiscoveryRecyclerView;
    protected DiscoveryAdapter adapter;

    @BindView(R.id.pb_first_loading)
    protected ProgressBar mFirstLoadingProgressBar;

    protected LoadNextPageScrollListener onScrollListener;
    protected GridLayoutManager layoutManager;

    protected DataCache discoveryData;

    public DiscoveryGridFragment() {
        // Required empty public constructor
    }

    /**
     * Gets the data cache used in the fragment.
     * @return Data cache.
     */
    abstract DataCache getDataCache();

    /**
     * Restores data cache from the bundle
     * @param bundle Bundle with stored data cache.
     * @param context Context
     * @param loaderManager LoaderManager
     * @return Data cache
     */
    abstract DataCache restoreDataCacheFromBundle(Bundle bundle, Context context, LoaderManager loaderManager);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_discovery_grid, container, false);
        ButterKnife.bind(this, view);

        columns = CommonUtils.calculateNoOfColumns(getContext());

        layoutManager = new GridLayoutManager(getContext(), columns);

        //Try to restore the saved information during onSaveInstanceState
        if(savedInstanceState != null){
            sortBy = SortBy.valueOf(savedInstanceState.getString(SORT_BY_SAVE_STATE_KEY));
            this.discoveryData = restoreDataCacheFromBundle(savedInstanceState.getBundle(DISCOVERY_DATA_CACHE_SAVE_STATE_KEY), getContext(), getLoaderManager());

            this.onAfterFirstLoad();
        }

        //Crete new default values when no SaveInstanceState or defective
        if(sortBy == null) {
            sortBy = SortBy.POPULARITY;
        }
        if(discoveryData == null){
            discoveryData = getDataCache();
        }


        adapter = new DiscoveryAdapter(mDiscoveryRecyclerView, columns, IMAGE_HEIGHT_TO_WIDTH_RATIO, discoveryData, getContext());

        discoveryData.setDataChangedListener(adapter);
        discoveryData.setRequestFailedListener(this);


        adapter.setOnFirstLoadingFinishedListener(this);
        adapter.setOnPosterClickedListener(this);
        mDiscoveryRecyclerView.setAdapter(adapter);

        mDiscoveryRecyclerView.setHasFixedSize(true);
        mDiscoveryRecyclerView.setLayoutManager(layoutManager);

        onScrollListener = new LoadNextPageScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                discoveryData.loadNextPage();
            }
        };
        mDiscoveryRecyclerView.addOnScrollListener(onScrollListener);

        /*
          Scroll to the saved position
         */
        if(savedInstanceState != null){
            int scrollPosition = savedInstanceState.getInt(SCROLL_POSITION_SAVE_STATE_KEY);
            if(scrollPosition != RecyclerView.NO_POSITION) {
                mDiscoveryRecyclerView.scrollToPosition(scrollPosition);
            }
        }

        //if(((DiscoveryActivity) getActivity()).isNetworkAvailable()) {
            discoveryData.loadNextPage();
        //}

        return view;
    }

    /**
     * Listens to the poster clicks. Starts new activity and passes movie information to it.
     * @param position Index of the clicked item.
     */
    @Override
    public void onPosterClicked(int position) {
        Log.v(TAG, "Poster clicked " + position);

        Intent movieDetailsIntent = new Intent(getContext(), MovieDetailActivity.class);

        Gson gson = PopularMoviesApplication.getGson();
        movieDetailsIntent.putExtra(Constants.MOVIE_DETAILS_EXTRA, gson.toJson(discoveryData.getItem(position)));
        startActivity(movieDetailsIntent);

    }

    /**
     * Tryes request again.
     */
    public void tryAgain(){
        discoveryData.loadNextPage();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(sortBy != null) {
            outState.putString(SORT_BY_SAVE_STATE_KEY, sortBy.name());
        }
        if(discoveryData != null){
            outState.putBundle(DISCOVERY_DATA_CACHE_SAVE_STATE_KEY, discoveryData.saveToBundle());
        }
        if(mDiscoveryRecyclerView != null && mDiscoveryRecyclerView.getLayoutManager() != null) {
            int scrollPosition = ((GridLayoutManager) mDiscoveryRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            outState.putInt(SCROLL_POSITION_SAVE_STATE_KEY, scrollPosition);
        }

        super.onSaveInstanceState(outState);

    }

    @Override
    public void onAfterFirstLoad() {
        mFirstLoadingProgressBar.setVisibility(View.GONE);
        mDiscoveryRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestTimeout() {
        onRequestFailed();
    }

    @Override
    public void onRequestFailed() {
        EventBus.getDefault().post(new RequestFailedEvent());
    }

    /**
     * LoadNextPageScrollListener class used to listen on RecyclerView scrolling and triggers the download of new page of movies if nearby the end of the list.
     *
     * Based on "Endless RecyclerView OnScrollListener" created by WoongBi Kim (https://github.com/ssinss), available at "https://gist.github.com/ssinss/e06f12ef66c51252563e"
     */
    abstract class LoadNextPageScrollListener extends RecyclerView.OnScrollListener {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 20;
        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        GridLayoutManager mLayoutManager;

        LoadNextPageScrollListener(GridLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        @Override
        public void onScrolled(RecyclerView view, int dx, int dy) {
            int lastVisibleItemPosition;
            int totalItemCount = mLayoutManager.getItemCount();

            lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }
            // If it’s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }

            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            // threshold should reflect how many total smallWidthColumns there are too
            if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                currentPage++;
                onLoadMore(currentPage, totalItemCount, view);
                loading = true;
            }
        }

        // Call this method whenever performing new searches
        void resetState() {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = 0;
            this.loading = true;
        }

        // Defines the process for actually loading more data based on page
        public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);
    }

}
