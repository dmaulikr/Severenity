package com.nosad.sample.view.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nosad.sample.R;
import com.nosad.sample.utils.common.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private int currentTotalStepsMade = 0;

    private TextView tvTotalStepsMade;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvTotalMetersPassed = (TextView) view.findViewById(R.id.tvTotalMetersPassed);
        tvTotalMetersPassed.setText(String.format(getResources().getString(R.string.totalStepsMade), currentTotalStepsMade));

        return view;
    }

    private BroadcastReceiver stepsCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentTotalStepsMade = intent.getIntExtra(Constants.EXTRA_STEPS, 0);
            tvTotalMetersPassed.setText(String.format(getResources().getString(R.string.totalStepsMade), currentTotalStepsMade));
        }
    };

    public BroadcastReceiver getStepsCountReceiver() {
        return stepsCountReceiver;
    }
}
