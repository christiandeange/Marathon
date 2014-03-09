package com.deange.marathonapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deange.marathonapp.R;
import com.deange.marathonapp.ui.view.ShimmerTextView;
import com.deange.marathonapp.utils.Utils;
import com.deange.marathonapp.google.BaseGameActivity;

public class SplashActivity extends BaseGameActivity {

    private static final int WAIT_MINIMUM_DELAY = 2000;
    private static final int WAIT_INTERVAL_DELAY = 500;

    private boolean mReceivedResult = false;
    private boolean mIsSignedIn = false;

    private ShimmerTextView mTitleView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mTitleView = (ShimmerTextView) findViewById(R.id.splash_screen_title);
        mProgressBar = (ProgressBar) findViewById(R.id.splash_screen_progress_bar);

        mReceivedResult = false;

        mTitleView.animate(null, 1000);

        new AsyncTask<Void, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(final Void... params) {
                // Sleep a little bit
                Utils.sleepQuietly(WAIT_MINIMUM_DELAY);

                publishProgress(0);

                int waits = 0;
                while (waits < 10 && !mReceivedResult) {
                    // Wait up to 10 more times for 500ms (total of 5 extra seconds)
                    Utils.sleepQuietly(WAIT_INTERVAL_DELAY);
                    waits++;
                }

                publishProgress(100);

                return true;
            }

            @Override
            protected void onProgressUpdate(final Integer... value) {
                final int progress = value[0];

                // Show or hide the progress bar
                final int visibility = (progress == 100) ? View.GONE : View.VISIBLE;
                mProgressBar.setVisibility(visibility);
            }

            @Override
            protected void onPostExecute(final Boolean result) {
                resolveActivity();
            }
        }.execute();
    }

    private void resolveActivity() {

        // Decide which activity we should open. Decisions, decisions...
        final Class clazz = mIsSignedIn ? MainActivity.class : LoginActivity.class;
        final Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();

        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_down);
    }

    @Override
    public void onSignInFailed() {
        mIsSignedIn = false;
        mReceivedResult = true;
    }

    @Override
    public void onSignInSucceeded() {
        mIsSignedIn = true;
        mReceivedResult = true;
    }

    @Override
    public void onSignOutSucceeded() {
        mIsSignedIn = false;
        mReceivedResult = true;
    }
}
