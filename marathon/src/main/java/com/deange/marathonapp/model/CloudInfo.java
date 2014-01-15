package com.deange.marathonapp.model;

import com.google.gson.annotations.SerializedName;

public class CloudInfo
        extends BaseModel
        implements Comparable<CloudInfo> {

    public static final String MILES_RAN = "miles_ran";
    public static final String TIMESTAMP = "time";

    @SerializedName(MILES_RAN)
    private int mMilesRan;

    @SerializedName(TIMESTAMP)
    private long mTimestamp;

    public int getMilesRan() {
        return mMilesRan;
    }

    public void setMilesRan(final int milesRan) {
        mMilesRan = milesRan;
        update();
    }

    // Should be called on every setter, so that we can keep track of any mutable field changes.
    // Useful for resolving conflicts from the Google App State API
    private void update() {
        mTimestamp = System.currentTimeMillis();
    }

    @Override
    public int compareTo(final CloudInfo another) {
        return another == null ? 1 : compareLong(mTimestamp, another.mTimestamp);
    }

    private int compareLong(final long lhs, final long rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }
}
