package com.severenity.view.fragments.clans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.App;
import com.severenity.R;
import com.severenity.view.Dialogs.CreateTeamDialog;

/**
 * Created by Andriy on 7/28/2016.
 */
public class TeamsListFragment extends Fragment implements View.OnClickListener, CreateTeamDialog.OnTeamCreatedListener {

    private View mAddTeamButtonsView = null;
    private CreateTeamDialog mTeamDialog;

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
        //if (App.getUserManager().getCurrentUser().getLevel() < 5
        if (!App.getUserManager().getCurrentUser().getTeam().isEmpty()) {
            mAddTeamButtonsView.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createTeam: {
                mTeamDialog = CreateTeamDialog.newInstance();
                mTeamDialog.setListener(this);
                mTeamDialog.show(getFragmentManager(), "CreateTeam");
                break;
            }
        }
    }

    @Override
    public void OnTeamCreated() {
        mTeamDialog.dismiss();
        mTeamDialog = null;
        if (!App.getUserManager().getCurrentUser().getTeam().isEmpty()) {
            mAddTeamButtonsView.setVisibility(View.GONE);
        }
    }
}
