package com.severenity.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.R;

/**
 * Created by Andriy on 7/28/2016.
 */
public class ClansTeamFragment extends Fragment {

    public ClansTeamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans_team, container, false);

        return view;
    }
}
