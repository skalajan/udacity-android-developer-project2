package com.example.android.popularmovies.model;

import com.google.gson.annotations.Expose;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;


/**
 * Class representing one response from the server with the list of popular/top rated movies.
 */
public class DiscoveryDataResponse {
    @Expose
    private List<MovieResult> results;
    @Expose
    private int page;
    @Expose
    private int total_results;
    @Expose
    private int total_pages;

    /**
     * Gets movie at the specified position in JSON array.
     * @param position Index of the movie in results array.
     * @return MovieResult representing the movie.
     */
    public MovieResult getMovieResult(int position){
        return results.get(position);
    }

    /**
     * Gets page of the response
     * @return Page of the response
     */
    int getPage(){
        return page;
    }

    /**
     * Gets the total number of movies in the list on the server (all pages)
     * @return Total number of movies
     */
    int getTotalResults() throws JSONException {
        return total_results;
    }

    /**
     * Gets the total number of the pages.
     * @return Total number of pages
     * @throws JSONException Thrown if not present or not an Integer
     */
    int getTotalPages() throws JSONException{
        return total_pages;
    }

    /**
     * Lenght of the list of movies in the response.
     * @return Size of returned movies list.
     */
    int size(){
        if(results == null)
            return 0;
        else return results.size();
    }
}
