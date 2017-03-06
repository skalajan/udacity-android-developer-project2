package com.example.android.popularmovies.model;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class preloading the discovery data when (before) the user hits the end of the recycler view. And changes the requests based on the sorting order.
 */
public class DiscoveryDataCache {
    private static final String TAG = DiscoveryDataCache.class.getName();

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


    /**
     * @param sortSuffix Suffix used in the URL to make the sorting.
     */
    public DiscoveryDataCache(String sortSuffix){
        discoveryResponses = new ArrayList<>();

        indexesFrom = new ArrayList<>();
        indexesTo = new ArrayList<>();

        this.sortSuffix = sortSuffix;
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
                    new DiscoveryRequest(sortSuffix).execute(nextPage);
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
    public static DiscoveryDataCache restoreFromBundle(Bundle bundle) throws JSONException {
        String sortSuffix = bundle.getString(SAVE_SORT_SUFFIX_IN_BUNDLE_KEY);
        ArrayList<String> responsesStrings = bundle.getStringArrayList(SAVE_RESPONSES_IN_BUNDLE_KEY);

        DiscoveryDataCache restored = new DiscoveryDataCache(sortSuffix);
        if (responsesStrings != null) {
            for(String responseString : responsesStrings){
                restored.discoveryResponses.add(new DiscoveryDataResponse(responseString));
            }
        }
        restored.recountItems();

        return restored;
    }

    /**
     * Discovery item async request class.
     */
    private class DiscoveryRequest extends DiscoveryDataAsyncRequest{
        DiscoveryRequest(String moviesPathSuffix) {
            super(moviesPathSuffix);
        }

        /**
         * Sets the loading new data flag
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingNewData = true;
            //notifyDataChanged();
            onLoadingNewData();
        }

        /**
         * Informs the cache that new data received and unset the loading flag.
         * @param jsonObject Received object.
         */
        @Override
        protected void onPostExecute(DiscoveryDataResponse jsonObject) {
            super.onPostExecute(jsonObject);
            if(jsonObject != null){
                dataReceived(jsonObject);
            }
            loadingNewData = false;
        }

        /**
         * Triggers listener when request timeouts.
         */
        @Override
        public void onTimeout() {
            super.onTimeout();

            Log.d(TAG, "request timeout");

            if(requestFailedListener != null)
                requestFailedListener.onTimeout();
        }


        /**
         * Triggers listener when request fails.
         */
        @Override
        public void onFail() {
            super.onFail();

            Log.d(TAG, "request failed");

            if(requestFailedListener != null)
                requestFailedListener.onFail();
        }
    }

}
