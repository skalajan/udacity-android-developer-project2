package com.example.android.popularmovies.model.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.popularmovies.PopularMoviesApplication;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;

/**
 * General Loader class used to load detailed information about the movie. T represents the response.
 */
public class MovieDetailLoader<T> extends AsyncTaskLoader<T> {
    private static final String TAG = MovieDetailLoader.class.getSimpleName();

    private final long movieId;
    private final Class<T> resultClass;
    private final String pathSuffix;

    private T response;


    /**
     * Constructor of the loader.
     * @param context Context
     * @param movieId Id of the movie to be loaded
     * @param pathSuffix Suffix of the wanted request
     * @param resultClass Class of the response
     */
    public MovieDetailLoader(Context context, long movieId, String pathSuffix, Class<T> resultClass){
        super(context);
        this.movieId = movieId;
        this.pathSuffix = pathSuffix;
        this.resultClass = resultClass;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if(response != null){
            deliverResult(response);
        }else{
            forceLoad();
        }
    }

    /**
     * Executes the request in the background.
     * @return Response with movie detail data or null.
     */
    @Override
    public T loadInBackground() {
        Log.v(TAG, "Loading");
        T result = null;

        URL url = NetworkUtils.buildMovieDetailUrl(movieId, pathSuffix);
        try {
            String resultJsonString = NetworkUtils.getResponseFromHttpUrl(url);
            if(resultJsonString != null){
                Gson gson = PopularMoviesApplication.getGson();
                result = gson.fromJson(resultJsonString, resultClass);
            }
        } catch (java.net.SocketTimeoutException e){
            Log.d(TAG, "timeout");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "request failed");
        }

        return result;
    }

    /**
     * Gets id of the loading movie.
     * @return Id of movie
     */
    public long getMovieId() {
        return movieId;
    }
}
