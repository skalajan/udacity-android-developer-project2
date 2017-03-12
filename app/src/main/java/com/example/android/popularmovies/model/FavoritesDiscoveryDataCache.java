package com.example.android.popularmovies.model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by kjs566 on 3/11/2017.
 */

public class FavoritesDiscoveryDataCache extends DataCache {
    private List<MovieResult> favoriteMovies;

    public FavoritesDiscoveryDataCache(){
        loadFavoriteMovies();
    }

    private void loadFavoriteMovies(){
        favoriteMovies = MovieResult.fetchFavoriteMoviesCursor();
    }

    @Override
    public int getCount() {
        return favoriteMovies.size();
    }

    @Override
    public MovieResult getItem(int position) {
        return favoriteMovies.get(position);
    }

    @Override
    public void loadNextPage() {
        dataChangedListener.onDataChanged();
        notifyDataChanged();
    }

    public void forceReload(){
        loadFavoriteMovies();
        notifyDataChanged();
    }

}
