package com.example.android.popularmovies.model.remote.review;

import com.google.gson.annotations.Expose;

/**
 * Review result class representing review downloaded fro, server.
 */
public class ReviewResult {
    @Expose
    private String id;

    @Expose
    private String author;

    @Expose
    private String content;

    @Expose
    private String url;


    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }
}
