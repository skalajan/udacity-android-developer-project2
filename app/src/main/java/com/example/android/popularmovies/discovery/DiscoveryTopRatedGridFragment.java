package com.example.android.popularmovies.discovery;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.example.android.popularmovies.model.DataCache;
import com.example.android.popularmovies.model.RemoteDiscoveryDataCache;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

/**
 * Created by kjs566 on 3/10/2017.
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
