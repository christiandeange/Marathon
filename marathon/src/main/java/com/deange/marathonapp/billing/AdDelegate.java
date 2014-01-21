package com.deange.marathonapp.billing;

import com.google.android.gms.ads.AdListener;

public class AdDelegate<T> extends AdListener {

    private Listener<T> mListener;
    private T mToken;

    public AdDelegate(final Listener<T> listener, final T token) {
        mListener = listener;
        mToken = token;
    }

    public void onAdClosed() {
        mListener.onAdClosed(mToken);
    }

    public void onAdFailedToLoad(final int errorCode) {
        mListener.onAdFailedToLoad(mToken, errorCode);
    }

    public void onAdLeftApplication() {
        mListener.onAdLeftApplication(mToken);
    }

    public void onAdOpened() {
        mListener.onAdOpened(mToken);
    }

    public void onAdLoaded() {
        mListener.onAdLoaded(mToken);
    }

    public interface Listener<T> {
        public void onAdClosed(final T token);

        public void onAdFailedToLoad(final T token, final int errorCode);

        public void onAdLeftApplication(final T token);

        public void onAdOpened(final T token);

        public void onAdLoaded(final T token);
    }
}
