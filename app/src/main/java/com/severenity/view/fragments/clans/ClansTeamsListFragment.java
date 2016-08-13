package com.severenity.view.fragments.clans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.App;
import com.severenity.R;
import com.severenity.entity.User;
import com.severenity.view.fragments.NotifiableFragment;

/**
 * Created by Andriy on 7/28/2016.
 */
public class ClansTeamsListFragment extends Fragment {

    public ClansTeamsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans_teams_list, container, false);

        return view;
    }
}