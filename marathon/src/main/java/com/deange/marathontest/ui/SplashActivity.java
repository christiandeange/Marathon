package com.deange.marathontest.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;

import com.deange.marathontest.R;
import com.deange.marathontest.Utils;
import com.deange.marathontest.google.BaseGameActivity;

public class SplashActivity extends BaseGameActivity {

    private static final int WAIT_DELAY = 2000;

    private boolean mIsSignedIn = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(final Void... params) {
                // Sleep a little bit
                Utils.sleepQuietly(WAIT_DELAY);
                return true;
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
    }

    @Override
    public void onSignInSucceeded() {
        mIsSignedIn = true;
    }

    @Override
    public void onSignOutSucceeded() {
        mIsSignedIn = false;
    }
}
