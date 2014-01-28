package com.deange.marathonapp;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import com.deange.marathonapp.controller.AchievementsController;
import com.deange.marathonapp.controller.BillingController;
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

        try {
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

            /**
             * Instantiate the in-app purchasing controller
             */
            BillingController.createInstance(getApplicationContext());

            /**
             * Instantiate the achievements controller
             */
            AchievementsController.createInstance(getApplicationContext());

        } catch (final Exception ex) {
            Log.e(TAG, "Application not instantiated properly", ex);
        }


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
                    if (i > 0) sb.append(':');
                    final int oneByte = (int) bytes[i] & 0xff;
                    sb.append((oneByte < 0x10) ? "0" : "");
                    sb.append(Long.toString(oneByte, 16));
                }
                hashKey = sb.toString().toUpperCase();
            }

        } catch (final Exception e) {
            Log.e(TAG, "Failed to get the signing hash key.", e);
        }

        return hashKey;
    }

}
