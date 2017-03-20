package com.example.android.popularmovies.discovery;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.example.android.popularmovies.model.cache.DataCache;
import com.example.android.popularmovies.model.cache.RemoteDiscoveryDataCache;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

/**
 * Fragment with the top rated discovery items.
 */
public class DiscoveryTopRatedGridFragment extends DiscoveryGridFragment {
    @Override
    DataCache getDataCache() {
        return new RemoteDiscoveryDataCache(NetworkUtils.TOP_RATED_MOVIES_SUFFIX, getContext(), getLoaderManager());
    }

    @Override
    DataCache restoreDataCacheFromBundle(Bundle bundle, Context context, LoaderManager loaderManager) {
        DataCache cache = null;
        try {
            cache =  RemoteDiscoveryDataCache.restoreFromBundle(bundle, context, loaderManager);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cache;
    }
}
