package com.severenity.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.adapters.TeamQuestsListAdapter;
import com.severenity.entity.quest.team.TeamQuest;
import com.severenity.entity.quest.team.TeamQuestPart;
import com.severenity.utils.common.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class TeamQuestsFragment extends Fragment {
    Realm realm;

    public TeamQuestsFragment() {
        // Required empty public constructor
    }

    public static TeamQuestsFragment newInstance() {
        return new TeamQuestsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        // Inflate the layout for this fragment
        App.getTeamQuestManager().getTeamQuests();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_quests, container, false);
        ExpandableListView teamQuestsListView = (ExpandableListView) view.findViewById(R.id.elvTeamQuests);

        RealmResults<TeamQuest> teamQuestsRealm = realm.where(TeamQuest.class).findAll();
        final ArrayList<TeamQuest> teamQuests = new ArrayList<>(realm.copyFromRealm(teamQuestsRealm));

        if (teamQuests.size() > 0) {

            Map<String, List<TeamQuestPart>> teamQuestParts = new HashMap<>();
            teamQuestParts.put(teamQuests.get(0).getId(), teamQuests.get(0).getParts());

            TeamQuestsListAdapter teamQuestsListAdapter = new TeamQuestsListAdapter(getActivity(), teamQuests, teamQuestParts);
            teamQuestsListView.setAdapter(teamQuestsListAdapter);

            teamQuestsListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                    Log.d(Constants.TAG, teamQuests.get(groupPosition) + " parts expanded.");
                }
            });

            teamQuestsListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {
                    Log.d(Constants.TAG, teamQuests.get(groupPosition) + " parts collapsed.");
                }
            });
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
