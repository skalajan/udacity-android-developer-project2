package com.example.android.popularmovies.model.cache;

import android.os.Bundle;

import com.example.android.popularmovies.model.DataChangedListener;
import com.example.android.popularmovies.model.remote.discovery.MovieResult;
import com.example.android.popularmovies.model.RequestFailedListener;

/**
 * Class abstracting the data cache.
 */
public abstract class DataCache {
    protected DataChangedListener dataChangedListener;
    protected RequestFailedListener requestFailedListener;

    /**
     * Get count of the items inside the cache.
     * @return count
     */
    public abstract int getCount();

    /**
     * Gets item at position
     * @param position Position of the wanted item.
     * @return Movie item
     */
    public abstract MovieResult getItem(int position);

    /**
     * Sets the listener to the data changed event.
     * @param listener Listener
     */
    public void setDataChangedListener(DataChangedListener listener){
        this.dataChangedListener = listener;
    }

    /**
     * Sets the listener to the request failed event.
     * @param listener Listener
     */
    public void setRequestFailedListener(RequestFailedListener listener){
        this.requestFailedListener = listener;
    }

    /**
     * Loads next page into the cache.
     */
    public void loadNextPage(){}

    /**
     * Saves the cache into the bundle.
     * @return Bundle
     */
    public Bundle saveToBundle(){return null;}

    /**
     * Forces to clear and reload the cache.
     */
    public void forceReload(){}
    protected void notifyDataChanged(){
        if(this.dataChangedListener != null)
            dataChangedListener.onDataChanged();
    }
}
