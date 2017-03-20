package com.example.android.popularmovies.moviedetail;

import android.content.Intent;
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
import com.example.android.popularmovies.model.remote.trailer.TrailersResponse;
import com.example.android.popularmovies.utilities.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link Fragment} subclass for downloading and showing trailers of the selected movie.
 * Use the {@link MovieTrailersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieTrailersFragment extends Fragment implements LoaderManager.LoaderCallbacks<TrailersResponse>, RequestFailedListener, OnTrailerClickListener {
    private static final String MOVIE_ID_ARG = "MOVIE_ID_ARG";

    private long movieId;

    @BindView(R.id.pb_trailers_loading)
    protected ProgressBar mLoadingProgressBar;
    @BindView(R.id.rv_movie_trailers)
    protected RecyclerView mRecyclerView;

    protected TrailersResponse data;
    protected TrailersAdapter adapter;


    /**
     * Empty public constructor. Use factory method instead.
     */
    public MovieTrailersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId Id of the movie which trailers should be shown.
     * @return A new instance of fragment MovieTrailersFragment.
     */
    public static MovieTrailersFragment newInstance(long movieId) {
        MovieTrailersFragment fragment = new MovieTrailersFragment();
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

        adapter = new TrailersAdapter();
        getLoaderManager().restartLoader(Constants.TRAILERS_LOADER_ID, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_trailers, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setNestedScrollingEnabled(false);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

        return view;
    }

    private void dataReceived(TrailersResponse data){
        this.data = data;
        adapter.notifyDataSetChanged();
    }

    private void hideLoadingBar(){
        mLoadingProgressBar.setVisibility(View.GONE);
    }


    @Override
    public Loader<TrailersResponse> onCreateLoader(int id, Bundle args) {
        return new MovieDetailLoader<>(getContext(), movieId, NetworkUtils.TRAILERS_SUFFIX, TrailersResponse.class);
    }

    @Override
    public void onLoadFinished(Loader<TrailersResponse> loader, TrailersResponse data) {
        if(data == null){
            this.onRequestFailed();
        }else{
            dataReceived(data);
        }
        hideLoadingBar();
    }

    @Override
    public void onLoaderReset(Loader<TrailersResponse> loader) {

    }

    @Override
    public void onRequestTimeout() {

    }

    @Override
    public void onRequestFailed() {

    }

    @Override
    public void onTrailerClicked(int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW, NetworkUtils.createUriToYoutube(data.getResults().get(position).getKey()));
        startActivity(intent);
    }


    /**
     * Adapter class for the trailers.
     */
    private class TrailersAdapter extends RecyclerView.Adapter{
        private final String TAG = TrailersAdapter.class.getSimpleName();

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
            TrailersViewHolder holder =  new TrailersViewHolder(itemView);
            holder.setOnTrailerClickListener(MovieTrailersFragment.this);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TrailersViewHolder viewHolder = (TrailersViewHolder) holder;
            viewHolder.setTitle(data.getResults().get(position).getName());
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
     * Trailers view holder class.
     */
    class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_trailer_title)
        TextView mTrailerTitle;

        private OnTrailerClickListener clickListener;

        /**
         * Creates view holder for the view;
         * @param itemView View.
         */
        TrailersViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        /**
         * Sets title of the trailer.
         * @param trailerTitle Title to show.
         */
        void setTitle(String trailerTitle) {
            this.mTrailerTitle.setText(trailerTitle);
        }

        /**
         * Sets listener to the trailer click event.
         * @param listener Listener of the click.
         */
        void setOnTrailerClickListener(OnTrailerClickListener listener){
            this.clickListener = listener;
        }

        @Override
        public void onClick(View v) {
            if(clickListener != null)
                clickListener.onTrailerClicked(getAdapterPosition());
        }
    }

}
