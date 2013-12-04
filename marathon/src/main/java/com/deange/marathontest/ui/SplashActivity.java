package com.deange.marathontest.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.deange.marathontest.R;
import com.deange.marathontest.google.BaseGameActivity;

public class SplashActivity extends BaseGameActivity {

    private static final int WAIT_DELAY = 3000;

    private boolean mIsSignedIn = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                try {
                    Thread.sleep(WAIT_DELAY);
                } catch (InterruptedException ignored) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {
                resolveActivity();
            }
        }.execute();
    }

    private void resolveActivity() {
        final Class clazz = mIsSignedIn ? MainActivity.class : LoginActivity.class;
        final Intent intent = new Intent(this, clazz);

        startActivity(intent);
        finish();

        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_down);
    }

    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {
        mIsSignedIn = true;
    }
}
