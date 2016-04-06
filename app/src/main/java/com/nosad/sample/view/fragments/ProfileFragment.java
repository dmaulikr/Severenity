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

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private TextView tvTotalMetersPassed;
    private User currentUser;

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

        tvTotalMetersPassed = (TextView) view.findViewById(R.id.tvTotalMetersPassed);
        tvTotalMetersPassed.setText(
                String.format(getResources().getString(R.string.totalStepsMade),
                        currentUser == null ? 0 : currentUser.getSteps()));

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
            tvTotalMetersPassed.setText(
                String.format(getResources().getString(R.string.totalStepsMade),
                currentUser.getSteps())
            );
        }
    };

    public void updateUserInfo() {
        tvTotalMetersPassed.setText(String.format(
            getResources().getString(R.string.totalStepsMade),
            currentUser == null ? 0 : currentUser.getSteps())
        );
    }

    public BroadcastReceiver getStepsCountReceiver() {
        return stepsCountReceiver;
    }
}
