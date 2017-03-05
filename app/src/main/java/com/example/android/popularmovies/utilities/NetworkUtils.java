package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.Constants;
import com.example.android.popularmovies.model.DiscoveryDataResponse;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static android.content.ContentValues.TAG;

/**
 * Class with static utility methods for network requests.
 */
public class NetworkUtils {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_QUALITY = "w185";

    /**
     * Suffix in the URL used for sorting by popularity.
     */
    public static final String POPULAR_MOVIES_SUFFIX = "popular";

    /**
     * Suffix in the URL used for sorting by reting.
     */
    public static final String TOP_RATED_MOVIES_SUFFIX = "top_rated";

    private static final String MOVIES_PATH = "movie";
    private static final int REQUEST_TIMEOUT = 3000;

    private static final String KEY_QUERY_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";

    /**
     * Creates Uri.Builder with base url and API key.
     * @return Uri.Builder with basic request parameters.
     */
    private static Uri.Builder createUriBuilder(){
        return Uri.parse(BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(KEY_QUERY_PARAM, Constants.API_KEY);
    }

    /**
     *
     * @param imageUrlSuffix Creates the URI to the image.
     * @return Image uri
     */
    public static Uri buildImageUri(String imageUrlSuffix){
        return Uri.parse(IMAGE_BASE_URL).buildUpon()
                    .appendPath(IMAGE_QUALITY)
                    .appendEncodedPath(imageUrlSuffix)
                    .build();
    }

    /**
     * Creates URL to the discovery API with the given page and sorting order.
     * @param page Page of the items to be returned.
     * @param pathSuffix Suffix used to sort items.
     * @return Url to the discovery API
     */
    public static URL buildDiscoveryUrl(int page, String pathSuffix) {
        Uri builtUri = createUriBuilder()
                .appendEncodedPath(MOVIES_PATH)
                .appendEncodedPath(pathSuffix)
                .appendQueryParameter(PAGE_PARAM, Integer.toString(page))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    /**
     * Makes API call and reads and parses response.
     * @param url Url of the API.
     * @return Parsed response.
     * @throws IOException
     */
    public static DiscoveryDataResponse getResponseFromHttpUrl(URL url) throws IOException {
        Log.d(TAG, "Request: " + url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(REQUEST_TIMEOUT);
        urlConnection.setReadTimeout(REQUEST_TIMEOUT);
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return new DiscoveryDataResponse(scanner.next());
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }
}