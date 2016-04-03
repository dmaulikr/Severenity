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

import com.google.android.gms.location.LocationServices;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.utils.common.Constants;
import com.nosad.sample.view.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private TextView tvTotalMetersPassed;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvTotalMetersPassed = (TextView) view.findViewById(R.id.tvTotalMetersPassed);
        tvTotalMetersPassed.setText(String.format(getResources().getString(R.string.totalStepsMade),
            App.getUserManager().getCurrentUser() == null ? 0 : App.getUserManager().getCurrentUser().getSteps()));

        return view;
    }

    private BroadcastReceiver stepsCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int currentTotalStepsMade = intent.getIntExtra(Constants.EXTRA_STEPS, 0);

            App.getUserManager().getCurrentUser().setSteps(currentTotalStepsMade);
            tvTotalMetersPassed.setText(String.format(getResources().getString(R.string.totalStepsMade), currentTotalStepsMade));
        }
    };

    public BroadcastReceiver getStepsCountReceiver() {
        return stepsCountReceiver;
    }
}
