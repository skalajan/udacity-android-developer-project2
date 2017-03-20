package com.example.android.popularmovies;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by kjs566 on 3/11/2017.
 */

/**
 * Application class initializing ActiveAndroid.
 */
public class PopularMoviesApplication extends Application {
    private static Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();

        ActiveAndroid.initialize(this);
    }

    /**
     * Creates and/or gets GSON instance.
     * @return Gson GSON instance
     */
    public static Gson getGson() {
        if(gson == null)
            gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson;
    }
}