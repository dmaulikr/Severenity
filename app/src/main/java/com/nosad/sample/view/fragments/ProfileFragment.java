package com.nosad.sample.view.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.entity.User;
import com.nosad.sample.utils.common.Constants;

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

        User currentUser = App.getUserManager().getCurrentUser();

        int meters = 0;

        if (currentUser != null) {
            meters = currentUser.getDistance();
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

    @Override
    public void onResume() {
        super.onResume();

        updateUIInfo();
        App.getLocalBroadcastManager().registerReceiver(
                updateUIReceiver,
                new IntentFilter(Constants.INTENT_FILTER_UPDATE_UI)
        );
    }

    @Override
    public void onPause() {
        super.onPause();

        App.getLocalBroadcastManager().unregisterReceiver(updateUIReceiver);
    }

    private BroadcastReceiver updateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUIInfo();
        }
    };

    private void updateUIInfo() {
        if (App.getUserManager().getCurrentUser() == null) {
            return;
        }

        tvTotalMetersPassed.setText(String.format(
            getResources().getString(R.string.totalDistancePassed),
            App.getUserManager().getCurrentUser().getDistance())
        );
    }
}
