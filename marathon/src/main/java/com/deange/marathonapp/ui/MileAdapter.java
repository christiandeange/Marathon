package com.deange.marathonapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.deange.marathonapp.R;
import com.deange.marathonapp.ui.view.MarathonView;

public class MileAdapter extends BaseAdapter {

    public static final String TAG = MileAdapter.class.getSimpleName();

    private static final int MARKER_COUNT = 10;

    private static final boolean SHOW_MILE_MARKERS = false;

    private final Context mContext;
    private final LayoutInflater mInflater;

    public MileAdapter(final Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return MarathonView.TOTAL_MILES;
    }

    @Override
    public Integer getItem(final int position) {
        return position;
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position);
    }

    @Override
    public boolean isEnabled(final int position) {
        return false;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        final View view;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.list_item_mile, null);

            final LinearLayout markersContainer = (LinearLayout) view.findViewById(R.id.list_item_mile_markers_container);

            for (int i = 0; i < MARKER_COUNT; i++) {

                final View markerView = mInflater.inflate(R.layout.list_item_mile_marker, null);
                final TextView markerTextView = (TextView) markerView.findViewById(R.id.list_item_marker_text);

                final String markerText = (i == 0) ? "-" : String.valueOf(i);
                markerTextView.setText(markerText);
                markersContainer.addView(markerView);

                // Each one of the markers takes up an equal amount of space
                ((LinearLayout.LayoutParams) markerView.getLayoutParams()).weight = 1;
            }

        } else {
            view = convertView;
        }

        // TODO Maybe change this to a user-defined setting?
        if (SHOW_MILE_MARKERS) {
            view.findViewById(R.id.list_item_mile_markers_container).setVisibility(View.VISIBLE);
            view.findViewById(R.id.list_item_mile_checkers).setVisibility(View.GONE);

        } else {
            view.findViewById(R.id.list_item_mile_markers_container).setVisibility(View.GONE);
            view.findViewById(R.id.list_item_mile_checkers).setVisibility(View.VISIBLE);
        }

        final int mile = getItem(position);
        final String markerText = mContext.getResources().getQuantityString(R.plurals.mile_marker, mile, mile);
        ((TextView) view.findViewById(R.id.list_item_mile_textview)).setText(markerText);

        return view;
    }
}
