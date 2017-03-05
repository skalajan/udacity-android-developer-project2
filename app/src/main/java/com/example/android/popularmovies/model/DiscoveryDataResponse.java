package com.example.android.popularmovies.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Class representing one response from the server with the list of popular/top rated movies.
 */
public class DiscoveryDataResponse extends JSONObject {
    /**
     *
     * @param json Json sting returned with movies list data.
     * @throws JSONException Throws exception when the string couldn't be parsed to JSONObject.
     */
    public DiscoveryDataResponse(String json) throws JSONException {
        super(json);
    }

    /**
     * Gets the the movies from the response.
     * @return JSONArray with the movies.
     * @throws JSONException
     */
    private JSONArray getResults() throws JSONException {
        return getJSONArray("results");
    }

    /**
     * Gets movie at the specified position in array.
     * @param position Index of the movie in JSONArray.
     * @return JSONObject representing the movie.
     * @throws JSONException Throws if the index does not exists or doesn't contain JSONObject.
     */
    JSONObject getItem(int position) throws JSONException {
        return getResults().getJSONObject(position);
    }

    /**
     * Gets page of the response
     * @return Page of the response
     * @throws JSONException Thrown if not present or isn't an Integer.
     */
    int getPage() throws JSONException {
        return getInt("page");
    }

    /**
     * Gets the total number of movies in the list on the server (all pages)
     * @return Total number of movies
     * @throws JSONException Thrown if not present or not an Integer
     */
    int getTotalResults() throws JSONException {
        return getInt("total_results");
    }

    /**
     * Gets the total number of the pages.
     * @return Total number of pages
     * @throws JSONException Thrown if not present or not an Integer
     */
    int getTotalPages() throws JSONException{
        return getInt("total_pages");
    }

    /**
     * Lenght of the list of movies in the response.
     * @return Size of returned movies list.
     */
    int size(){
        int len = 0;
        try {
            len = getResults().length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return len;
    }
}
