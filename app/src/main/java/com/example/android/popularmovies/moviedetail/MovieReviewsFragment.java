package com.example.android.popularmovies.moviedetail;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.Constants;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.RequestFailedListener;
import com.example.android.popularmovies.model.loader.MovieDetailLoader;
import com.example.android.popularmovies.model.remote.review.ReviewsResponse;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link Fragment} subclass downloading and showing reviews of the selected movie.
 * Use the {@link MovieReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ReviewsResponse>, RequestFailedListener{
    private static final String MOVIE_ID_ARG = "MOVIE_ID_ARG";

    private long movieId;

    @BindView(R.id.pb_reviews_loading)
    protected ProgressBar mLoadingProgressBar;
    @BindView(R.id.rv_movie_reviews)
    protected RecyclerView mRecyclerView;

    protected ReviewsResponse data;
    protected ReviewAdapter adapter;


    /**
     * Empty public constructor. Use factory method instead.
     */
    public MovieReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId Id of the movie which trailers should be shown.
     * @return A new instance of fragment MovieTrailersFragment.
     */
    public static MovieReviewsFragment newInstance(long movieId) {
        MovieReviewsFragment fragment = new MovieReviewsFragment();
        Bundle args = new Bundle();
        args.putLong(MOVIE_ID_ARG, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getLong(MOVIE_ID_ARG);
        }

        adapter = new ReviewAdapter();
        getLoaderManager().restartLoader(Constants.REVIEWS_LOADER_ID, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_reviews, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setNestedScrollingEnabled(false);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        return view;
    }

    private void dataReceived(ReviewsResponse data){
        this.data = data;
        adapter.notifyDataSetChanged();
    }

    private void hideLoadingBar(){
        mLoadingProgressBar.setVisibility(View.GONE);
    }


    @Override
    public Loader<ReviewsResponse> onCreateLoader(int id, Bundle args) {
        return new MovieDetailLoader<>(getContext(), movieId, NetworkUtils.REVIEWS_SUFFIX, ReviewsResponse.class);
    }

    @Override
    public void onLoadFinished(Loader<ReviewsResponse> loader, ReviewsResponse data) {
        if(data == null){
            this.onRequestFailed();
        }else{
            dataReceived(data);
        }
        hideLoadingBar();
    }

    @Override
    public void onLoaderReset(Loader<ReviewsResponse> loader) {

    }

    @Override
    public void onRequestTimeout() {

    }

    @Override
    public void onRequestFailed() {

    }


    private class ReviewAdapter extends RecyclerView.Adapter{
        private final String TAG = ReviewAdapter.class.getSimpleName();

        private HashSet<Integer> expandState = new HashSet<>();



        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
            return new ReviewViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            final ReviewViewHolder viewHolder = (ReviewViewHolder) holder;
            viewHolder.setAuthor(data.getResults().get(position).getAuthor());
            viewHolder.setContent(data.getResults().get(position).getContent());
            viewHolder.mExpandableLinearLayout.setInterpolator(Utils.createInterpolator(Utils.ACCELERATE_DECELERATE_INTERPOLATOR));
            viewHolder.mExpandableLinearLayout.setInRecyclerView(true);
            viewHolder.mExpandableLinearLayout.setListener(new ExpandableLayoutListenerAdapter() {
                @Override
                public void onPreOpen() {
                    createRotateAnimator(viewHolder.rotatingArrowView, 0f, 180f).start();
                    expandState.add(holder.getAdapterPosition());
                }

                @Override
                public void onPreClose() {
                    createRotateAnimator(viewHolder.rotatingArrowView, 180f, 0f).start();
                    expandState.remove(holder.getAdapterPosition());
                }
            });
            viewHolder.rotatingArrowView.setRotation(expandState.contains(position) ? 180f : 0f);

            holder.setIsRecyclable(false);

        }

        @Override
        public int getItemCount() {
            int c = 0;
            if(data != null) {
                c = data.getResults().size();
            }
            Log.v(TAG, "item count " + c);
            return c;
        }
    }

    /**
     * Review view holder.
     */
    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_review_author)
        TextView mReviewAuthor;

        @BindView(R.id.tv_review_content)
        TextView mReviewContent;

        @BindView(R.id.ell_expandable_review)
        ExpandableLinearLayout mExpandableLinearLayout;

        @BindView(R.id.v_arrow_down)
        View rotatingArrowView;

        ReviewViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        /**
         * Sets the author of the review.
         * @param author Author of the review.
         */
        void setAuthor(String author) {
            this.mReviewAuthor.setText(getString(R.string.authors_review, author));
        }

        /**
         * Sets the content of the review.
         * @param content Content of the review
         */
        void setContent(String content) {
            this.mReviewContent.setText(content);
        }

        /**
         * Toggles expandable layout.
         * @param v Clicked view.
         */
        @Override
        public void onClick(View v) {
            mExpandableLinearLayout.toggle();
        }
    }

    /**
     * Animator for the rotation of the arrow when expanding/collapsing review content.
     * @param target View that should be rotated.
     * @param from Starting rotation.
     * @param to Ending rotation.
     * @return ObjectAnimator
     */
    public ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

}
