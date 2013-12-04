package com.deange.marathontest;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.view.View;

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

    public static int calculateWindowFlags() {

        int windowFlags = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            windowFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        return windowFlags;
    }

    private Utils() {
        // Uninstantiable
    }
}
