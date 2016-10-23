package com.severenity.view.fragments.clans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.adapters.TeamsListAdapter;
import com.severenity.engine.adapters.UsersListAdapter;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.Team;
import com.severenity.entity.User;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;
import com.severenity.view.Dialogs.CustomAlertDialog;
import com.severenity.view.custom.CustomListView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andriy on 8/25/2016.
 */
public class TeamFragment extends Fragment implements AdapterView.OnItemLongClickListener {

    private TextView mTeamModerator;
    private TextView mTeamName;
    private CustomListView mUsersInTeamList;
    private String mTeamID;
    private TeamFragment TeamFragmentInstance = this;

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

        UsersListAdapter searchAdapter = new UsersListAdapter(getContext());
        mUsersInTeamList = (CustomListView)view.findViewById(R.id.usersInTeamList);
        mUsersInTeamList.setAdapter(searchAdapter);
        mUsersInTeamList.showLoadSpinner(false);
        mUsersInTeamList.setChoiceMode(CustomListView.CHOICE_MODE_SINGLE);
        mUsersInTeamList.setSelector(R.drawable.item_selector);

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
                            if (App.getUserManager().getCurrentUser().getId().equals(team.getModerator().getId())) {
                                Log.i(Constants.TAG, "Current user is moderator for the team. Give ability to delete users");
                                mUsersInTeamList.setLongClickable(true);
                                mUsersInTeamList.setOnItemLongClickListener(TeamFragmentInstance);
                            }
                            mTeamModerator.setHint(team.getModerator().getId());
                            mTeamName.setText(team.getName());
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

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        User user = (User)mUsersInTeamList.getItemAtPosition(i);
        String moderatorID = mTeamModerator.getHint().toString();
        if (!user.getId().equals(moderatorID)) {
            Toast.makeText(getContext(), "not moderator", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            Toast.makeText(getContext(), "moderator", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
