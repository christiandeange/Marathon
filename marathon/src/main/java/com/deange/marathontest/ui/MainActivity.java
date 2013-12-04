package com.deange.marathontest.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.deange.marathontest.R;
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
            getWindow().getDecorView().setSystemUiVisibility(calculateWindowFlags());
        }
    }

    private static int calculateWindowFlags() {

        int windowFlags = 0;

        windowFlags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            windowFlags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            windowFlags |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            windowFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            windowFlags |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            windowFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        return windowFlags;
    }


    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {
    }
}
