package com.deange.marathontest.controller;

import android.content.Context;
import android.content.SharedPreferences;

public class StateController {

    private static final String TAG = StateController.class.getSimpleName();

    private static final String PREFERENCES_NAME = TAG + ".prefs";
    private static final String KEY_MILES_RAN = "milesRan";

    private static final Object sLock = new Object();
    private static StateController sInstance;

    private SharedPreferences mPreferences;

    public static synchronized void createInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new StateController(context.getApplicationContext());
        }
    }

    public void setMilesRan(final int milesRan) {
        synchronized (sLock) {
            mPreferences.edit().putInt(KEY_MILES_RAN, milesRan).apply();
        }
    }

    public int getMilesRan() {
        synchronized (sLock) {
            return mPreferences.getInt(KEY_MILES_RAN, 0);
        }
    }

    public static StateController getInstance() {
        synchronized (sLock) {
            if (sInstance == null) {
                throw new IllegalStateException("StateController has not been created");
            }
            return sInstance;
        }
    }



    private StateController(final Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
