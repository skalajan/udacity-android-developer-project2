package com.example.android.popularmovies.discovery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.android.popularmovies.PopularMoviesActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.RequestFailedListener;


/**
 * Abstract activity class that checks internet connectivity and reacts to the failed network requests. Shows the network error box and retries the request when try again button pressed.
 *
 * TODO LISTENS TO THE NETWORK STATUS CHANGE AND TRY TO LOAD AGAIN AUTOMATICALLY
 */
public abstract class NetworkAccessingActivity extends PopularMoviesActivity implements  View.OnClickListener, RequestFailedListener {
    protected RelativeLayout networkErrorBoxRelativeLayout;
    protected Button tryAgainButton;

    protected boolean networkError;

    protected abstract void tryRequestAgain();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     /*   networkErrorBoxRelativeLayout = (RelativeLayout) findViewById(R.id.rl_error_message_box);
        tryAgainButton = (Button) findViewById(R.id.btn_try_again);

        tryAgainButton.setOnClickListener(this);

        if(!isNetworkAvailable()) {
            networkError = true;
            showNetworkFailedBox();
        }
        */
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
    public void onTimeout() {
        onRequestNotCompleted();
    }

    /**
     * Reacts in the request failed.
     */
    @Override
    public void onFail() {
        onRequestNotCompleted();
    }
}
