package com.example.android.popularmovies.model.cache;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.util.Log;

import com.example.android.popularmovies.PopularMoviesApplication;
import com.example.android.popularmovies.model.DataChangedListener;
import com.example.android.popularmovies.model.loader.DiscoveryDataAsyncLoader;
import com.example.android.popularmovies.model.remote.discovery.DiscoveryDataResponse;
import com.example.android.popularmovies.model.remote.discovery.MovieResult;
import com.example.android.popularmovies.model.RequestFailedListener;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Class preloading the discovery data when (before) the user hits the end of the recycler view from the network.
 */
public class RemoteDiscoveryDataCache extends DataCache implements LoaderManager.LoaderCallbacks<DiscoveryDataResponse>{
    private static final String TAG = RemoteDiscoveryDataCache.class.getName();

    private static final int LOADER_ID = 111;

    private final Context context;
    private final LoaderManager loaderManager;

    private ArrayList<DiscoveryDataResponse> discoveryResponses;
    private ArrayList<Integer> indexesFrom;
    private ArrayList<Integer> indexesTo;

    private int loadedCount = 0;

    private boolean loadingNewData = false;

    private String sortSuffix;

    private static final String SAVE_RESPONSES_IN_BUNDLE_KEY = "RESPONSES_KEY";
    private static final String SAVE_SORT_SUFFIX_IN_BUNDLE_KEY = "SUFFIX_KEY";

    private static final String SORT_SUFFIX_BUNDLE_KEY = "SORT_SUFFIX_BUNDLE_KEY ";
    private static final String LOADING_PAGE_NUMBER_BUNDLE_KEY = "LOADING_PAGE_NUMBER_BUNDLE_KEY ";


    /**
     * @param sortSuffix Suffix used in the URL to make the sorting.
     */
    public RemoteDiscoveryDataCache(String sortSuffix, Context context, LoaderManager loaderManager){
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

    public MovieResult getItem(int position) {
        if(loadingNewData && position == getCount())
            return null;

        MovieResult item = null;
        for(int i = 0; i < discoveryResponses.size(); i++){
            if(indexesFrom.get(i) <= position && indexesTo.get(i) >= position) {
                item = discoveryResponses.get(i).getMovieResult(position - indexesFrom.get(i));
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
        if(discoveryData.getPage() >= discoveryResponses.size()){
            Log.v(TAG, "New page added");
            discoveryResponses.add(discoveryData);
        }else{
            discoveryResponses.set(discoveryData.getPage(), discoveryData);
        }
        notifyDataChanged();
    }

    /**
     * Recounts items and informs listener.
     */
    protected void notifyDataChanged(){
        recountItems();
        super.notifyDataChanged();
    }

    /**
     * Creates bundle from the received data to store during the SaveInstanceState in activity
     * @return Bundle with all information needed to save the cache.
     */
    public Bundle saveToBundle() {
        Gson gson = PopularMoviesApplication.getGson();

        Bundle bundle = new Bundle();
        ArrayList<String> responses = new ArrayList<>(discoveryResponses.size());
        for(DiscoveryDataResponse response : discoveryResponses){
            responses.add(gson.toJson(response));
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
    public static RemoteDiscoveryDataCache restoreFromBundle(Bundle bundle, Context context, LoaderManager loaderManager) throws JSONException {
        String sortSuffix = bundle.getString(SAVE_SORT_SUFFIX_IN_BUNDLE_KEY);
        ArrayList<String> responsesStrings = bundle.getStringArrayList(SAVE_RESPONSES_IN_BUNDLE_KEY);

        RemoteDiscoveryDataCache restored = new RemoteDiscoveryDataCache(sortSuffix, context, loaderManager);
        Gson gson = PopularMoviesApplication.getGson();
        if (responsesStrings != null) {
            for(String responseString : responsesStrings){
                restored.discoveryResponses.add(gson.fromJson(responseString, DiscoveryDataResponse.class));
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
            requestFailedListener.onRequestFailed();
        }else if(sortSuffix.equals(l.getMoviesPathSuffix())){
                dataReceived(data);
        }

        loadingNewData = false;
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<DiscoveryDataResponse> loader) {

    }
}
