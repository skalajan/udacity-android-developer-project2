package com.example.android.popularmovies.model.remote.review;

import com.google.gson.annotations.Expose;

import java.util.List;


/**
 * Response to reviews request.
 */
public class ReviewsResponse {
    @Expose
    private int page;

    @Expose
    private int total_pages;

    @Expose
    private List<ReviewResult> results;

    /**
     * Gets page of the response.
     * @return Page of the response.
     */
    public int getPage() {
        return page;
    }

    /**
     * Gets total pages available for the request.
     * @return Total pages.
     */
    public int getTotalPages() {
        return total_pages;
    }

    /**
     * Gets list of movie results inside the response.
     * @return List of movie resutls.
     */
    public List<ReviewResult> getResults() {
        return results;
    }
}
