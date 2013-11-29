package com.deange.marathon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MileAdapter extends BaseAdapter {

    public static final String TAG = MileAdapter.class.getSimpleName();
    private final Context mContext;

    public MileAdapter(final Context context) {
        mContext = context;
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
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        final View view;

        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_mile, null);
        } else {
            view = convertView;
        }

        ((TextView) view.findViewById(R.id.list_item_mile_textview)).setText(getItem(position) + " miles");

        return view;
    }
}
