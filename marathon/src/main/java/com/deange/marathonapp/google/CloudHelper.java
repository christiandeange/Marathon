package com.deange.marathonapp.google;

import android.util.Log;

import com.deange.marathonapp.controller.StateController;
import com.deange.marathonapp.model.CloudInfo;
import com.google.android.gms.appstate.AppStateClient;
import com.google.android.gms.appstate.OnStateLoadedListener;

import java.nio.charset.Charset;

public final class CloudHelper {

    private static final String TAG = CloudHelper.class.getSimpleName();
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static final int KEY_GAME_STATE = 0;

    private static long mLastSave = 0;

    public static byte[] convert(final CloudInfo cloudInfo) {

        byte[] bytes = null;
        if (cloudInfo != null) {
            final String serializedInfo = cloudInfo.serialize();
            bytes = serializedInfo.getBytes(DEFAULT_CHARSET);
        }
        return bytes;
    }

    public static CloudInfo convert(final byte[] bytes) {

        CloudInfo cloudInfo = null;
        if (bytes != null) {
            final String serializedInfo = new String(bytes, DEFAULT_CHARSET);
            cloudInfo = CloudInfo.deserialize(serializedInfo, CloudInfo.class);
        }
        return cloudInfo;
    }

    public static void updateState(final int stateKey, final CloudInfo cloudInfo) {

        // Only update the state if this one is technically "after" the last time we saved
        if (updateTime(cloudInfo)) {
            final AppStateClient client = GoogleClients.getInstance().getAppStateClient();
            if (client.isConnected()) {
                final byte[] infoInBytes = convert(cloudInfo);
                client.updateState(stateKey, infoInBytes);
            }
        }

    }

    public static void getState(final OnStateLoadedListener listener, final int stateKey) {
        final AppStateClient client = GoogleClients.getInstance().getAppStateClient();
        if (client.isConnected()) {
            client.loadState(listener, stateKey);
        }
    }

    public static CloudInfo resolveConflict(final CloudInfo server, final CloudInfo local) {
        Log.v(TAG, "resolveConflict()");
        Log.v(TAG, "local = " + local);
        Log.v(TAG, "server = " + server);

        // Let's try to do a "smart" resolution
        // Say we have both server and local data for this user.
        // We want to grab the one with more...progress. That can be identified by
        // the miles ran parameter (since essentially that is the objective of the game)
        // We grab the data which has the most progress in that sense

        // Cases:   server notnull  local notnull   > more recently-saved
        //          server notnull  local null      > server
        //          server null     local notnull   > local
        //          server null     local null      > null (no data stored)

        final CloudInfo resolvedInfo;

        if (server != null) {

            if (server.compareTo(local) > 0) {
                // Server is newer
                resolvedInfo = server;

            } else {
                //Use local, doesn't matter if null
                resolvedInfo = local;
            }

        } else {
            // Server info is null, use local. Doesn't matter if null
            resolvedInfo = local;
        }

        return resolvedInfo;
    }

    public static void onStateLoaded(final int statusCode, final int stateKey, final byte[] localData) {
        Log.v(TAG, "onStateLoaded()");

        final CloudInfo info = CloudHelper.convert(localData);
        if (info != null) {
            updateTime(info);
            StateController.getInstance().setMilesRan(info.getMilesRan());
        }
    }

    public static void onStateConflict(final int stateKey, final String resolvedVersion, final byte[] localData, final byte[] serverData,
                                final AppStateClient client, final OnStateLoadedListener listener) {
        Log.v(TAG, "onStateConflict()");

        final CloudInfo serverInfo = convert(serverData);
        final CloudInfo localInfo = convert(localData);

        // Manually resolve the conflict
        final CloudInfo resolvedInfo = resolveConflict(serverInfo, localInfo);

        updateTime(resolvedInfo);

        // Signal to the AppStateClient that we have resolved the right version of the info
        if (client.isConnected()) {
            client.resolveState(listener, stateKey, resolvedVersion, CloudHelper.convert(resolvedInfo));
        }
    }

    // Returns true if the new CloudInfo object has a later timestamp than the saved one
    private static boolean updateTime(final CloudInfo info) {
        if (info != null && info.getTimestamp() >= mLastSave) {
            mLastSave = info.getTimestamp();
            return true;

        } else {
            return false;
        }
    }

    public static class SimpleStateListenerDelegate
        implements OnStateLoadedListener {

        @Override
        public void onStateLoaded(final int statusCode, final int stateKey, final byte[] localData) {
            CloudHelper.onStateLoaded(statusCode, stateKey, localData);
        }

        @Override
        public void onStateConflict(final int stateKey, final String resolvedVersion, final byte[] localData, final byte[] serverData) {
            CloudHelper.onStateConflict(stateKey, resolvedVersion, localData, serverData, GoogleClients.getInstance().getAppStateClient(), this);
        }
    }

    private CloudHelper() {
        // Not instantiable
    }

}