package com.example.android.popularmovies.model.cache;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.example.android.popularmovies.model.loader.FavoriteMoviesLoader;
import com.example.android.popularmovies.model.remote.discovery.MovieResult;


/**
 * DataCache for the favorites items.
 */
public class FavoritesDiscoveryDataCache extends DataCache implements LoaderManager.LoaderCallbacks<Cursor>{
    private Cursor cursor;

    private static final int LOADER_ID = 222;

    private final Context context;
    private LoaderManager loaderManager;

    /**
     * Constructor of the favorites data cache.
     */
    public FavoritesDiscoveryDataCache(Context context, LoaderManager loaderManager){
        this.context = context;

        this.loaderManager = loaderManager;
    }

    @Override
    public int getCount() {
        if(cursor == null)
            return 0;
        else
            return cursor.getCount();
    }

    @Override
    public MovieResult getItem(int position) {
        cursor.moveToPosition(position);
        return MovieResult.populateFromCursor(cursor);
    }

    @Override
    public void loadNextPage() {
        dataChangedListener.onDataChanged();
        notifyDataChanged();
    }

    public void forceReload(){
        loaderManager.restartLoader(LOADER_ID, null, this);
        notifyDataChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new FavoriteMoviesLoader(context);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.cursor = data;
        notifyDataChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
