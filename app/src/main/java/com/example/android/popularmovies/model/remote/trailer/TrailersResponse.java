package com.example.android.popularmovies.model.remote.trailer;

import com.google.gson.annotations.Expose;

import java.util.List;


/**
 * Response from server to the trailers request.
 */
public class TrailersResponse {
    @Expose
    private List<TrailerResult> results;

    /**
     * Gets trailer results of the response.
     * @return List of trailer results.
     */
    public List<TrailerResult> getResults() {
        return results;
    }
}