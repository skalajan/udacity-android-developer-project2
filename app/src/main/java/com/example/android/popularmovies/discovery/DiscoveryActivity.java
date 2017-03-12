package com.example.android.popularmovies.discovery;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.popularmovies.Constants;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.moviedetail.MovieDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiscoveryActivity extends NetworkAccessingActivity implements OnPosterClickedListener, OnFirstLoadingFinishedListener {
    public static final String TAG = DiscoveryActivity.class.getName();

    @BindView(R.id.tabs)
    protected TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    protected ViewPager mViewPager;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;


    /**
     * Saves the states of the activity and data cache before the activity is destroyed.
     * @param outState Output bundle with stored data.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * Initializes the activity, sets the number of columns according to screen width and setups the Recycler view.
     * @param savedInstanceState Saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_discovery_screen);

        //Check whether the API KEY is present, else do not further initialize and show toast
        if(Constants.API_KEY == null) {
            Toast.makeText(this, R.string.no_api_key_message, Toast.LENGTH_LONG).show();
            return;
        }

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);

        for(int i = 0; i < mTabLayout.getTabCount(); i++){
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_layout);
            }
        }

        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_like);
        mTabLayout.getTabAt(0).setText(R.string.popular);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_star_border_black_24dp);
        mTabLayout.getTabAt(1).setText(R.string.top_rated);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_favorite_border_white_24dp);
        mTabLayout.getTabAt(2).setText(R.string.my_favorites);

    }

    /**
     * Listens to the poster clicks. Starts new activity and passes movie information to it.
     * @param position Index of the clicked item.
     */
    @Override
    public void onPosterClicked(int position) {
        Log.v(TAG, "Poster clicked " + position);

        Intent movieDetailsIntent = new Intent(this, MovieDetailActivity.class);
        //try {
            //movieDetailsIntent.putExtra(Constants.MOVIE_DETAILS_EXTRA, discoveryData.getMovieResult(position).toString());
            startActivity(movieDetailsIntent);
        /*} catch (JSONException e) {
            Log.e(TAG, "Error during getting clicked movie data");
            e.printStackTrace();
            Toast.makeText(this, "Couldn't get the movie detail data", Toast.LENGTH_SHORT).show();
        }*/
    }

    /**
     * Hides initial progress bar after first data loaded.
     */
    @Override
    public void onAfterFirstLoad() {
        /*mFirstLoadingProgressBar.setVisibility(View.GONE);
        mDiscoveryRecyclerView.setVisibility(View.VISIBLE);*/
    }

    /**
     * Tries again to load next page.
     */
    @Override
    protected void tryRequestAgain() {
        //discoveryData.loadNextPage();
    }

    /**
     * Updates the sorting (titleResIdmenu item title.
     * @param item Item that should change the title.
     */
        protected void updateSortByMenuItemTitle(MenuItem item) {
        /*int titleResId;
        if(sortBy == SortBy.POPULARITY)
            titleResId = R.string.sort_by_ratings;
        else
            titleResId = R.string.sort_by_popularity;

        item.setTitle);*/
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final Fragment[] mFragments = new Fragment[3];


        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if(mFragments[position] == null) {
                mFragments[position] = createFragment(position);
            }
            return mFragments[position];
        }

        Fragment createFragment(int position){
            switch(position){
                case 0:
                    return new DiscoveryPopularGridFragment();
                case 1:
                    return new DiscoveryTopRatedGridFragment();
                case 2:
                    return new DiscoveryMyFavoritesGridFragment();
                default:
                    Log.e(TAG, "Unknown fragment, not in 0-2 range");
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }
    }

}
