package com.severenity.view.fragments.clans;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.Team;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andriy on 8/25/2016.
 */
public class TeamFragment extends Fragment {

    private TextView mTeamModerator;
    private TextView mTeamName;

    public TeamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans_team, container, false);

        mTeamModerator = (TextView)view.findViewById(R.id.teamModeratorText);
        mTeamName = (TextView)view.findViewById(R.id.teamNameText);

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
        String currentUsersTeam = App.getUserManager().getCurrentUser().getTeam();
        if (currentUsersTeam != null && !currentUsersTeam.isEmpty()) {
            App.getTeamManager().getTeam(currentUsersTeam, new RequestCallback() {
                @Override
                public void onResponseCallback(JSONObject response) {
                    try {
                        if (response.getString("result").equals("success")) {
                            Team team = Utils.createTeamFromJSON(response.getJSONObject("data"));
                            mTeamModerator.setText(team.getModerator().getName());
                            mTeamName.setText(team.getName());
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
