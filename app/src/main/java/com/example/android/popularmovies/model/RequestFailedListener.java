package com.example.android.popularmovies.model;

/**
 * Interface representing listener to the network request failures.
 */
public interface RequestFailedListener {
    /**
     * Triggered when request timeouts.
     */
    void onTimeout();

    /**
     * Triggered when request fails.
     */
    void onFail();
}
