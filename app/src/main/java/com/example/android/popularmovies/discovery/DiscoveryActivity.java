package com.example.android.popularmovies.discovery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.popularmovies.Constants;
import com.example.android.popularmovies.model.DiscoveryDataCache;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.moviedetail.MovieDetailActivity;
import com.example.android.popularmovies.utilities.CommonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscoveryActivity extends NetworkAccessingActivity implements OnPosterClickedListener, OnFirstLoadingFinishedListener {
    public static final String TAG = DiscoveryActivity.class.getName();

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

    protected DiscoveryDataCache discoveryData;

    /**
     * Sorting orders
     */
    private enum SortBy{
        POPULARITY, RATING
    }

    protected SortBy sortBy;


    private static final String SORT_BY_SAVE_STATE_KEY = "SORT_BY_SAVE_STATE_KEY";
    private static final String DISCOVERY_DATA_CACHE_SAVE_STATE_KEY = "DISCOVERY_CACHE_SAVE_STATE_KEY";
    private static final String SCROLL_POSITION_SAVE_STATE_KEY = "SCROLL_POSITION_SAVE_STATE_KEY";


    /**
     * Saves the states of the activity and data cache before the activity is destroyed.
     * @param outState Output bundle with stored data.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
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

    /**
     * Initializes the activity, sets the number of columns according to screen width and setups the Recycler view.
     * @param savedInstanceState Saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main_discovery_screen);

        super.onCreate(savedInstanceState);

        //Check whether the API KEY is present, else do not further initialize and show toast
        if(Constants.API_KEY == null) {
            Toast.makeText(this, R.string.no_api_key_message, Toast.LENGTH_LONG).show();
            return;
        }

        ButterKnife.bind(this);

        int screenWidthDp = getResources().getConfiguration().screenWidthDp;
        columns = CommonUtils.calculateNoOfColumns(this);


        layoutManager = new GridLayoutManager(this, columns);

        //Try to restore the saved information during onSaveInstanceState
        if(savedInstanceState != null){
            sortBy = SortBy.valueOf(savedInstanceState.getString(SORT_BY_SAVE_STATE_KEY));

            try {
                this.discoveryData = DiscoveryDataCache.restoreFromBundle(savedInstanceState.getBundle(DISCOVERY_DATA_CACHE_SAVE_STATE_KEY));
            } catch (JSONException e) {
                Log.e(TAG, "Could not restore discovery data cache");
                e.printStackTrace();
            }
        }

        //Crete new default values when no SaveInstanceState or defective
        if(sortBy == null) {
            sortBy = SortBy.POPULARITY;
        }
        if(discoveryData == null){
            discoveryData = new DiscoveryDataCache(NetworkUtils.POPULAR_MOVIES_SUFFIX);
        }


        adapter = new DiscoveryAdapter(mDiscoveryRecyclerView, columns, IMAGE_HEIGHT_TO_WIDTH_RATIO, discoveryData, this);
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

        /**
         * Scroll to the saved position
         */
        if(savedInstanceState != null){
            int scrollPosition = savedInstanceState.getInt(SCROLL_POSITION_SAVE_STATE_KEY);
            if(scrollPosition != RecyclerView.NO_POSITION) {
                mDiscoveryRecyclerView.scrollToPosition(scrollPosition);
            }
        }


        if(isNetworkAvailable()) {
            discoveryData.loadNextPage();
        }
    }

    /**
     * Inflates the menu with sorting button and sets its title
     * @param menu Menu to be inflated
     * @return True to show the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        updateSortByMenuItemTitle(menu.findItem(R.id.sort_by_menu_item));

        return true;
    }

    /**
     * Listens to click on sorting button.
     * @param item Clicked menu item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.sort_by_menu_item) {
            switchSorting();
            updateSortByMenuItemTitle(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Listens to the poster clicks. Starts new activity and passes movie information to it.
     * @param position Index of the clicked item.
     */
    @Override
    public void onPosterClicked(int position) {
        Log.v(TAG, "Poster clicked " + position);

        Intent movieDetailsIntent = new Intent(this, MovieDetailActivity.class);
        try {
            movieDetailsIntent.putExtra(Constants.MOVIE_DETAILS_EXTRA, discoveryData.getItem(position).toString());
            startActivity(movieDetailsIntent);
        } catch (JSONException e) {
            Log.e(TAG, "Error during getting clicked movie data");
            e.printStackTrace();
            Toast.makeText(this, "Couldn't get the movie detail data", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Hides initial progress bar after first data loaded.
     */
    @Override
    public void onAfterFirstLoad() {
        mFirstLoadingProgressBar.setVisibility(View.GONE);
        mDiscoveryRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Tries again to load next page.
     */
    @Override
    protected void tryRequestAgain() {
        discoveryData.loadNextPage();
    }

    /**
     * Switches sorting methods and the title of the menu item.
     */
    protected void switchSorting() {
        String sortSuffix;
        if(sortBy == SortBy.POPULARITY) {
            sortBy = SortBy.RATING;
            sortSuffix = NetworkUtils.TOP_RATED_MOVIES_SUFFIX;
        }else{
            sortBy = SortBy.POPULARITY;
            sortSuffix = NetworkUtils.POPULAR_MOVIES_SUFFIX;
        }

        if(mDiscoveryRecyclerView != null) {
            mDiscoveryRecyclerView.stopScroll();
            mDiscoveryRecyclerView.scrollToPosition(0);
            onScrollListener.resetState();
            discoveryData.changeSortSuffix(sortSuffix);
        }
    }

    /**
     * Updates the sorting menu item title.
     * @param item Item that should change the title.
     */
    protected void updateSortByMenuItemTitle(MenuItem item) {
        int titleResId;
        if(sortBy == SortBy.POPULARITY)
            titleResId = R.string.sort_by_ratings;
        else
            titleResId = R.string.sort_by_popularity;

        item.setTitle(titleResId);
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
