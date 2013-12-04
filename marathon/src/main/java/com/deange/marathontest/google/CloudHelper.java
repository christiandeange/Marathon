package com.deange.marathontest.google;

import com.deange.marathontest.Utils;
import com.deange.marathontest.controller.GsonController;
import com.google.android.gms.appstate.AppStateClient;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class CloudHelper {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static byte[] convert(final CloudInfo cloudInfo) {
        final String serializedInfo = GsonController.getInstance().toJson(cloudInfo);
        final byte[] bytes = serializedInfo.getBytes(DEFAULT_CHARSET);
        return bytes;
    }

    public static CloudInfo convert(final byte[] bytes) {
        final String serializedInfo = new String(bytes, DEFAULT_CHARSET);
        final CloudInfo cloudInfo = GsonController.getInstance().fromJson(serializedInfo, CloudInfo.class);
        return cloudInfo;
    }

    public static void updateState(final AppStateClient client, final int stateKey, final CloudInfo cloudInfo) {
        Utils.runAsynchronouslyIfNecessary(new StateUpdater(client, stateKey, cloudInfo));
    }

    public static class StateUpdater implements Runnable {

        final AppStateClient mClient;
        final int mStateKey;
        final CloudInfo mCloudInfo;

        public StateUpdater(final AppStateClient client, final int stateKey, final CloudInfo cloudInfo) {
            mClient = client;
            mStateKey = stateKey;
            mCloudInfo = cloudInfo;
        }

        @Override
        public void run() {

        }
    }

}