package com.example.android.popularmovies.model.remote.review;

import com.google.gson.annotations.Expose;

/**
 * Created by kjs566 on 19.3.2017.
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
