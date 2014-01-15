package com.deange.marathonapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.deange.marathonapp.R;
import com.deange.marathonapp.utils.Utils;
import com.deange.marathonapp.controller.StateController;
import com.deange.marathonapp.google.BaseGameActivity;
import com.deange.marathonapp.google.CloudHelper;
import com.deange.marathonapp.model.CloudInfo;
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

        final CloudInfo serverInfo = CloudHelper.convert(serverData);
        final CloudInfo localInfo = CloudHelper.convert(localData);

        // Manually resolve the conflict
        final CloudInfo resolvedInfo = CloudHelper.resolveConflict(serverInfo, localInfo);

        // Signal to the AppStateClient that we have resolved the right version of the info
        getAppStateClient().resolveState(this, stateKey, resolvedVersion, CloudHelper.convert(resolvedInfo));

        startMarathonActivity();
    }

    private void startMarathonActivity() {
        Log.v(TAG, "startMarathonActivity()");
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
