package com.severenity.view.fragments.clans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NetworkResponse;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.adapters.TeamsListAdapter;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.Team;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;
import com.severenity.view.custom.CustomListView;
import com.severenity.view.dialogs.CreateTeamDialog;
import com.severenity.view.fragments.clans.pages.TeamEventsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Novosad on 7/28/2016.
 */
public class TeamsListFragment extends Fragment implements View.OnClickListener, TeamEventsListener,
        CustomListView.LoadDataListener {
    private static final int ITEM_PER_REQUEST = 15;

    // listener that might handle event once team is created
    private TeamEventsListener mListener;
    private View mAddTeamButtonsView;
    private CreateTeamDialog mTeamDialog;
    private int mOffset = 0;

    private CustomListView mTeamsList;

    public TeamsListFragment() {
        // Required empty public constructor
    }

    public static TeamsListFragment newInstance() {
        return new TeamsListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans_teams_list, container, false);

        mAddTeamButtonsView = view.findViewById(R.id.createTeam);
        mAddTeamButtonsView.setOnClickListener(this);
        TeamsListAdapter adapter = new TeamsListAdapter(getContext(), this);
        mTeamsList = view.findViewById(R.id.teamsList);
        mTeamsList.setAdapter(adapter);
        mTeamsList.setListener(this);

        if (!App.getUserManager().getCurrentUser().getTeamId().isEmpty() || App.getUserManager().getCurrentUser().getLevel() < 5) {
            mAddTeamButtonsView.setVisibility(View.GONE);
        }

        requestTeams();

        return view;
    }

    /**
     * Requests and parses new teams page.
     */
    private void requestTeams() {
        App.getTeamManager().getTeamsAsPage(mOffset, ITEM_PER_REQUEST, new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                try {
                    if ("success".equals(response.getString("result"))) {
                        JSONArray data = response.getJSONArray("data");
                        mOffset += data.length();

                        List<Team> result = new ArrayList<>(data.length());
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonTeam = data.getJSONObject(i);
                            Team team = Utils.createTeamFromJSON(jsonTeam);
                            if (team != null) {
                                result.add(team);
                            }
                        }

                        mTeamsList.addNewData(result);
                    } else {
                        Log.e(Constants.TAG, "Retrieve teams error: " + response.getJSONObject("data").toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorCallback(NetworkResponse response) {
                Log.e(Constants.TAG, "Request teams fails: " + (response == null ? "" : response.toString()));
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createTeam:
                mTeamDialog = CreateTeamDialog.newInstance();
                mTeamDialog.setListener(this);
                mTeamDialog.show(getFragmentManager(), "CreateTeam");
                break;
        }
    }

    @Override
    public void onTeamCreated() {
        mTeamDialog.dismiss();
        mTeamDialog = null;
        if (!App.getUserManager().getCurrentUser().getTeamId().isEmpty()) {
            mAddTeamButtonsView.setVisibility(View.GONE);
        }
        Utils.hideKeyboard(getActivity());

        // pass information further to the main holder
        // so that it can create team fragment and switch
        // user to it.
        mListener.onTeamCreated();
    }

    @Override
    public void onTeamJoined() {
        if (!App.getUserManager().getCurrentUser().getTeamId().isEmpty()) {
            mAddTeamButtonsView.setVisibility(View.GONE);
        }

        mListener.onTeamJoined();
    }

    @Override
    public void onTeamLeft() {
        mOffset = 0;
        mTeamsList.clearData();
        requestTeams();
        if (mListener != null) {
            mListener.onTeamLeft();
        }
    }

    @Override
    public void loadData() {
        requestTeams();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            mOffset = 0;
            mTeamsList.clearData();
            requestTeams();
        }
    }

    public void setListener(TeamEventsListener listener) {
        mListener = listener;
    }
}
