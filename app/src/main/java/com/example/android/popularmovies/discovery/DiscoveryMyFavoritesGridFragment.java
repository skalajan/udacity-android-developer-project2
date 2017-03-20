package com.example.android.popularmovies.discovery;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.example.android.popularmovies.model.cache.DataCache;
import com.example.android.popularmovies.model.cache.FavoritesDiscoveryDataCache;

/**
 * Fragment with favorite items saved in the DB.
 */
public class DiscoveryMyFavoritesGridFragment extends DiscoveryGridFragment {
    @Override
    public void onStart() {
        super.onStart();
        discoveryData.forceReload();
    }

    @Override
    DataCache getDataCache() {
        return new FavoritesDiscoveryDataCache();
    }

    @Override
    DataCache restoreDataCacheFromBundle(Bundle bundle, Context context, LoaderManager loaderManager) {
        return new FavoritesDiscoveryDataCache();
    }
}
