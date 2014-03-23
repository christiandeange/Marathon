package com.deange.marathonapp.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.deange.marathonapp.utils.Utils;

public class ImmersiveActivity
        extends FragmentActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(Utils.calculateWindowFlags());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(Utils.calculateWindowFlags());
        }
    }
}
