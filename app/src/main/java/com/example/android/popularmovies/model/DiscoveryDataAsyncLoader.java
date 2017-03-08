package com.example.android.popularmovies.model;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * AsyncTack class executing request to the movie database for the list of movies in the background.
 */
class DiscoveryDataAsyncLoader extends AsyncTaskLoader<DiscoveryDataResponse> {
    private static final String TAG = DiscoveryDataAsyncLoader.class.getSimpleName();

    private final int pageToLoad;
    private final String moviesPathSuffix;

    DiscoveryDataResponse response;

    /**
     * @param context Context of the loader
     * @param moviesPathSuffix Suffix in the URL - switches between popular and top rated movies.
     */
    DiscoveryDataAsyncLoader(Context context, String moviesPathSuffix, int pageToLoad){
        super(context);
        this.moviesPathSuffix = moviesPathSuffix;
        this.pageToLoad = pageToLoad;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if(response != null){
            deliverResult(response);
        }else{
            forceLoad();
        }
    }

    /**
     * Executes the request in the background.
     * @return Response with discovery data or null.
     */
    @Override
    public DiscoveryDataResponse loadInBackground() {
        Log.v(TAG, "Loading");
        DiscoveryDataResponse result = null;

        URL url = NetworkUtils.buildDiscoveryUrl(pageToLoad, moviesPathSuffix);
        try {
            result = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (java.net.SocketTimeoutException e){
            Log.d(TAG, "timeout");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "request failed");
        }

        return result;
    }

    public int getPageToLoad() {
        return pageToLoad;
    }

    public String getMoviesPathSuffix() {
        return moviesPathSuffix;
    }
}
