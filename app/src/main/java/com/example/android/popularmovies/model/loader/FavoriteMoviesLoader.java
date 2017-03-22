package com.example.android.popularmovies.model.loader;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.example.android.popularmovies.model.database.PopularMoviesContract;


/**
 * Async loader fetching the favorite movies from content provider.
 */
public class FavoriteMoviesLoader extends AsyncTaskLoader<Cursor> {
    private static final String TAG = FavoriteMoviesLoader.class.getSimpleName();
    private Cursor mData;

    public FavoriteMoviesLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if(mData != null){
            deliverResult(mData);
        }else{
            forceLoad();
        }
    }

    @Override
    public Cursor loadInBackground() {
        try {
            return getContext().getContentResolver().query(PopularMoviesContract.FavoriteMovieEntry.CONTENT_URI, null, null, null, null);
        }catch (Exception e){
            Log.e(TAG, "Couldnt load cursor to favorite movies");
            return null;
        }
    }

    @Override
    public void deliverResult(Cursor data) {
        super.deliverResult(data);

        this.mData = data;
    }
}
