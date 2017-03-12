package com.example.android.popularmovies.model;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kjs566 on 3/10/2017.
 */

public abstract class DataCache {
    protected DataChangedListener dataChangedListener;
    protected RequestFailedListener requestFailedListener;

    public abstract int getCount();
    public abstract MovieResult getItem(int position);
    public void setDataChangedListener(DataChangedListener listener){
        this.dataChangedListener = listener;
    }
    public void setRequestFailedListener(RequestFailedListener listener){
        this.requestFailedListener = listener;
    }
    public void loadNextPage(){}
    public Bundle saveToBundle(){return null;}
    public void forceReload(){}
    protected void notifyDataChanged(){
        if(this.dataChangedListener != null)
            dataChangedListener.onDataChanged();
    }
}
