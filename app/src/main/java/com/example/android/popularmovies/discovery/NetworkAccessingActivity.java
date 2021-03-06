package com.example.android.popularmovies.discovery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.android.popularmovies.PopularMoviesActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.RequestFailedEvent;
import com.example.android.popularmovies.model.RequestFailedSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * Abstract activity class that checks internet connectivity and reacts to the failed network requests. Shows the network error box and retries the request when try again button pressed.
 *
 */
public abstract class NetworkAccessingActivity extends PopularMoviesActivity implements  View.OnClickListener, RequestFailedSubscriber {
    protected RelativeLayout networkErrorBoxRelativeLayout;
    protected Button tryAgainButton;

    protected boolean networkError;

    protected abstract void tryRequestAgain();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        networkErrorBoxRelativeLayout = (RelativeLayout) findViewById(R.id.rl_error_message_box);
        tryAgainButton = (Button) findViewById(R.id.btn_try_again);

        tryAgainButton.setOnClickListener(this);

        if(!isNetworkAvailable()) {
            networkError = true;
            showNetworkFailedBox();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    /**
     * Reacts to the try again button pressed.
     * @param v Clicked view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_try_again:
                if(isNetworkAvailable()) {
                    tryRequestAgain();
                    hideNetworkFailedBox();
                }
                break;
        }
    }

    /**
     * Checks if the device is connected to the network.
     * @return Device connectivity status
     */
    protected boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    /**
     * Reacts to the network error. Triggered after failed/timeout request.
     */
    protected void onRequestNotCompleted(){
        networkError = true;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showNetworkFailedBox();
            }
        });
    }

    /**
     * Shows network failed box
     */
     protected void showNetworkFailedBox() {
        networkErrorBoxRelativeLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Hides network failed box
     */
    protected void hideNetworkFailedBox(){
        networkErrorBoxRelativeLayout.setVisibility(View.GONE);
    }

    /**
     * Reacts on the request timeout.
     */
    @Override
    public void onRequestTimeout() {
        onRequestNotCompleted();
    }

    /**
     * Listens to the request failed event from the EventBus.
     * @param event Event object.
     */
    @Override
    @Subscribe
    public void onRequestFailed(RequestFailedEvent event) {
        onRequestNotCompleted();
    }
}
