package com.deange.marathontest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.deange.marathontest.R;
import com.deange.marathontest.Utils;
import com.deange.marathontest.controller.StateController;
import com.deange.marathontest.google.BaseGameActivity;
import com.deange.marathontest.google.CloudHelper;
import com.deange.marathontest.google.CloudInfo;
import com.google.android.gms.appstate.OnStateLoadedListener;

public class LoginActivity extends BaseGameActivity implements View.OnClickListener, OnStateLoadedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        findViewById(R.id.gplus_signin_button).setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(Utils.calculateWindowFlags());
        }
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
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.gplus_signin_button) {
            beginUserInitiatedSignIn();
        }
    }

    @Override
    public void onStateLoaded(final int statusCode, final int stateKey, final byte[] localData) {
        Log.v(TAG, "onStateLoaded()");

        if (localData != null) {
            final CloudInfo info = CloudHelper.convert(localData);
            StateController.getInstance().setMilesRan(info.getMilesRan());
        }
        startMarathonActivity();
    }

    @Override
    public void onStateConflict(final int stateKey, final String resolvedVersion, final byte[] localData, final byte[] serverData) {
        Log.v(TAG, "onStateConflict()");

        CloudInfo serverInfo = null;
        CloudInfo localInfo = null;

        if (serverData != null) {
            serverInfo = CloudHelper.convert(serverData);

        } else if (localData != null) {
            localInfo = CloudHelper.convert(localData);
        }

        final CloudInfo resolvedInfo = CloudHelper.resolveConflict(serverInfo, localInfo);

        if (resolvedInfo != null) {
            StateController.getInstance().setMilesRan(resolvedInfo.getMilesRan());
        }

        startMarathonActivity();
    }

    private void startMarathonActivity() {
        Log.v(TAG, "startMarathonActivity()");
        startActivity(new Intent(this, MainActivity.class));
    }

}
