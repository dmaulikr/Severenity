package com.nosad.sample.view.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.adapters.QuestsAdapter;
import com.nosad.sample.entity.quest.Quest;
import com.nosad.sample.view.activities.MainActivity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestsFragment extends Fragment {
    private MainActivity activity;

    public QuestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quests, container, false);

        ArrayList<Quest> quests = App.getQuestManager().getQuests();
        QuestsAdapter questsAdapter = new QuestsAdapter(
            activity,
            R.layout.quest_item,
            quests
        );

        ListView questsList = (ListView) view.findViewById(R.id.lvQuests);
        questsList.setAdapter(questsAdapter);

        questsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
            }
        });

        return view;
    }


}
