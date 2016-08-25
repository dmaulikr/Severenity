package com.severenity.view.fragments.clans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.R;
import com.severenity.view.Dialogs.CreateTeamDialog;

/**
 * Created by Andriy on 7/28/2016.
 */
public class TeamsListFragment extends Fragment implements View.OnClickListener, CreateTeamDialog.OnTeamCreatedListener {

    private View mAddTeamButtonsView = null;

    public TeamsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans_teams_list, container, false);

        mAddTeamButtonsView = view.findViewById(R.id.createTeam);
        mAddTeamButtonsView.setOnClickListener(this);
        //if (App.getUserManager().getCurrentUser().getLevel() < 5 || user is within some team )
        //mAddTeamButtonsView.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createTeam: {
                CreateTeamDialog teamDialog = CreateTeamDialog.newInstance();
                teamDialog.setListener(this);
                teamDialog.show(getFragmentManager(), "CreateTeam");
                break;
            }
        }
    }

    @Override
    public void OnTeamCreated() {

    }
}
