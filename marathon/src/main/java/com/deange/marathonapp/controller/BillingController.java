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
import com.deange.marathonapp.utils.ProcessList;

public final class BillingController {

    public static final String TAG = BillingController.class.getSimpleName();
    public static final int PURCHASE_REQUEST_CODE = 10001;

    private static final Object sLock = new Object();
    private static BillingController sInstance;

    private final IabHelper mHelper;
    private final ProcessList mProcessList;

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
        mProcessList = new ProcessList();
        mHelper = new IabHelper(context, BillingConstants.BASE_64_KEY);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(final IabResult result) {
                if (result.isSuccess()) {
                    // If there is no issue, we should run all of our backed-up processes.
                    mProcessList.processAll();

                } else {
                    // Oh noes, there was a problem!
                    // Sorry waiters, but we can't process your things
                    Log.w(TAG, "Problem setting up In-app Billing: " + result);
                    mProcessList.clear();
                }

            }
        });
    }

    // Synchronous
    public Inventory queryInventory() throws IabException {
        synchronized (sLock) {
            if (mHelper.isSetupDone()) {
                return mHelper.queryInventory(true, null);

            } else {
                return null;
            }
        }
    }

    // Asynchronous
    public void queryInventory(final IabHelper.QueryInventoryFinishedListener listener) {
        synchronized (sLock) {

            final Runnable queryProcess = new Runnable() {
                @Override
                public void run() {
                    mHelper.queryInventoryAsync(listener);
                }
            };


            if (mHelper.isSetupDone()) {
                queryProcess.run();

            } else {
                mProcessList.admitProcess(queryProcess);
            }

        }
    }

    // Asynchronous
    public void purchase(final Activity activity, final String sku,
                         final IabHelper.OnIabPurchaseFinishedListener listener) {
        synchronized (sLock) {

            final Runnable purchaseProcess = new Runnable() {
                @Override
                public void run() {
                    mHelper.launchPurchaseFlow(activity, sku, PURCHASE_REQUEST_CODE,
                            listener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                }
            };


            if (mHelper.isSetupDone()) {
                purchaseProcess.run();

            } else {
                mProcessList.admitProcess(purchaseProcess);
            }
        }
    }

    public void onActivityResult(final int request, final int response, final Intent data) {
        synchronized (sLock) {

            final Runnable resultProcess = new Runnable() {
                @Override
                public void run() {
                    mHelper.handleActivityResult(request, response, data);
                }
            };

            if (mHelper.isSetupDone()) {
                resultProcess.run();

            } else {
                mProcessList.admitProcess(resultProcess);
            }

        }
    }
}