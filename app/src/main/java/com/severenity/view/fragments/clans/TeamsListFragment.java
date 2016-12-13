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
import com.severenity.view.Dialogs.CreateTeamDialog;
import com.severenity.view.custom.CustomListView;
import com.severenity.view.fragments.clans.pages.TeamEventsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andriy on 7/28/2016.
 */
public class TeamsListFragment extends Fragment implements View.OnClickListener, TeamEventsListener,
        CustomListView.LoadDataListener {

    private static final String ARGUMENT_TEAM_EVENTS_LISTENER = "teamEventsListener";
    private static final int ITEM_PER_REQUEST = 15;

    // listener that might handle event once team is created
    private TeamEventsListener mListener;
    private View mAddTeamButtonsView = null;
    private CreateTeamDialog mTeamDialog;
    private int mOffset = 0;

    private CustomListView mTeamsList;

    public TeamsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param listener - listener for the team events (created etc.)
     * @return A new instance of fragment {@link TeamsListFragment}.
     */
    public static TeamsListFragment newInstance(TeamEventsListener listener) {
        TeamsListFragment fragment = new TeamsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_TEAM_EVENTS_LISTENER, listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans_teams_list, container, false);

        mAddTeamButtonsView = view.findViewById(R.id.createTeam);
        mAddTeamButtonsView.setOnClickListener(this);
        TeamsListAdapter adapter = new TeamsListAdapter(getContext(), this);
        mTeamsList = (CustomListView)view.findViewById(R.id.teamsList);
        mTeamsList.setAdapter(adapter);
        mTeamsList.setListener(this);

        mListener = (TeamEventsListener) getArguments().getSerializable(ARGUMENT_TEAM_EVENTS_LISTENER);

        if (!App.getUserManager().getCurrentUser().getTeam().isEmpty() || (App.getUserManager().getCurrentUser().getLevel() < 5) ) {
            mAddTeamButtonsView.setVisibility(View.GONE);
        }

        requestTeams();

        return view;
    }

    private void requestTeams() {
        App.getTeamManager().getTeamsAsPage(mOffset, ITEM_PER_REQUEST, new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                try {
                    if ("success".equals(response.getString("result"))) {
                        JSONArray data = response.getJSONObject("data").getJSONArray("docs");
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
        if (!App.getUserManager().getCurrentUser().getTeam().isEmpty()) {
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
        if (!App.getUserManager().getCurrentUser().getTeam().isEmpty()) {
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
}
