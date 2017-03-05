package com.example.android.popularmovies.model;

import org.json.JSONObject;

/**
 * Created by kjs566 on 3/4/2017.
 */

public interface DiscoveryDataCache {
    int getCount();

    JSONObject getItem(int position);
}
