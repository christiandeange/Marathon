package com.deange.marathontest.controller;

import android.content.Context;
import android.util.Pair;
import android.view.Gravity;

import com.google.android.gms.appstate.AppStateClient;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.plus.PlusClient;

public class GoogleClients {

    private static GoogleClients sInstance;
    private static final Object sLock = new Object();

    private Context mContext;

    // Client objects we manage. If a given client is not enabled, it is null.
    GamesClient mGamesClient = null;
    PlusClient mPlusClient = null;
    AppStateClient mAppStateClient = null;

    // What clients we manage (OR-able values, can be combined as flags)
    public final static int CLIENT_NONE = 0x00;
    public final static int CLIENT_GAMES = 0x01;
    public final static int CLIENT_PLUS = 0x02;
    public final static int CLIENT_APPSTATE = 0x04;
    public final static int CLIENT_ALL = CLIENT_GAMES | CLIENT_PLUS | CLIENT_APPSTATE;

    private GoogleClients(final Context context) {
        mContext = context;
    }

    public static GoogleClients getInstance() {
        synchronized (sLock) {
            if (sInstance == null) {
                throw new IllegalStateException("GoogleClients has not been created");
            }
            return sInstance;
        }
    }

    public static synchronized void createInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new GoogleClients(context.getApplicationContext());
        }
    }

    public void initializeClients(final int clientsToUse, final String[] scopes,
                                  final GooglePlayServicesClient.ConnectionCallbacks connectionCallbacks,
                                  final GooglePlayServicesClient.OnConnectionFailedListener failedListener) {

        if (0 != (clientsToUse & CLIENT_GAMES)) {
            mGamesClient = new GamesClient.Builder(mContext, connectionCallbacks, failedListener)
                    .setGravityForPopups(Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                    .setScopes(scopes)
                    .create();
        }

        if (0 != (clientsToUse & CLIENT_PLUS)) {
            mPlusClient = new PlusClient.Builder(mContext, connectionCallbacks, failedListener)
                    .setScopes(scopes)
                    .build();
        }

        if (0 != (clientsToUse & CLIENT_APPSTATE)) {
            mAppStateClient = new AppStateClient.Builder(mContext, connectionCallbacks, failedListener)
                    .setScopes(scopes)
                    .create();
        }

    }

    /**
     * Returns the GamesClient object. In order to call this method, you must have
     * called @link{setup} with a set of clients that includes CLIENT_GAMES.
     */
    public GamesClient getGamesClient() {
        if (mGamesClient == null) {
            throw new IllegalStateException("No GamesClient. Did you request it at setup?");
        }
        return mGamesClient;
    }

    /**
     * Returns the AppStateClient object. In order to call this method, you must have
     * called @link{#setup} with a set of clients that includes CLIENT_APPSTATE.
     */
    public AppStateClient getAppStateClient() {
        if (mAppStateClient == null) {
            throw new IllegalStateException("No AppStateClient. Did you request it at setup?");
        }
        return mAppStateClient;
    }

    /**
     * Returns the PlusClient object. In order to call this method, you must have
     * called @link{#setup} with a set of clients that includes CLIENT_PLUS.
     */
    public PlusClient getPlusClient() {
        if (mPlusClient == null) {
            throw new IllegalStateException("No PlusClient. Did you request it at setup?");
        }
        return mPlusClient;
    }


    public void signOut() {
        // for the PlusClient, "signing out" means clearing the default account and
        // then disconnecting
        if (mPlusClient != null && mPlusClient.isConnected()) {
            mPlusClient.clearDefaultAccount();
        }

        // For the games client, signing out means calling signOut and disconnecting
        if (mGamesClient != null && mGamesClient.isConnected()) {
            mGamesClient.signOut();
        }

    }

    public void killConnections() {
        if (mGamesClient != null && mGamesClient.isConnected()) {
            mGamesClient.disconnect();
        }
        if (mPlusClient != null && mPlusClient.isConnected()) {
            mPlusClient.disconnect();
        }
        if (mAppStateClient != null && mAppStateClient.isConnected()) {
            mAppStateClient.disconnect();
        }
    }

    public int calculateConnectionFlags(int connectionFlags) {

        // failsafe, in case we somehow lost track of what clients are connected or not.
        if (mGamesClient != null && mGamesClient.isConnected() && (0 == (connectionFlags & CLIENT_GAMES))) {
            connectionFlags |= CLIENT_GAMES;
        }

        if (mPlusClient != null && mPlusClient.isConnected() && (0 == (connectionFlags & CLIENT_PLUS))) {
            connectionFlags |= CLIENT_PLUS;
        }

        if (mAppStateClient != null && mAppStateClient.isConnected() && (0 == (connectionFlags & CLIENT_APPSTATE))) {
            connectionFlags |= CLIENT_APPSTATE;
        }

        return connectionFlags;
    }

    public int calculateConnectionClient(final int pendingClients) {

        int clientCurrentlyConnecting = -1;

        // which client should be the next one to connect?
        if (mGamesClient != null && (0 != (pendingClients & CLIENT_GAMES))) {
            clientCurrentlyConnecting = CLIENT_GAMES;

        } else if (mPlusClient != null && (0 != (pendingClients & CLIENT_PLUS))) {
            clientCurrentlyConnecting = CLIENT_PLUS;

        } else if (mAppStateClient != null && (0 != (pendingClients & CLIENT_APPSTATE))) {
            clientCurrentlyConnecting = CLIENT_APPSTATE;
        }

        return clientCurrentlyConnecting;
    }

    public void connectClient(final int client) {
        switch (client) {
            case CLIENT_GAMES:
                mGamesClient.connect();
                break;
            case CLIENT_APPSTATE:
                mAppStateClient.connect();
                break;
            case CLIENT_PLUS:
                mPlusClient.connect();
                break;
        }
    }

    public Pair<Integer, Boolean> reconnectClients(final int whatClients, int connectedClients) {

        boolean actuallyReconnecting = false;

        if ((whatClients & CLIENT_GAMES) != 0 && mGamesClient != null
                && mGamesClient.isConnected()) {
            actuallyReconnecting = true;
            connectedClients &= ~CLIENT_GAMES;
            mGamesClient.reconnect();
        }
        if ((whatClients & CLIENT_APPSTATE) != 0 && mAppStateClient != null
                && mAppStateClient.isConnected()) {
            actuallyReconnecting = true;
            connectedClients &= ~CLIENT_APPSTATE;
            mAppStateClient.reconnect();
        }

        if ((whatClients & CLIENT_PLUS) != 0 && mPlusClient != null
                && mPlusClient.isConnected()) {
            // PlusClient doesn't need reconnections.
        }

        return Pair.create(connectedClients, actuallyReconnecting);
    }



}
