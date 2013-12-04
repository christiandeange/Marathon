package com.deange.marathontest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.deange.marathontest.R;
import com.deange.marathontest.Utils;
import com.deange.marathontest.google.BaseGameActivity;

public class LoginActivity extends BaseGameActivity implements View.OnClickListener {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

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

    }

    @Override
    public void onSignInSucceeded() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.gplus_signin_button) {
            beginUserInitiatedSignIn();
        }
    }
}
