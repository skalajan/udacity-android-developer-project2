package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;

/**
 * Helping common parent activity for whole project.
 */
public abstract class PopularMoviesActivity extends AppCompatActivity {
    PopularMoviesApplication getApp(){
        return (PopularMoviesApplication) getApplication();
    }
}
