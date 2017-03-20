package com.example.android.popularmovies.model.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.popularmovies.PopularMoviesApplication;
import com.example.android.popularmovies.model.remote.discovery.DiscoveryDataResponse;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;

/**
 * AsyncLoader class executing request to the movie database for the list of movies in the background.
 */
public class DiscoveryDataAsyncLoader extends AsyncTaskLoader<DiscoveryDataResponse> {
    private static final String TAG = DiscoveryDataAsyncLoader.class.getSimpleName();

    private final int pageToLoad;
    private final String moviesPathSuffix;

    DiscoveryDataResponse response;

    /**
     * @param context Context of the loader
     * @param moviesPathSuffix Suffix in the URL - switches between popular and top rated movies.
     */
    public DiscoveryDataAsyncLoader(Context context, String moviesPathSuffix, int pageToLoad){
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
            String resultString = NetworkUtils.getResponseFromHttpUrl(url);
            if(resultString != null){
                Gson gson = PopularMoviesApplication.getGson();
                result = gson.fromJson(resultString, DiscoveryDataResponse.class);
            }
        } catch (java.net.SocketTimeoutException e){
            Log.d(TAG, "timeout");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "request failed");
        }

        return result;
    }

    /**
     * Gets the page loading in the loader
     * @return Page
     */
    public int getPageToLoad() {
        return pageToLoad;
    }

    /**
     * Gets the path suffix inside the loader.
     * @return Path suffix.
     */
    public String getMoviesPathSuffix() {
        return moviesPathSuffix;
    }
}
