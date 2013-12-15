package com.deange.marathonapp.google;

import com.google.gson.annotations.SerializedName;

public class CloudInfo implements Comparable<CloudInfo> {

    public static final String MILES_RAN = "miles_ran";

    @SerializedName(MILES_RAN)
    private int mMilesRan;

    public int getMilesRan() {
        return mMilesRan;
    }

    public void setMilesRan(final int milesRan) {
        mMilesRan = milesRan;
    }

    @Override
    public int compareTo(final CloudInfo another) {
        return another == null ? 1 : Double.compare(getMilesRan(), another.getMilesRan());
    }
}
