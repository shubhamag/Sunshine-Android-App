package com.example.shubhama.sunshine.app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DayWeatherDetailActivityFragment extends Fragment {

    public DayWeatherDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent parentIntent = getActivity().getIntent();
        View rootFragmentView = inflater.inflate(R.layout.fragment_day_weather_detail, container, false);
        if(!(parentIntent==null) && parentIntent.hasExtra(Intent.EXTRA_TEXT)) {
            String stringExtra = parentIntent.getStringExtra(Intent.EXTRA_TEXT);
            TextView tv = (TextView) rootFragmentView.findViewById(R.id.day_detail_text);
            tv.setText(stringExtra);
        }
        return rootFragmentView;
    }
}
