package com.example.android.popularmovies.discovery;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmovies.Constants;
import com.example.android.popularmovies.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity with the discovery movie items.
 */
public class DiscoveryActivity extends NetworkAccessingActivity {
    public static final String TAG = DiscoveryActivity.class.getName();

    @BindView(R.id.tabs)
    protected TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    protected ViewPager mViewPager;
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    ViewPagerAdapter adapter;


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
        setContentView(R.layout.activity_main_discovery_screen);
        super.onCreate(savedInstanceState);

        //Check whether the API KEY is present, else do not further initialize and show toast
        if(Constants.API_KEY == null) {
            Toast.makeText(this, R.string.no_api_key_message, Toast.LENGTH_LONG).show();
            return;
        }

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);

        for(int i = 0; i < mTabLayout.getTabCount(); i++){
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_layout);
            }
        }


        if(mTabLayout.getTabCount() > 2) {
            TabLayout.Tab tab0 = mTabLayout.getTabAt(0);
            TabLayout.Tab tab1 = mTabLayout.getTabAt(1);
            TabLayout.Tab tab2 = mTabLayout.getTabAt(2);

            if(tab0 != null) {
                tab0.setIcon(R.drawable.ic_like);
                tab0.setText(R.string.popular);
            }
            if(tab1 != null) {
                tab1.setIcon(R.drawable.ic_star_border_black_24dp);
                tab1.setText(R.string.top_rated);
            }
            if(tab2 != null) {
                tab2.setIcon(R.drawable.ic_favorite_border_white_24dp);
                tab2.setText(R.string.my_favorites);
            }
        }
    }

    @Override
    protected void tryRequestAgain() {
        for (DiscoveryGridFragment fragment : adapter.getItemsWithoutCreating()) {
            if(fragment != null){
                fragment.tryAgain();
            }
        }
    }


    /**
     * Pager adapter fot the TabLayout.
     */
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private DiscoveryGridFragment[] mFragments = new DiscoveryGridFragment[3];


        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if(mFragments[position] == null) {
                mFragments[position] = createFragment(position);
            }
            return mFragments[position];
        }

        /**
         * Gets fragments without creating missing.
         * @return Discoverz grid fragments
         */
        DiscoveryGridFragment[] getItemsWithoutCreating(){
            return mFragments;
        }

        /**
         * Creates fragment at position.
         * @param position Position of the fragment.
         * @return Fragment with the discovery items grid.
         */
        DiscoveryGridFragment createFragment(int position){
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
