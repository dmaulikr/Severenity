package com.nosad.sample.view.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.exceptions.NotAuthenticatedException;
import com.nosad.sample.entity.User;
import com.nosad.sample.utils.common.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private TextView tvTotalMetersPassed;
    private User currentUser;
    private int experienceMultiplier = 10;
    private int levelMultiplier = 1000;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        try {
            currentUser = App.getUserManager().getCurrentUser();
        } catch (NotAuthenticatedException e) {
            e.printStackTrace();
            App.getInstance().logOut();
        }

        int meters = 0;

        if (currentUser != null) {
            if (currentUser.getSteps() / 2 <= App.getLocationManager().getTotalMetersPassed()) {
                meters = currentUser.getSteps() / 2;
            } else {
                meters = App.getLocationManager().getTotalMetersPassed();
            }
        }

        tvTotalMetersPassed = (TextView) view.findViewById(R.id.tvTotalMetersPassed);
        tvTotalMetersPassed.setText(String.format(getResources().getString(R.string.totalDistancePassed), meters));

        ImageView fbLogout = (ImageView) view.findViewById(R.id.ivFBLogout);
        fbLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getInstance().logOut();
            }
        });

        return view;
    }

    private BroadcastReceiver stepsCountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            currentUser.setSteps(currentUser.getSteps() + 1);
        }
    };

    public void updateUserInfo() {
        int meters = 0;

        if (currentUser != null) {
            if (currentUser.getSteps() / 2 <= App.getLocationManager().getTotalMetersPassed()) {
                meters = currentUser.getSteps() / 2;
            } else {
                meters = App.getLocationManager().getTotalMetersPassed();
            }

            currentUser.setExperience(currentUser.getExperience() + meters / experienceMultiplier);
            currentUser.setLevel(currentUser.getExperience() / levelMultiplier);
        }

        tvTotalMetersPassed.setText(String.format(getResources().getString(R.string.totalDistancePassed), meters));
        App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_UPDATE_UI));
        App.getUserManager().updateUserInfo();
    }

    public BroadcastReceiver getStepsCountReceiver() {
        return stepsCountReceiver;
    }
}
