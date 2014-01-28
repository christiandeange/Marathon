package com.deange.marathonapp.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.deange.marathonapp.billing.BillingConstants;
import com.deange.marathonapp.billing.IabException;
import com.deange.marathonapp.billing.IabHelper;
import com.deange.marathonapp.billing.IabResult;
import com.deange.marathonapp.model.Inventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class BillingController {

    private static final String TAG = BillingController.class.getSimpleName();

    public static final int PURCHASE_REQUEST_CODE = 10001;

    private static final Object sLock = new Object();
    private static BillingController sInstance;

    private IabHelper mHelper;

    public static BillingController createInstance(final Context context) {
        synchronized (sLock) {
            sInstance = new BillingController(context.getApplicationContext());
        }
        return sInstance;
    }

    public static BillingController getInstance() {
        synchronized (sLock) {
            if (sInstance == null) {
                throw new IllegalStateException("BillingController not instantiated");
            }
            return sInstance;
        }
    }

    private BillingController(final Context context) {
        mHelper = new IabHelper(context, BillingConstants.BASE_64_KEY);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(final IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.w(TAG, "Problem setting up In-app Billing: " + result);
                }
            }
        });
    }

    // Synchronous
    public Inventory queryInventory() throws IabException {
        synchronized (sLock) {
            return mHelper.queryInventory(true, null);
        }
    }

    // Asynchronous
    public void queryInventory(final IabHelper.QueryInventoryFinishedListener listener) {
        synchronized (sLock) {
            mHelper.queryInventoryAsync(listener);
        }
    }

    // Asynchronous
    public void purchase(final Activity activity, final String sku,
                         final IabHelper.OnIabPurchaseFinishedListener listener) {
        synchronized (sLock) {
            mHelper.launchPurchaseFlow(activity, sku, PURCHASE_REQUEST_CODE,
                    listener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
        }
    }

    public void onActivityResult(final int request, final int response, final Intent data) {
        synchronized (sLock) {
            mHelper.handleActivityResult(request, response, data);
        }
    }
}