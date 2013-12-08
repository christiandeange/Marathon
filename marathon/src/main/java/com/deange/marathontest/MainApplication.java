package com.deange.marathontest;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.deange.marathontest.controller.GoogleClients;
import com.deange.marathontest.controller.GsonController;
import com.deange.marathontest.controller.StateController;

public class MainApplication extends Application {

    private static final String TAG = MainApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate()");
        super.onCreate();

        /**
         * Instantiate the GSON cache
         */
        GsonController.getInstance();

        /**
         * Instantiate the state manager & shared preferences
         */
        StateController.createInstance(getApplicationContext());

        /**
         * Instantiate the Google Play clients
         */
        GoogleClients.createInstance(getApplicationContext());

    }

}
