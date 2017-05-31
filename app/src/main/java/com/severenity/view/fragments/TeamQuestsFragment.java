package com.severenity.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.App;
import com.severenity.R;

public class TeamQuestsFragment extends Fragment {
    public TeamQuestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TeamQuestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TeamQuestsFragment newInstance() {
        return new TeamQuestsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        App.getTeamQuestManager().getTeamQuests();
        return inflater.inflate(R.layout.fragment_team_quests, container, false);
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
