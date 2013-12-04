package com.deange.marathontest.google;

import com.google.gson.annotations.SerializedName;

public class CloudInfo {

    public static final String MILES_RAN = "miles_ran";

    @SerializedName(MILES_RAN)
    private int mMilesRan;

    public int getMilesRan() {
        return mMilesRan;
    }

    public void setMilesRan(final int milesRan) {
        mMilesRan = milesRan;
    }
}
