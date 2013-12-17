package com.deange.marathonapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deange.marathonapp.R;
import com.deange.marathonapp.controller.StateController;
import com.deange.marathonapp.google.CloudHelper;
import com.deange.marathonapp.model.CloudInfo;

public class MarathonFragment
        extends BaseFragment implements MarathonView.OnMileRanListener {

    public static final String TAG = MarathonFragment.class.getSimpleName();
    public static final String KEY_MILES_RAN = "milesRan";
    public static final String KEY_MILES_OFFSET = "milesOffset";
    public static final int MILES_UPDATE_STATE_INTERVAL = 50;

    private MarathonView mMarathonView;

    // Cache an instance to change details on
    private CloudInfo mCloudInfo = new CloudInfo();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        Log.v(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView()");

        final int milesRan;
        final int offset;

        if (savedInstanceState != null) {
            // Recovering from rotation, restore the index/offset
            milesRan = savedInstanceState.getInt(KEY_MILES_RAN);
            offset = savedInstanceState.getInt(KEY_MILES_OFFSET);

        } else {
            // First fragment load, retrieve saved value
            milesRan = StateController.getInstance().getMilesRan();
            offset = 0;
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMarathonView = (MarathonView) rootView.findViewById(R.id.marathon_view);
        mMarathonView.setAdapter(new MileAdapter(getActivity()));
        mMarathonView.setOnMileRanListener(this);
        mMarathonView.setSelectionFromTop(milesRan, offset);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        Log.v(TAG, "onSaveInstanceState()");

        final int index = mMarathonView.getFirstVisiblePosition();
        final View view = mMarathonView.getChildAt(0);
        final int offset = (view == null) ? 0 : view.getTop();

        outState.putInt(KEY_MILES_RAN, index);
        outState.putInt(KEY_MILES_OFFSET, offset);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy()");

        // Our last breath...save this state
        CloudHelper.updateState(CloudHelper.KEY_GAME_STATE, mCloudInfo);
        super.onDestroy();
    }

    @Override
    public void onMileRan(final int mile) {
        // Locally cache the amount of miles ran
        StateController.getInstance().setMilesRan(mile);

        if (mile % MILES_UPDATE_STATE_INTERVAL == 0) {
            // Currently we will update the info on every 10th mile ran,
            // let the play services library handle optimizing network requests
            // (Hopefully it does)
            mCloudInfo.setMilesRan(mile);
            CloudHelper.updateState(CloudHelper.KEY_GAME_STATE, mCloudInfo);
        }

    }
}
