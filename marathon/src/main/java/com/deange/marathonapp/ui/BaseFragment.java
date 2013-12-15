package com.deange.marathonapp.ui;

import android.support.v4.app.Fragment;

import com.deange.marathonapp.google.BaseGameActivity;

public class BaseFragment extends Fragment {

    protected BaseGameActivity getBaseActivity() {
        if (!(getActivity() instanceof BaseGameActivity)) {
            throw new IllegalStateException("Attached activity is not BaseGameActivity");
        }

        return (BaseGameActivity) getActivity();
    }

}
