package com.deange.marathonapp.controller;

import android.content.Context;
import android.content.SharedPreferences;
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
    private static Map<Context, BillingController> sInstances =
            Collections.synchronizedMap(new HashMap<Context, BillingController>());

    private Context mContext;
    private IabHelper mHelper;

    public static BillingController getInstance(final Context context) {
        synchronized (sLock) {
            BillingController instance = sInstances.get(context);
            if (instance == null) {
                instance = new BillingController(context);
            }
            return instance;
        }
    }

    private BillingController(final Context context) {
        mContext = context;
        mHelper = new IabHelper(context, BillingConstants.BASE_64_KEY);

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

    public void removeInstance(final Context context) {
        sInstances.remove(context);
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

}
