package com.deange.marathonapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.deange.marathonapp.R;
import com.deange.marathonapp.google.BaseGameActivity;
import com.deange.marathonapp.google.CloudHelper;
import com.deange.marathonapp.ui.view.ShimmerTextView;
import com.google.android.gms.appstate.OnStateLoadedListener;

public class LoginActivity
        extends BaseGameActivity
        implements View.OnClickListener, OnStateLoadedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        findViewById(R.id.auth_gplus_signin_button).setOnClickListener(this);

        final ShimmerTextView titleView = (ShimmerTextView) findViewById(R.id.auth_title);
        titleView.animate(null, 2000);
    }

    @Override
    public void onSignInFailed() {
        // Nothing to do here
    }

    @Override
    public void onSignOutSucceeded() {
        // Nothing to do here
    }

    @Override
    public void onSignInSucceeded() {
        // Try to load the previous state of the user
        Log.v(TAG, "onSignInSucceeded()");
        CloudHelper.getState(this, CloudHelper.KEY_GAME_STATE);

        if (getPlusClient().isConnected()) {
            final String accountName = getPlusClient().getAccountName();
            Log.v(TAG, "Account: " + accountName);
            Crashlytics.setUserEmail(accountName);
        }
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.auth_gplus_signin_button) {
            beginUserInitiatedSignIn();
        }
    }

    @Override
    public void onStateLoaded(final int statusCode, final int stateKey, final byte[] localData) {
        Log.v(TAG, "onStateLoaded()");

        CloudHelper.onStateLoaded(statusCode, stateKey, localData);
        startMarathonActivity();
    }

    @Override
    public void onStateConflict(final int stateKey, final String resolvedVersion, final byte[] localData, final byte[] serverData) {
        Log.v(TAG, "onStateConflict()");

        CloudHelper.onStateConflict(stateKey, resolvedVersion, localData, serverData, getAppStateClient(), this);
        startMarathonActivity();
    }

    private void startMarathonActivity() {
        Log.v(TAG, "startMarathonActivity()");

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
