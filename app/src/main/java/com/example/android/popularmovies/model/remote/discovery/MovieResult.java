package com.example.android.popularmovies.model.remote.discovery;

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
 * Class representing one result of the response for the list of movies. Also capable of storing into the favorites database.
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

    /**
     * Public empty constructor.
     */
    public MovieResult(){}

    /**
     * Gets id of the movie
     * @return Id of the movie
     */
    public long getMovieId() {
        return movieId;
    }

    /**
     * Gets path to the poster
     * @return Path to the poster
     */
    public String getPosterPath() {
        return poster_path;
    }

    /**
     * Gets title of the movie.
     * @return Title of the movie
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets average of the votes for the movie.
     * @return Avarage voting
     */
    public String getVoteAverage() {
        return vote_average;
    }

    /**
     * Gets overview of the movie
     * @return Overview of the movie
     */
    public String getOverview() {
        return overview;
    }

    /**
     * Gets release date of the movie
     * @return Release date
     */
    public String getReleaseDate() {
        return release_date;
    }

    /**
     * Gets original title of the movie.
     * @return Original title
     */
    public String getOriginalTitle() {
        return original_title;
    }

    /**
     * Fetches the favorite movies from the DB.
     * @return List of favorite movies
     */
    public static List<MovieResult> fetchFavoriteMovies(){
        return new Select().from(MovieResult.class).execute();
    }

    /**
     * Checks whether the movie with id is in favorites DB.
     * @param movieId Id of the movie
     * @return Whether the movie is in favorites
     */
    public static boolean isInFavorites(long movieId){
        return new Select().from(MovieResult.class).where("movieId = " + movieId).exists();
    }

    /**
     * Counts the favorite movies in DB.
     * @return count
     */
    public static int countFavoriteMovies(){
        return new Select().from(MovieResult.class).count();
    }

    /**
     * Adds the movie to the favorites.
     * @param result Movie result that should be added.
     */
    public static void addToFavorites(MovieResult result){
        result.save();
    }

    /**
     * Remove movie with selected id from the DB.
     * @param movieId Id of the movie to be deleted.
     */
    public static void removeFromFavorites(long movieId) {
        new Delete().from(MovieResult.class).where("movieId = " + movieId).execute();
    }
}
