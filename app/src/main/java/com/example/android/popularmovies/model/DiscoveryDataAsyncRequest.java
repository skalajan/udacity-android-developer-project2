package com.example.android.popularmovies.model;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

/**
 * AsyncTack class executing request to the movie database for the list of movies in the background.
 */
abstract class DiscoveryDataAsyncRequest extends AsyncTask<Integer, Void, DiscoveryDataResponse>{
    private static final String TAG = DiscoveryDataAsyncRequest.class.getSimpleName();

    private String moviesPathSuffix;

    /**
     * @param moviesPathSuffix Suffix in the URL - switches between popular and top rated movies.
     */
    DiscoveryDataAsyncRequest(String moviesPathSuffix){
        this.moviesPathSuffix = moviesPathSuffix;
    }

    /**
     * Executes the request in the background.
     * @param params Optional page of the movies we want to receive from server. If not present, the request for the first page is executed.
     * @return Response with discovery data.
     */
    @Override
    protected DiscoveryDataResponse doInBackground(Integer... params) {
        DiscoveryDataResponse result = null;
        int page = 1;
        if(params.length >= 1)
            page = params[0];

        URL url = NetworkUtils.buildDiscoveryUrl(page, moviesPathSuffix);
        try {
            result = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (java.net.SocketTimeoutException e){
            Log.d(TAG, "timeout");
            onTimeout();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "request failed");
            onFail();
        }

        return result;
    }

    /**
     * Method called if the request timeouts.
     */
    public void onTimeout(){}

    /**
     * Method called if the request fails.
     */
    public void onFail(){}
}
