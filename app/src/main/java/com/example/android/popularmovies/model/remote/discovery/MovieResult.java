package com.example.android.popularmovies.model.remote.discovery;

import android.content.ContentValues;
import android.database.Cursor;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.example.android.popularmovies.model.database.PopularMoviesContract;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Class representing one result of the response for the list of movies. Also capable of storing into the favorites database.
 */
@Table(name = PopularMoviesContract.FAVORITES_TABLE_NAME)
public class MovieResult extends Model {

    @Expose
    @SerializedName("id")
    @Column(name = PopularMoviesContract.FavoriteMovieEntry.MOVIE_ID_COLUMN)
    private long movieId;

    @Expose
    @SerializedName("poster_path")
    @Column(name = PopularMoviesContract.FavoriteMovieEntry.POSTER_PATH_COLUMN)
    private String posterPath;

    @Expose
    @SerializedName("title")
    @Column(name = PopularMoviesContract.FavoriteMovieEntry.TITLE_COLUMN)
    private String title;

    @Expose
    @SerializedName("vote_average")
    @Column(name = PopularMoviesContract.FavoriteMovieEntry.VOTE_AVERAGE_COLUMN)
    private String voteAverage;

    @Expose
    @SerializedName("overview")
    @Column(name = PopularMoviesContract.FavoriteMovieEntry.OVERVIEW_COLUMN)
    private String overview;

    @Expose
    @SerializedName("release_date")
    @Column(name = PopularMoviesContract.FavoriteMovieEntry.RELEASE_DATE_COLUMN)
    private String releaseDate;

    @Expose
    @SerializedName("original_title")
    @Column(name = PopularMoviesContract.FavoriteMovieEntry.ORIGINAL_TITLE_COLUMN)
    private String originalTitle;

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
        return posterPath;
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
        return voteAverage;
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
        return releaseDate;
    }

    /**
     * Gets original title of the movie.
     * @return Original title
     */
    public String getOriginalTitle() {
        return originalTitle;
    }

    public static MovieResult populateFromCursor(Cursor b) {
        MovieResult mr = new MovieResult();
        int movieIdCol = b.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.MOVIE_ID_COLUMN);
        int posterPathCol = b.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.POSTER_PATH_COLUMN);
        int titleCol = b.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.TITLE_COLUMN);
        int voteCol = b.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.VOTE_AVERAGE_COLUMN);
        int overviewCol = b.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.OVERVIEW_COLUMN);
        int releaseCol = b.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.RELEASE_DATE_COLUMN);
        int originalCol = b.getColumnIndex(PopularMoviesContract.FavoriteMovieEntry.ORIGINAL_TITLE_COLUMN);

        if(movieIdCol != -1)
            mr.movieId = b.getLong(movieIdCol);
        if(posterPathCol != -1)
            mr.posterPath = b.getString(posterPathCol);
        if(titleCol != -1)
            mr.title = b.getString(titleCol);
        if(voteCol != -1)
            mr.voteAverage = b.getString(voteCol);
        if(overviewCol != -1)
            mr.overview = b.getString(overviewCol);
        if(releaseCol != -1)
            mr.releaseDate = b.getString(releaseCol);
        if(originalCol != -1)
            mr.originalTitle = b.getString(originalCol);

        return mr;
    }

    public ContentValues toContentValues(){
        ContentValues values = new ContentValues();
        values.put(PopularMoviesContract.FavoriteMovieEntry.MOVIE_ID_COLUMN, getMovieId());
        values.put(PopularMoviesContract.FavoriteMovieEntry.ORIGINAL_TITLE_COLUMN, getOriginalTitle());
        values.put(PopularMoviesContract.FavoriteMovieEntry.OVERVIEW_COLUMN, getOverview());
        values.put(PopularMoviesContract.FavoriteMovieEntry.POSTER_PATH_COLUMN, getPosterPath());
        values.put(PopularMoviesContract.FavoriteMovieEntry.RELEASE_DATE_COLUMN, getReleaseDate());
        values.put(PopularMoviesContract.FavoriteMovieEntry.VOTE_AVERAGE_COLUMN, getVoteAverage());
        values.put(PopularMoviesContract.FavoriteMovieEntry.TITLE_COLUMN, getTitle());

        return values;
    }
}
