package com.deange.marathontest.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.deange.marathontest.R;
import com.deange.marathontest.Utils;
import com.deange.marathontest.google.BaseGameActivity;

public class MainActivity extends BaseGameActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MarathonFragment.TAG);
        if (fragment == null) {
            fragment = new MarathonFragment();
        }

        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, MarathonFragment.TAG)
                    .commit();
        }
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
    }
}
