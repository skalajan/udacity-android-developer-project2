package com.example.android.popularmovies.discovery;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.example.android.popularmovies.model.DataCache;
import com.example.android.popularmovies.model.FavoritesDiscoveryDataCache;
import com.example.android.popularmovies.model.RemoteDiscoveryDataCache;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

/**
 * Created by kjs566 on 3/10/2017.
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
