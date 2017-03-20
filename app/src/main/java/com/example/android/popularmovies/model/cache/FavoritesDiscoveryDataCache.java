package com.example.android.popularmovies.model.cache;

import com.example.android.popularmovies.model.remote.discovery.MovieResult;

import java.util.List;


/**
 * DataCache for the favorites items taking the items from the DB.
 */
public class FavoritesDiscoveryDataCache extends DataCache {
    private List<MovieResult> favoriteMovies;

    /**
     * Constructor of the favorites data cache.
     */
    public FavoritesDiscoveryDataCache(){
        loadFavoriteMovies();
    }

    private void loadFavoriteMovies(){
        favoriteMovies = MovieResult.fetchFavoriteMovies();
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
