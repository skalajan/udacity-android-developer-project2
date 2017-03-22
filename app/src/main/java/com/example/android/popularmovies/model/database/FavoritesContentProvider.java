package com.example.android.popularmovies.model.database;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.Cache;

/**
 * Content provider for the favorite movies.
 */
public class FavoritesContentProvider extends ContentProvider {
    public static final int FAVORITES_CODE = 100;
    public static final int ONE_FAVORITE_CODE = 101;
    public static final int ONE_FAVORITE_BY_MOVIE_ID = 102;

    private static final UriMatcher sUriMatcher = createUriMatcher();

    public static UriMatcher createUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.FAVORITES_PATH, FAVORITES_CODE);
        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.FAVORITES_PATH + "/#", ONE_FAVORITE_CODE);
        uriMatcher.addURI(PopularMoviesContract.AUTHORITY, PopularMoviesContract.FAVORITES_PATH_BY_MOVIE_ID + "/*", ONE_FAVORITE_BY_MOVIE_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;

        String mSelection;
        String[] mSelectionArgs;

        switch (sUriMatcher.match(uri)){
            case FAVORITES_CODE:
                retCursor = Cache.openDatabase().query(PopularMoviesContract.FAVORITES_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ONE_FAVORITE_CODE:
                String id = uri.getPathSegments().get(1);

                mSelection = "_id=?";
                mSelectionArgs = new String[]{id};

                retCursor = Cache.openDatabase().query(PopularMoviesContract.FAVORITES_TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;
            case ONE_FAVORITE_BY_MOVIE_ID:
                String movieId = uri.getPathSegments().get(2);
                mSelection = PopularMoviesContract.FavoriteMovieEntry.MOVIE_ID_COLUMN + "=?";
                mSelectionArgs = new String[]{movieId};

                retCursor = Cache.openDatabase().query(PopularMoviesContract.FAVORITES_TABLE_NAME, projection, mSelection, mSelectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        Context context = getContext();
        if(context != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case FAVORITES_CODE:
                long id = Cache.openDatabase().insert(PopularMoviesContract.FAVORITES_TABLE_NAME, null, values);
                returnUri = ContentUris.withAppendedId(PopularMoviesContract.FavoriteMovieEntry.CONTENT_URI, id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        Context context = getContext();
        if(context != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String mSelection;
        String[] mSelectionArgs;

        switch (sUriMatcher.match(uri)){
            case ONE_FAVORITE_CODE:
                String id = uri.getPathSegments().get(1);

                mSelection = "_id=?";
                mSelectionArgs = new String[]{id};

                return Cache.openDatabase().delete(PopularMoviesContract.FAVORITES_TABLE_NAME, mSelection, mSelectionArgs);
            case ONE_FAVORITE_BY_MOVIE_ID:
                String movieId = uri.getPathSegments().get(2);
                mSelection = PopularMoviesContract.FavoriteMovieEntry.MOVIE_ID_COLUMN + "=?";
                mSelectionArgs = new String[]{movieId};

                return Cache.openDatabase().delete(PopularMoviesContract.FAVORITES_TABLE_NAME, mSelection, mSelectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Update method is not supported");
    }
}
