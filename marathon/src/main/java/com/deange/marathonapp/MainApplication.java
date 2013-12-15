package com.deange.marathonapp;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import com.deange.marathonapp.controller.GoogleClients;
import com.deange.marathonapp.controller.GsonController;
import com.deange.marathonapp.controller.StateController;

import java.security.MessageDigest;

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


        Log.i(TAG, "Signing key hash: " + getSignatureHashKey(getApplicationContext()));

    }

    public static String getSignatureHashKey(final Context context) {
        String hashKey = null;

        try {
            final PackageInfo info = context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                final byte[] bytes = md.digest();
                final StringBuilder sb = new StringBuilder();
                for (int i = 0; i < bytes.length; i++) {
                    if (((int) bytes[i] & 0xff) < 0x10) {
                        sb.append("0");
                    }
                    sb.append(Long.toString((int) bytes[i] & 0xff, 16));

                    if (i != bytes.length - 1) {
                        sb.append(':');
                    }
                }
                hashKey = sb.toString().toUpperCase();
            }

        } catch (final Exception e) {
            Log.e(TAG, "Failed to get the signing hash key.", e);
        }

        return hashKey;
    }

}
