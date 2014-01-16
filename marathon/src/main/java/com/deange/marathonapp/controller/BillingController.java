package com.deange.marathonapp.controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.deange.marathonapp.billing.BillingConstants;
import com.deange.marathonapp.billing.IabHelper;
import com.deange.marathonapp.billing.IabResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BillingController {

    private static final String TAG = BillingController.class.getSimpleName();

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

}
