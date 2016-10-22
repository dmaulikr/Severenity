package com.severenity.view.fragments.clans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.adapters.TeamsListAdapter;
import com.severenity.engine.adapters.UsersListAdapter;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.Team;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;
import com.severenity.view.custom.CustomListView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andriy on 8/25/2016.
 */
public class TeamFragment extends Fragment {

    private TextView mTeamModerator;
    private TextView mTeamName;
    private CustomListView mUsersInTeamList;
    private String mTeamID;

    public TeamFragment(String teamID) {
        // Required empty public constructor
        mTeamID = teamID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans_team, container, false);

        mTeamModerator = (TextView)view.findViewById(R.id.teamModeratorText);
        mTeamName = (TextView)view.findViewById(R.id.teamNameText);

        UsersListAdapter searchAdapter = new UsersListAdapter(getContext(), true);
        mUsersInTeamList = (CustomListView)view.findViewById(R.id.usersInTeamList);
        mUsersInTeamList.setAdapter(searchAdapter);
        mUsersInTeamList.showLoadSpinner(false);

        requestTeamInfo();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            requestTeamInfo();
        }
    }

    private void requestTeamInfo() {

        if (mTeamID != null && !mTeamID.isEmpty()) {
            App.getTeamManager().getTeam(mTeamID, new RequestCallback() {
                @Override
                public void onResponseCallback(JSONObject response) {
                    try {
                        if (response.getString("result").equals("success")) {
                            Team team = Utils.createTeamFromJSON(response.getJSONObject("data"));
                            if (team == null) {
                                Log.e(Constants.TAG, "Was not able to create user from JSON:" + response.getJSONObject("data"));
                                return;
                            }
                            mTeamModerator.setText(team.getModerator().getName());
                            mTeamName.setText(team.getName());
                            UsersListAdapter adapter = (UsersListAdapter)mUsersInTeamList.getAdapter();
                            if (adapter != null &&
                                    team.getModerator().getId().equals(App.getUserManager().getCurrentUser().getId())) {
                                adapter.setModeratorID(team.getModerator().getId());
                            }

                            mUsersInTeamList.clearData();
                            mUsersInTeamList.addNewData(team.getMembers());
                        } else {
                            Log.e(Constants.TAG, "Getting team by name fails");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorCallback(NetworkResponse response) {
                    Log.e(Constants.TAG, "Getting team by name fails");
                }
            });
        }
    }
}
