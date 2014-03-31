package com.deange.marathonapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.deange.marathonapp.R;
import com.deange.marathonapp.google.BaseGameActivity;
import com.deange.marathonapp.google.CloudHelper;
import com.deange.marathonapp.utils.Utils;

public class SplashActivity
        extends BaseGameActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final int WAIT_MINIMUM_DELAY = 3000;
    private static final int WAIT_INTERVAL_DELAY = 500;

    private boolean mReceivedResult = false;
    private boolean mIsSignedIn = false;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mProgressBar = (ProgressBar) findViewById(R.id.splash_screen_progress_bar);

        mReceivedResult = false;

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

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

        CloudHelper.getState(new CloudHelper.SimpleStateListenerDelegate(), CloudHelper.KEY_GAME_STATE);

        if (getPlusClient().isConnected()) {
            final String accountName = getPlusClient().getAccountName();
            Log.v(TAG, "Account: " + accountName);
            Crashlytics.setUserEmail(accountName);
        }
    }

    @Override
    public void onSignOutSucceeded() {
        mIsSignedIn = false;
        mReceivedResult = true;
    }

}
