package com.deange.marathonapp.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.deange.marathonapp.billing.BillingConstants;
import com.deange.marathonapp.billing.IabException;
import com.deange.marathonapp.billing.IabHelper;
import com.deange.marathonapp.billing.IabResult;
import com.deange.marathonapp.billing.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingController {

    private static final String TAG = BillingController.class.getSimpleName();

    public static final int PURCHASE_REQUEST_CODE = 10001;

    private static final Object sLock = new Object();
    private static Map<Activity, BillingController> sInstances =
            Collections.synchronizedMap(new HashMap<Activity, BillingController>());

    private Activity mActivity;
    private IabHelper mHelper;

    public static BillingController getInstance(final Activity activity) {
        synchronized (sLock) {
            BillingController instance = sInstances.get(activity);
            if (instance == null) {
                instance = new BillingController(activity);
                sInstances.put(activity, instance);
            }
            return instance;
        }
    }

    private BillingController(final Activity activity) {
        mActivity = activity;
        mHelper = new IabHelper(activity, BillingConstants.BASE_64_KEY);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(final IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
            }
        });
    }

    public void removeInstance(final Activity activity) {
        sInstances.remove(activity);
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    // Synchronous
    public Inventory queryInventory() throws IabException {
        return mHelper.queryInventory(true, null);
    }

    // Asynchronous
    public void queryInventory(final IabHelper.QueryInventoryFinishedListener listener) {
        mHelper.queryInventoryAsync(listener);
    }

    // Asynchronous
    public void purchase(final String sku, final IabHelper.OnIabPurchaseFinishedListener listener) {
        mHelper.launchPurchaseFlow(mActivity, sku, PURCHASE_REQUEST_CODE,
                listener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
    }

}