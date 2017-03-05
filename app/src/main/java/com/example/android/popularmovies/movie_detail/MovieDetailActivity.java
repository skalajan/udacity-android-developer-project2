package com.example.android.popularmovies.movie_detail;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.Constants;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.utilities.CommonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Activity class with clicked movie detail.
 */
public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    protected JSONObject movieDetails;
    protected ImageView posterImageView;
    protected TextView originalTitleTextView;
    protected TextView ratingTextView;
    protected RatingBar ratingBar;
    protected TextView plotSynopsisTextView;
    protected TextView releaseDateTextView;

    /**
     * Inflates activity_movie_detail layout and sets the subviews according to the information for the selected movie from intent extras.
     * @param savedInstanceState Saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        String jsonString = getIntent().getStringExtra(Constants.MOVIE_DETAILS_EXTRA);
        if(jsonString != null){
            try {
                movieDetails = new JSONObject(jsonString);
                initView();
            } catch (JSONException e) {
                Log.e(TAG, "Could not parse the movies detail from extras");
                e.printStackTrace();
            }
        }else{
            Log.e(TAG, "Json string extra not found");
        }
    }

    /**
     * Gets the information about the movie from the movieDetails and sets the views
     */
    private void initView() {
        originalTitleTextView = (TextView) findViewById(R.id.tv_original_name);
        posterImageView = (ImageView) findViewById(R.id.iv_poster);
        ratingTextView = (TextView) findViewById(R.id.tv_rating);
        ratingBar = (RatingBar) findViewById(R.id.rb_rating_stars);
        plotSynopsisTextView = (TextView) findViewById(R.id.tv_plot_synopsis);
        releaseDateTextView = (TextView) findViewById(R.id.tv_release_date);

        String movieTitle = movieDetails.optString("title");
        String originalTitle = movieDetails.optString("original_title");
        String voteAverage = movieDetails.optString("vote_average");
        String plotSynopsis = movieDetails.optString("overview");
        String releaseDateString = movieDetails.optString("release_date");

        if(movieTitle != null && !"null".equals(movieTitle)) {
            setTitle(movieTitle);
        }

        if(originalTitle != null && !"null".equals(originalTitle)){
            originalTitleTextView.setText(originalTitle);
        }else{
            findViewById(R.id.ll_original_name).setVisibility(View.GONE);
        }

        if(voteAverage != null && !"null".equals(voteAverage)){
            float rating = Float.parseFloat(voteAverage);
            ratingTextView.setText(String.format(Locale.getDefault(),"%.1f",rating));
            ratingBar.setRating(rating/2);
        }else{
            findViewById(R.id.ll_rating).setVisibility(View.GONE);
        }

        if(plotSynopsis != null && !"null".equals(plotSynopsis)){
            plotSynopsisTextView.setText(plotSynopsis);
        }else{
            findViewById(R.id.ll_plot_synopsis).setVisibility(View.GONE);
        }

        if(releaseDateString != null && !"null".equals(releaseDateString)){
            Date releaseDate = null;
            try {
                releaseDate = CommonUtils.parseDateFromReceivedString(releaseDateString);
            } catch (ParseException e) {
                e.printStackTrace();
                Log.w(TAG, "Could not reformat the received release date");
                findViewById(R.id.ll_release_date).setVisibility(View.GONE);
            }
            String formattedRelease = CommonUtils.formatDateToHumanReadableString(releaseDate);
            releaseDateTextView.setText(formattedRelease);
        }else{
            findViewById(R.id.ll_release_date).setVisibility(View.GONE);
        }

        loadPosterImage();

    }

    /**
     * Loads movie poster into posterImageView
     */
    private void loadPosterImage() {
        String posterUrl;
        posterUrl = movieDetails.optString("poster_path");
        if (posterUrl != null && !"null".equals(posterUrl)) {
            Uri imageUri = NetworkUtils.buildImageUri(posterUrl);
            Log.d(TAG, "Loading image " + imageUri.toString());
            Picasso.with(this)
                    .load(imageUri)
                    .into(posterImageView);
        }else{
            Log.d(TAG, "Null image url");
        }
    }
}
