package com.deange.marathontest.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.deange.marathontest.R;
import com.deange.marathontest.Utils;
import com.deange.marathontest.controller.StateController;
import com.deange.marathontest.google.BaseGameActivity;

public class MainActivity extends BaseGameActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        final int fullscreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(fullscreenFlag, fullscreenFlag);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();

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
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item) {

        boolean handled = false;

        switch (item.getItemId()) {
            case R.id.menu_logout:
                handled = true;
                signOut();
        }


        return handled || super.onMenuItemSelected(featureId, item);
    }

    private void setupActionBar() {
        final ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(Utils.calculateWindowFlags());
        }
    }

    @Override
    public void onSignInSucceeded() {
    }

    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignOutSucceeded() {

        // Remove all prefs
        StateController.getInstance().clear();

        // Head back to the LoginActivity!
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
