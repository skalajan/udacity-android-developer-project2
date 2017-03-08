package com.example.android.popularmovies.model;

import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class preloading the discovery data when (before) the user hits the end of the recycler view. And changes the requests based on the sorting order.
 */
public class DiscoveryDataCache implements LoaderManager.LoaderCallbacks<DiscoveryDataResponse>{
    private static final String TAG = DiscoveryDataCache.class.getName();

    private static final int LOADER_ID = 111;

    private final Context context;
    private final LoaderManager loaderManager;

    private ArrayList<DiscoveryDataResponse> discoveryResponses;
    private ArrayList<Integer> indexesFrom;
    private ArrayList<Integer> indexesTo;

    private int loadedCount = 0;

    private boolean loadingNewData = false;
    private DataChangedListener dataChangedListener;
    private RequestFailedListener requestFailedListener;

    private String sortSuffix;

    private static final String SAVE_RESPONSES_IN_BUNDLE_KEY = "RESPONSES_KEY";
    private static final String SAVE_SORT_SUFFIX_IN_BUNDLE_KEY = "SUFFIX_KEY";

    private static final String SORT_SUFFIX_BUNDLE_KEY = "SORT_SUFFIX_BUNDLE_KEY ";
    private static final String LOADING_PAGE_NUMBER_BUNDLE_KEY = "LOADING_PAGE_NUMBER_BUNDLE_KEY ";


    /**
     * @param sortSuffix Suffix used in the URL to make the sorting.
     */
    public DiscoveryDataCache(String sortSuffix, Context context, LoaderManager loaderManager){
        discoveryResponses = new ArrayList<>();

        indexesFrom = new ArrayList<>();
        indexesTo = new ArrayList<>();

        this.sortSuffix = sortSuffix;
        this.context = context;
        this.loaderManager = loaderManager;
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

    public JSONObject getItem(int position) throws JSONException {
        if(loadingNewData && position == getCount())
            return null;

        JSONObject item = null;
        for(int i = 0; i < discoveryResponses.size(); i++){
            if(indexesFrom.get(i) <= position && indexesTo.get(i) >= position) {
                item = discoveryResponses.get(i).getItem(position - indexesFrom.get(i));
                break;
            }
        }

        return item;
    }

    /**
     * Sets listener triggered when data changes.
     * @param listener Listener of data change event.
     */
    public void setDataChangedListener(DataChangedListener listener){
        this.dataChangedListener = listener;
    }

    /**
     * Sets listener triggered when request fails.
     * @param listener Listener to the request failed error.
     */
    public void setRequestFailedListener(RequestFailedListener listener){
        this.requestFailedListener = listener;
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
                    Bundle args = new Bundle();
                    args.putString(SORT_SUFFIX_BUNDLE_KEY, sortSuffix);
                    args.putInt(LOADING_PAGE_NUMBER_BUNDLE_KEY, nextPage);

                    loaderManager.restartLoader(LOADER_ID, args, this);
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
     * Method triggered before loading new data.
     */
    protected void onLoadingNewData(){

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
            notifyDataChanged();
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
        if(dataChangedListener != null){
            dataChangedListener.onDataChanged();
        }
    }

    /**
     * Creates bundle from the received data to store during the SaveInstanceState in activity
     * @return Bundle with all information needed to save the cache.
     */
    public Bundle saveToBundle() {
        Bundle bundle = new Bundle();
        ArrayList<String> responses = new ArrayList<>(discoveryResponses.size());
        for(DiscoveryDataResponse response : discoveryResponses){
            responses.add(response.toString());
        }
        bundle.putStringArrayList(SAVE_RESPONSES_IN_BUNDLE_KEY, responses);
        bundle.putString(SAVE_SORT_SUFFIX_IN_BUNDLE_KEY, sortSuffix);

        return bundle;
    }

    /**
     * Restores the cache from the saved bundle.
     * @param bundle Saved bundle with.
     * @return Restored data cache
     * @throws JSONException
     */
    public static DiscoveryDataCache restoreFromBundle(Bundle bundle, Context context, LoaderManager loaderManager) throws JSONException {
        String sortSuffix = bundle.getString(SAVE_SORT_SUFFIX_IN_BUNDLE_KEY);
        ArrayList<String> responsesStrings = bundle.getStringArrayList(SAVE_RESPONSES_IN_BUNDLE_KEY);

        DiscoveryDataCache restored = new DiscoveryDataCache(sortSuffix, context, loaderManager);
        if (responsesStrings != null) {
            for(String responseString : responsesStrings){
                restored.discoveryResponses.add(new DiscoveryDataResponse(responseString));
            }
        }
        restored.recountItems();

        return restored;
    }


    @Override
    public android.support.v4.content.Loader<DiscoveryDataResponse> onCreateLoader(int id, Bundle args) {
        return new DiscoveryDataAsyncLoader(context, args.getString(SORT_SUFFIX_BUNDLE_KEY), args.getInt(LOADING_PAGE_NUMBER_BUNDLE_KEY));
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<DiscoveryDataResponse> loader, DiscoveryDataResponse data) {
        DiscoveryDataAsyncLoader l = (DiscoveryDataAsyncLoader) loader;
        if(data == null){
            requestFailedListener.onFail();
        }else if(sortSuffix.equals(l.getMoviesPathSuffix())){
                dataReceived(data);
        }

        loadingNewData = false;
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<DiscoveryDataResponse> loader) {

    }
}
