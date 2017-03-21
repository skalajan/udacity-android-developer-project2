package com.example.android.popularmovies.moviedetail;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.Constants;
import com.example.android.popularmovies.PopularMoviesActivity;
import com.example.android.popularmovies.PopularMoviesApplication;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.remote.discovery.MovieResult;
import com.example.android.popularmovies.utilities.CommonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity class with clicked movie detail.
 */
public class MovieDetailActivity extends PopularMoviesActivity{
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    protected MovieResult movieDetails;

    @BindView(R.id.iv_poster)
    protected ImageView mPosterImageView;
    @BindView(R.id.tv_original_name)
    protected TextView mOriginalTitleTextView;
    @BindView(R.id.tv_rating)
    protected TextView mRatingTextView;
    @BindView(R.id.rb_rating_stars)
    protected RatingBar mRatingBar;
    @BindView(R.id.tv_plot_synopsis)
    protected TextView mPlotSynopsisTextView;
    @BindView(R.id.tv_release_date)
    protected TextView mReleaseDateTextView;
    @BindView(R.id.pb_poster_loading)
    protected ProgressBar mPosterLoadingProgressBar;
    @BindView(R.id.iv_loading_poster_error)
    protected ImageView mLoadingPosterErrorImageView;
    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    /**
     * Inflates activity_movie_detail layout and sets the subviews according to the information for the selected movie from intent extras.
     * @param savedInstanceState Saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }

        String jsonString = getIntent().getStringExtra(Constants.MOVIE_DETAILS_EXTRA);
        if(jsonString != null){
            Gson gson = PopularMoviesApplication.getGson();
            movieDetails = gson.fromJson(jsonString, MovieResult.class);
            initView();
        }else{
            Log.e(TAG, "Json string extra not found");
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MovieTrailersFragment trailersFragment = MovieTrailersFragment.newInstance(movieDetails.getMovieId());
        MovieReviewsFragment reviewsFragment = MovieReviewsFragment.newInstance(movieDetails.getMovieId());

        fragmentTransaction.add(R.id.trailers_fragment, trailersFragment);
        fragmentTransaction.add(R.id.reviews_fragment, reviewsFragment);

        fragmentTransaction.commit();
    }

    /**
     * Gets the information about the movie from the movieDetails and sets the views
     */
    private void initView() {
        String movieTitle = movieDetails.getTitle();
        String originalTitle = movieDetails.getOriginalTitle();
        String voteAverage = movieDetails.getVoteAverage();
        String plotSynopsis = movieDetails.getOverview();
        String releaseDateString = movieDetails.getReleaseDate();

        if(movieTitle != null && !"null".equals(movieTitle)) {
            setTitle(movieTitle);
        }

        if(originalTitle != null && !"null".equals(originalTitle)){
            mOriginalTitleTextView.setText(originalTitle);
        }else{
            findViewById(R.id.ll_original_name).setVisibility(View.GONE);
        }

        if(voteAverage != null && !"null".equals(voteAverage)){
            float rating = Float.parseFloat(voteAverage);
            mRatingTextView.setText(String.format(Locale.getDefault(),"%.1f",rating));
            mRatingBar.setRating(rating/2);
        }else{
            findViewById(R.id.ll_rating).setVisibility(View.GONE);
        }

        if(plotSynopsis != null && !"null".equals(plotSynopsis)){
            mPlotSynopsisTextView.setText(plotSynopsis);
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
            mReleaseDateTextView.setText(formattedRelease);
        }else{
            findViewById(R.id.ll_release_date).setVisibility(View.GONE);
        }

        loadPosterImage();

    }

    /**
     * Inflates the menu with heart icon to remove/add movie to favorites.
     * @param menu Menu to be inflated
     * @return True to show the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_detail, menu);

        if(MovieResult.isInFavorites(movieDetails.getMovieId())){
            menu.findItem(R.id.favorite_menu_item).setIcon(R.drawable.ic_favorite_white_24dp);
        }
        return true;
    }

    /**
     * Listens to click on sorting button.
     * @param item Clicked menu item
     * @return True if the delegation end here
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.favorite_menu_item) {
            toggleFavorite(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Removes/adds item to the favorites.
     * @param item Clicked menu item.
     */
    private void toggleFavorite(MenuItem item){
        if(MovieResult.isInFavorites(movieDetails.getMovieId())){
            MovieResult.removeFromFavorites(movieDetails.getMovieId());
            item.setIcon(R.drawable.ic_favorite_border_white_24dp);
        }else{
            MovieResult.addToFavorites(this.movieDetails);
            item.setIcon(R.drawable.ic_favorite_white_24dp);
        }
    }

    /**
     * Loads movie poster into mPosterImageView
     */
    private void loadPosterImage() {
        String posterUrl;
        posterUrl = movieDetails.getPosterPath();
        if (posterUrl != null && !"null".equals(posterUrl)) {
            Uri imageUri = NetworkUtils.buildImageUri(posterUrl);
            Log.d(TAG, "Loading image " + imageUri.toString());
            Picasso.with(this)
                    .load(imageUri)
                    .into(mPosterImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            mPosterLoadingProgressBar.setVisibility(View.GONE);
                            mPosterImageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            mPosterLoadingProgressBar.setVisibility(View.INVISIBLE);
                            mLoadingPosterErrorImageView.setVisibility(View.VISIBLE);
                        }
                    });
        }else{
            Log.d(TAG, "Null image url");
        }
    }
}
