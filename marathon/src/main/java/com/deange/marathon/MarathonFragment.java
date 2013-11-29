package com.deange.marathon;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

public class MarathonFragment
        extends Fragment {

    public static final String TAG = MarathonFragment.class.getSimpleName();

    private MarathonView mMarathonView;

    public MarathonFragment() {
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMarathonView = (MarathonView) rootView.findViewById(R.id.marathon_view);
        mMarathonView.setAdapter(new MileAdapter(getActivity()));

        return rootView;
    }

}
