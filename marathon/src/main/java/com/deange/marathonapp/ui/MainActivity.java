package com.deange.marathonapp.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.deange.marathonapp.BuildConfig;
import com.deange.marathonapp.billing.AdDelegate;
import com.deange.marathonapp.billing.BillingConstants;
import com.deange.marathonapp.billing.BillingConstants2;
import com.deange.marathonapp.billing.IabHelper;
import com.deange.marathonapp.billing.IabResult;
import com.deange.marathonapp.model.Inventory;
import com.deange.marathonapp.model.Purchase;
import com.deange.marathonapp.controller.AchievementsController;
import com.deange.marathonapp.controller.BillingController;
import com.deange.marathonapp.controller.GoogleClients;
import com.deange.marathonapp.utils.PlatformUtils;
import com.deange.marathonapp.R;
import com.deange.marathonapp.utils.Utils;
import com.deange.marathonapp.controller.StateController;
import com.deange.marathonapp.google.BaseGameActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.games.GamesClient;

public class MainActivity
        extends BaseGameActivity
        implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, AdDelegate.Listener<InterstitialAd> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String KEY_SHOW_AD = TAG + ".show_ad";
    private static final String KEY_LOADED_INVENTORY = TAG + ".loaded_inv";

    private PopupMenu mPopupMenu;
    private MenuItem mRemoveAdsItem;
    private boolean mShowAd = true;
    private boolean mLoadedInventory = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();

        // Load saved variables
        if (savedInstanceState != null) {
            mShowAd = savedInstanceState.getBoolean(KEY_SHOW_AD);
            mLoadedInventory = savedInstanceState.getBoolean(KEY_LOADED_INVENTORY);
        }

        // Force an initialization
        BillingController.createInstance(this);

        MarathonFragment fragment = (MarathonFragment) getSupportFragmentManager().findFragmentByTag(MarathonFragment.TAG);
        if (fragment == null) {
            fragment = new MarathonFragment();
        }

        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment, MarathonFragment.TAG)
                    .commit();
        }

        // These must be done in this order.
        // Unfortunately need to wait a bit to ensure that the IabHelper
        // has completed setup :(
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkInventory();
                showAdIfNecessary();
            }
        }, 1000);
    }

    @Override
    public boolean onMenuItemClick(final MenuItem item) {

        boolean handled = false;

        switch (item.getItemId()) {

            case R.id.menu_test:
                handleTest();
                handled = true;
                break;

            case R.id.menu_achievements:
                handleAchievements();
                handled = true;
                break;

            case R.id.menu_leaderboards:
                handleLeaderBoards();
                handled = true;
                break;

            case R.id.menu_logout:
                handled = true;
                handleSignout();
                break;
        }

        return handled;
    }

    private void handleTest() {

        BillingController.getInstance(this).purchase(BillingConstants2.SKU_TEST, new IabHelper.OnIabPurchaseFinishedListener() {

            @Override
            public void onIabPurchaseFinished(final IabResult result, final Purchase purchase) {

                Log.d(TAG, "purchase = " + purchase);

                if (result.isSuccess() || result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                    checkInventory();
                }

            }
        });

    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putBoolean(KEY_SHOW_AD, mShowAd);
        outState.putBoolean(KEY_LOADED_INVENTORY, mLoadedInventory);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");

        BillingController.getInstance(this).removeInstance();
        super.onDestroy();
    }

    private void checkInventory() {
        BillingController.getInstance(this).queryInventory(new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(final IabResult result, final Inventory inv) {

                if ((result.isSuccess()) && (inv != null)) {
                    mLoadedInventory = true;
                    Purchase purchase = inv.getPurchase(BillingConstants2.SKU_TEST);
                    Log.d(TAG, "purchase = " + purchase);

                    // If mShowAd is false, we want it to stay false
                    mShowAd = mShowAd && (purchase == null);
                    showAdIfNecessary();

                    if (mRemoveAdsItem != null) {
                        mRemoveAdsItem.setVisible(purchase == null);
                    }
                }

            }
        });
    }

    private void showAdIfNecessary() {
        if (mShowAd && mLoadedInventory) {
            final AdRequest.Builder requestBuilder = new AdRequest.Builder();
            if (BuildConfig.DEBUG) {
                requestBuilder.addTestDevice("FCCD174D5B83FA1062468A3C8E63AF38");
            }

            final AdRequest adRequest = requestBuilder.build();
            final InterstitialAd ad = new InterstitialAd(this);
            ad.setAdUnitId(BillingConstants.AD_UNIT_ID);
            ad.setAdListener(new AdDelegate<InterstitialAd>(this, ad));
            ad.loadAd(adRequest);
        }
    }

    private void handleAchievements() {
        final GamesClient client = GoogleClients.getInstance().getGamesClient();
        startActivityForResult(client.getAchievementsIntent(), 0);
    }

    private void handleLeaderBoards() {
        final GamesClient client = GoogleClients.getInstance().getGamesClient();
        AchievementsController.getInstance().notifyLeaderBoardImmediate(
                StateController.getInstance().getMilesRan());
        startActivityForResult(client.getLeaderboardIntent(
                getString(R.string.leaderboard_total_distance_ran)), 0);
    }

    private void handleSignout() {
        signOut();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setupActionBar() {

        final View overflowView = findViewById(R.id.activity_overflow_button);
        overflowView.setOnClickListener(this);

        mPopupMenu = new PopupMenu(this, overflowView);
        mPopupMenu.setOnMenuItemClickListener(this);
        mPopupMenu.inflate(R.menu.main_menu);
        mRemoveAdsItem = mPopupMenu.getMenu().findItem(R.id.menu_test);

        if (PlatformUtils.hasKitKat()) {
            overflowView.setOnTouchListener(mPopupMenu.getDragToOpenListener());
        }
    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.activity_overflow_button) {
            mPopupMenu.show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(Utils.calculateWindowFlags());
        }
    }

    @Override
    public void onSignInSucceeded() {
    }

    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignOutSucceeded() {

        // Remove all prefs
        StateController.getInstance().clear();

        // Head back to the LoginActivity!
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAdClosed(final InterstitialAd ad) {
        Log.v(TAG, "onAdClosed()");
        // Nothing to do here
    }

    @Override
    public void onAdFailedToLoad(final InterstitialAd ad, final int errorCode) {
        Log.v(TAG, "onAdFailedToLoad()");
        // Nothing to do here
    }

    @Override
    public void onAdLeftApplication(final InterstitialAd ad) {
        Log.v(TAG, "onAdLeftApplication()");
        // Nothing to do here
    }

    @Override
    public void onAdOpened(final InterstitialAd ad) {
        Log.v(TAG, "onAdOpened()");
        // Nothing to do here
    }

    @Override
    public void onAdLoaded(final InterstitialAd ad) {
        Log.v(TAG, "onAdLoaded()");
        mShowAd = false;
        if (ad != null) {
            ad.show();
        }
    }
}
