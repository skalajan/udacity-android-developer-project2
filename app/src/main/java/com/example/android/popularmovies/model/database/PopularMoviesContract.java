package com.example.android.popularmovies.model.database;

import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Contact class for storing DB schema constants and favorite content provider uris.
 */
public class PopularMoviesContract {
    public static final String FAVORITES_TABLE_NAME = "FavoriteMoviesTable";

    public static final String AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String FAVORITES_PATH = "favorites";
    public static final String FAVORITES_PATH_BY_MOVIE_ID = FAVORITES_PATH + "/movieId";


    public static final class FavoriteMovieEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(FAVORITES_PATH).build();

        /**
         * Creates content URI for favorite movie with specified movieId.
         * @param movieId MovieId
         * @return Uri to the content of the movie with movieId.
         */
        public static Uri createContentUriByMovieId(String movieId){
            return BASE_CONTENT_URI.buildUpon().appendEncodedPath(FAVORITES_PATH_BY_MOVIE_ID).appendPath(movieId).build();
        }

        public static final String MOVIE_ID_COLUMN = "movieid";
        public static final String POSTER_PATH_COLUMN = "posterpath";
        public static final String TITLE_COLUMN = "title";
        public static final String VOTE_AVERAGE_COLUMN = "voteaverage";
        public static final String OVERVIEW_COLUMN = "overview";
        public static final String RELEASE_DATE_COLUMN = "releasedate";
        public static final String ORIGINAL_TITLE_COLUMN = "originaltitle";
    }
}
