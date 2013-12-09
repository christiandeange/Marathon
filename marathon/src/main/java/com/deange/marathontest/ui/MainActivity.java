package com.deange.marathontest.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.deange.marathontest.utils.PlatformUtils;
import com.deange.marathontest.R;
import com.deange.marathontest.utils.Utils;
import com.deange.marathontest.controller.StateController;
import com.deange.marathontest.google.BaseGameActivity;

public class MainActivity
        extends BaseGameActivity
        implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private PopupMenu mPopupMenu;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

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
    public boolean onMenuItemClick(final MenuItem item) {

        boolean handled = false;

        switch (item.getItemId()) {
            case R.id.menu_logout:
                handled = true;
                signOut();
                break;
        }

        return handled;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setupActionBar() {

        final View overflowView = findViewById(R.id.activity_overflow_button);
        overflowView.setOnClickListener(this);

        mPopupMenu = new PopupMenu(this, overflowView);
        mPopupMenu.setOnMenuItemClickListener(this);
        mPopupMenu.inflate(R.menu.main_menu);

        if (PlatformUtils.hasKitKat()) {
            overflowView.setOnTouchListener(mPopupMenu.getDragToOpenListener());
        }
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.activity_overflow_button) {
            mPopupMenu.show();
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
