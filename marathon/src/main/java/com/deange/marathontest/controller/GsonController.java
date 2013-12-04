package com.deange.marathontest.controller;

import com.google.gson.Gson;

public class GsonController {

    private static final String TAG = GsonController.class.getSimpleName();

    private static final Object sLock = new Object();
    private static Gson sInstance;

    public static Gson getInstance() {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new Gson();
            }
            return sInstance;
        }
    }

    private GsonController() {
        // Uninstantiable
    }


}
