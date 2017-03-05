package com.example.android.popularmovies.model;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Class preloading the discovery data when (before) the user hits the end of the recycler view. And changes the requests based on the sorting order.
 */
public class DiscoveryDataLoader extends AsyncTaskLoader<DiscoveryDataCache> implements DiscoveryDataCache {
    private static final String TAG = DiscoveryDataLoader.class.getName();

    public static final int LOADER_ID = 111;
    public static final String SORT_SUFFIX_BUNDLE_KEY = "SORT_SUFFIX_BUNDLE_KEY";

    private ArrayList<DiscoveryDataResponse> discoveryResponses;
    private ArrayList<Integer> indexesFrom;
    private ArrayList<Integer> indexesTo;

    private int loadedCount = 0;

    private boolean loadingNewData = false;

    private String sortSuffix;


    public DiscoveryDataLoader(Context context, Bundle args){
        super(context);

        sortSuffix = args.getString(SORT_SUFFIX_BUNDLE_KEY);

        discoveryResponses = new ArrayList<>();

        indexesFrom = new ArrayList<>();
        indexesTo = new ArrayList<>();
    }

    /**
     * Counts the items in all pages received.
     */
    private void recountItems(){
        int newCount = 0;
        int from = 0;

        indexesFrom.clear();
        indexesTo.clear();

        for(int i = 0; i < discoveryResponses.size(); i++){
            int currentSize = discoveryResponses.get(i).size();
            newCount += currentSize;
            indexesFrom.add(from);
            indexesTo.add(from + currentSize -1);

            from += currentSize;
        }

        loadedCount = newCount;
    }

    /**
     * returns the count of items + 1 to inform about loading at the last item.
     * @return Number of items + 1
     */
    public int getCount(){
        return loadedCount + 1;
    }

    public int getCurrentPage(){
        if(discoveryResponses == null || discoveryResponses.size() == 0)
            return 1;
        else return discoveryResponses.size() + 1;
    }

    public JSONObject getItem(int position){
        if(loadingNewData && position == getCount())
            return null;

        JSONObject item = null;
        for(int i = 0; i < discoveryResponses.size(); i++){
            if(indexesFrom.get(i) <= position && indexesTo.get(i) >= position) {
                try {
                    item = discoveryResponses.get(i).getItem(position - indexesFrom.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        return item;
    }

    /**
     * Loads the next page of the movies.
     */
    public void loadNextPage(){
        if(!loadingNewData) {
            Log.v(TAG, "Loafing next page");

            int nextPage = discoveryResponses.size() + 1;
            try {
                if (discoveryResponses.size() == 0 || (discoveryResponses.size() > 0 && discoveryResponses.get(0).getTotalPages() >= nextPage)) {
                    forceLoad();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Changes sorting order of the movies.
     * @param sortSuffix Suffix used to the sorting
     */
    public void changeSortSuffix(String sortSuffix){
        this.sortSuffix = sortSuffix;

        resetCache();

        loadNextPage();
    }

    /**
     * Resets the cache, removing all the items.
     */
    private void resetCache(){
        loadedCount = 0;
        notifyDataChanged();

        discoveryResponses.clear();
        indexesFrom.clear();
        indexesTo.clear();

        loadingNewData = false;
        //TODO CANCEL ALL PENDING REQUESTS
    }

    /**
     * Adds new data to the list of movies.
     * @param discoveryData New received response from the server.
     */
    private void dataReceived(DiscoveryDataResponse discoveryData){
        try {
            if(discoveryData.getPage() >= discoveryResponses.size()){
                Log.v(TAG, "New page added");
                discoveryResponses.add(discoveryData);
            }else{
                discoveryResponses.set(discoveryData.getPage(), discoveryData);
            }
//          notifyDataChanged();
            recountItems();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recounts items and informs listener.
     */
    private void notifyDataChanged(){
        recountItems();
        Log.d(TAG, "Data changed, new count " + getCount());

        deliverResult(this);
    }

    @Override
    public DiscoveryDataLoader loadInBackground() {

        Log.v(TAG, "Loading in background");
        loadingNewData = true;

        DiscoveryDataResponse result = null;

        URL url = NetworkUtils.buildDiscoveryUrl(getCurrentPage(), sortSuffix);
        try {
            result = NetworkUtils.getResponseFromHttpUrl(url);
        } catch (java.net.SocketTimeoutException e){
            Log.d(TAG, "timeout");
            onTimeout();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "request failed");
            onFailed();
        }

        if(result != null){
            dataReceived(result);
        }

        loadingNewData = false;


        return this;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if(discoveryResponses == null || discoveryResponses.size() == 0)
            forceLoad();
        else deliverResult(this);
    }

    protected void onTimeout(){
        loadingNewData = false;
    }

    protected void onFailed(){
        loadingNewData = false;
    }
}
