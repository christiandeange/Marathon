package com.deange.marathontest;

import android.app.Application;
import android.util.Log;

import com.deange.marathontest.controller.StateController;

public class MainApplication extends Application {

    private static final String TAG = MainApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate()");
        super.onCreate();

        StateController.createInstance(getApplicationContext());

    }

}
