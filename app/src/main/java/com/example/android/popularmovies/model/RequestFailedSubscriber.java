package com.example.android.popularmovies.model;

/**
 * Interface representing listener to the network request failures.
 */
public interface RequestFailedSubscriber {
    /**
     * Triggered when request timeouts.
     */
    void onRequestTimeout();

    /**
     * Triggered when request fails.
     */
    void onRequestFailed(RequestFailedEvent event);
}
