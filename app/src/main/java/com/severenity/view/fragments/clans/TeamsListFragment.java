package com.severenity.view.fragments.clans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NetworkResponse;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.adapters.TeamsListAdapter;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.Team;
import com.severenity.entity.User;
import com.severenity.utils.Utils;
import com.severenity.view.Dialogs.CreateTeamDialog;
import com.severenity.view.custom.CustomListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andriy on 7/28/2016.
 */
public class TeamsListFragment extends Fragment implements View.OnClickListener, CreateTeamDialog.OnTeamCreatedListener,
        CustomListView.LoadDataListener{

    private View mAddTeamButtonsView = null;
    private CreateTeamDialog mTeamDialog;
    private int mOffset = 0;
    private final int ITEM_PER_REQUEST = 15;

    private CustomListView mTeamsList;

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
        TeamsListAdapter adapter = new TeamsListAdapter(getContext());
        mTeamsList = (CustomListView)view.findViewById(R.id.teamsList);
        mTeamsList.setAdapter(adapter);
        mTeamsList.setListener(this);

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
                    if (response.getString("result").equals("success")) {
                        JSONArray data = response.getJSONObject("data").getJSONArray("docs");
                        mOffset += data.length();


                        List<Team> result = new ArrayList<Team>(data.length());
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

                int i = 0;
                i = i + 1;
            }
        });
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

    @Override
    public void loadData() { requestTeams(); }

}
