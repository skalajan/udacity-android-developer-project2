package com.example.android.popularmovies.model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.example.android.popularmovies.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kjs566 on 3/11/2017.
 */
@Table(name = Constants.FAVORITES_TABLE_NAME)
public class MovieResult extends Model {
    @Expose
    @SerializedName("id")
    @Column
    private long movieId;

    @Expose
    @Column
    private String poster_path;
    @Expose
    @Column
    private String title;
    @Expose
    @Column
    private String vote_average;
    @Expose
    @Column
    private String overview;
    @Expose
    @Column
    private String release_date;
    @Expose
    @Column
    private String original_title;

    public MovieResult(){};

    public long getMovieId() {
        return movieId;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public String getTitle() {
        return title;
    }

    public String getVoteAverage() {
        return vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public String getOriginalTitle() {
        return original_title;
    }



    public static List<MovieResult> fetchFavoriteMoviesCursor(){
        return new Select().from(MovieResult.class).execute();
    }

    public static boolean isInFavorites(long movieId){
        return new Select().from(MovieResult.class).where("movieId = " + movieId).exists();
    }

    public static int countFavoriteMovies(){
        return new Select().from(MovieResult.class).count();
    }

    public static void addToFavorites(MovieResult result){
        result.save();
    }

    public static void removeFromFavorites(long movieId) {
        new Delete().from(MovieResult.class).where("movieId = " + movieId).execute();
    }
}
