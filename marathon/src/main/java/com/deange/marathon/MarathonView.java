package com.deange.marathon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewConfiguration;
import android.widget.ListView;
import android.widget.Toast;

public class MarathonView extends ListView {

    int mFirst = 0;

    public OnMileRanListener mOnMileRanListener;

    public MarathonView(final Context context) {
        super(context);
        init();
    }

    public MarathonView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarathonView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setFriction(ViewConfiguration.getScrollFriction() * 2);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        final int first = getFirstVisiblePosition();

        if (first != mFirst) {
            dispatchOnMileRan(first);
            Toast.makeText(getContext(), "Ran " + first + " mile" + ((first == 1) ? "" : "s"), Toast.LENGTH_SHORT).show();
        }

        mFirst = first;
    }

    protected void dispatchOnMileRan(final int mile) {
        if (mOnMileRanListener != null) {
            mOnMileRanListener.onMileRan(mile);
        }
    }

    public void setOnMileRanListener(final OnMileRanListener onMileRanListener) {
        mOnMileRanListener = onMileRanListener;
    }

    public OnMileRanListener getOnMileRanListener() {
        return mOnMileRanListener;
    }

    public interface OnMileRanListener {
        public void onMileRan(final int mile);
    }
}
