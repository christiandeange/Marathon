package com.deange.marathonapp.google;

import android.util.Log;

import com.deange.marathonapp.controller.GoogleClients;
import com.deange.marathonapp.controller.GsonController;
import com.deange.marathonapp.model.CloudInfo;
import com.google.android.gms.appstate.AppStateClient;
import com.google.android.gms.appstate.OnStateLoadedListener;

import java.nio.charset.Charset;

public final class CloudHelper {

    private static final String TAG = CloudHelper.class.getSimpleName();
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static final int KEY_GAME_STATE = 0;

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
        final AppStateClient client = GoogleClients.getInstance().getAppStateClient();
        if (client.isConnected()) {
            final byte[] infoInBytes = convert(cloudInfo);
            client.updateState(stateKey, infoInBytes);
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

        // Cases:   server notnull  local notnull   > one with more miles ran
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

    private CloudHelper() {
        // Not instantiable
    }

}