package com.deange.marathonapp.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

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

    private PopupMenu mPopupMenu;
    private boolean mShowAd;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();

        // Load saved variables
        if (savedInstanceState != null) {
            mShowAd = savedInstanceState.getBoolean(KEY_SHOW_AD);
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

        final InterstitialAd ad = new InterstitialAd(this);
        final AdRequest adRequest = new AdRequest.Builder().addTestDevice("FCCD174D5B83FA1062468A3C8E63AF38").build();
        ad.setAdUnitId(BillingConstants.AD_UNIT_ID);
        ad.setAdListener(new AdDelegate<InterstitialAd>(ad, this));
        ad.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
            public void onIabPurchaseFinished(final IabResult result, final Purchase info) {

                if (result.isSuccess() || result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
                    BillingController.getInstance(MainActivity.this).queryInventory(new IabHelper.QueryInventoryFinishedListener() {
                        @Override
                        public void onQueryInventoryFinished(final IabResult result, final Inventory inv) {

                            if (result.isSuccess()) {
                                Purchase purchase = inv.getPurchase(BillingConstants2.SKU_TEST);
                                Log.d("MainActivity", "purchase = " + purchase);
                            }

                        }
                    });
                }

            }
        });

    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putBoolean(KEY_SHOW_AD, mShowAd);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");

        BillingController.getInstance(this).removeInstance();
        super.onDestroy();
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
