package com.deange.marathontest.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewConfiguration;
import android.widget.ListView;

public class MarathonView extends ListView {

    private static final int VELOCITY_FRICTION_FACTOR = 10;
    public static final int TOTAL_MILES = 2000000000; // 2 billion

    private int mFirst = 0;
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
        setFriction(ViewConfiguration.getScrollFriction() * VELOCITY_FRICTION_FACTOR);
        setDivider(null);
        setDividerHeight(0);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        final int first = getFirstVisiblePosition();

        if (first != mFirst) {
            dispatchOnMileRan(first);
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
