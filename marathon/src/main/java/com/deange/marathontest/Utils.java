package com.deange.marathontest;

import android.os.AsyncTask;
import android.os.Looper;

public final class Utils {

    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void runAsynchronouslyIfNecessary(final Runnable runnable) {
        if (isOnMainThread()) {
            runnable.run();

        } else {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(final Void... params) {
                    runnable.run();
                    return null;
                }
            }.execute();
        }
    }

    private Utils() {
        // Uninstantiable
    }
}
