package com.nosad.sample.view.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.adapters.QuestsAdapter;
import com.nosad.sample.engine.managers.messaging.FCMListener;
import com.nosad.sample.entity.quest.CaptureQuest;
import com.nosad.sample.entity.quest.CollectQuest;
import com.nosad.sample.entity.quest.DistanceQuest;
import com.nosad.sample.entity.quest.Quest;
import com.nosad.sample.utils.common.Constants;
import com.nosad.sample.view.activities.MainActivity;
import com.nosad.sample.view.custom.DividerItemDecoration;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestsFragment extends Fragment {
    private MainActivity activity;
    private RecyclerView questsList;
    private TextView emptyList;

    private QuestsAdapter questsAdapter;
    private ArrayList<Quest> quests = new ArrayList<>();

    public QuestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter questIntentFilter = new IntentFilter();
        questIntentFilter.addAction(Constants.INTENT_FILTER_QUEST_UPDATE);
        questIntentFilter.addAction(Constants.INTENT_FILTER_NEW_QUEST);

        App.getLocalBroadcastManager().registerReceiver(questReceiver, questIntentFilter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            App.getQuestManager().refreshWithQuestsFromServer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        App.getLocalBroadcastManager().unregisterReceiver(questReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quests, container, false);

        App.getQuestManager().refreshWithQuestsFromServer();
        questsAdapter = new QuestsAdapter(activity, quests);

        questsList = (RecyclerView) view.findViewById(R.id.rvQuests);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        questsList.setLayoutManager(linearLayoutManager);
        questsList.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST));
        questsList.setItemAnimator(new DefaultItemAnimator());
        questsList.setAdapter(questsAdapter);
        activity.registerForContextMenu(questsList);

        emptyList = (TextView) view.findViewById(R.id.tvEmptyList);
        checkEmptyList();

        return view;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = ((QuestsAdapter) questsList.getAdapter()).getPosition();
        switch (item.getItemId()) {
            case R.id.menu_delete:
                Quest quest = questsAdapter.getItem(position);
                App.getQuestManager().deleteQuest(quest);
                questsAdapter.remove(quest);
                checkEmptyList();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Checks whether adapter with items is empty. If so - display appropriate message.
     */
    private void checkEmptyList() {
        if (questsAdapter.getItemCount() > 0) {
            emptyList.setVisibility(View.GONE);
        } else {
            emptyList.setVisibility(View.VISIBLE);
        }
    }

    /**
     * GCM receiver for the message sent from {@link FCMListener}
     * Reacts to registration and messages.
     */
    private BroadcastReceiver questReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle questObj = intent.getExtras();
            if (intent.getAction().equalsIgnoreCase(Constants.INTENT_FILTER_NEW_QUEST)) {
                if (questObj.getBoolean(Constants.INTENT_EXTRA_SINGLE_QUEST)) {
                    int questType = questObj.getInt("type");

                    Quest q = new Quest();
                    q.setId(questObj.getLong("id"));
                    q.setTitle(questObj.getString("title"));
                    q.setCredits(questObj.getLong("credits"));
                    q.setExperience(questObj.getLong("experience"));

                    String expirationTime = questObj.getString("expirationTime");
                    if (expirationTime != null && !expirationTime.equals("null")) {
                        q.setExpirationTime(expirationTime);
                    }

                    q.setStatus(Quest.QuestStatus.Accepted);

                    if (questType == Quest.QuestType.Distance.ordinal()) {
                        q.setType(Quest.QuestType.Distance);

                        int distance = questObj.getInt("distance");
                        q = new DistanceQuest(q, distance);
                    } else if (questType == Quest.QuestType.Capture.ordinal()) {
                        q.setType(Quest.QuestType.Capture);

                        String placeType = questObj.getString("placeType");
                        int placeTypeValue = questObj.getInt("placeTypeValue");
                        q = new CaptureQuest(q, placeType, placeTypeValue);
                    } else if (questType == Quest.QuestType.Collect.ordinal()) {
                        q.setType(Quest.QuestType.Collect);
                        int characteristic = questObj.getInt("characteristic");
                        int characteristicAmount = questObj.getInt("characteristicAmount");
                        Constants.Characteristic c;
                        if (characteristic == Constants.Characteristic.Level.ordinal()) {
                            c = Constants.Characteristic.Level;
                        } else if (characteristic == Constants.Characteristic.Experience.ordinal()) {
                            c = Constants.Characteristic.Experience;
                        } else if (characteristic == Constants.Characteristic.Intelligence.ordinal()) {
                            c = Constants.Characteristic.Intelligence;
                        } else if (characteristic == Constants.Characteristic.Immunity.ordinal()) {
                            c = Constants.Characteristic.Immunity;
                        } else {
                            Log.e(Constants.TAG, "Unknown characteristic " + characteristic + " received.");
                            return;
                        }

                        q = new CollectQuest(q, c, characteristicAmount);
                    }
                    questsAdapter.add(q);
                } else {
                    questsAdapter.refreshWith(App.getQuestManager().getQuests());
                }
            } else if (intent.getAction().equalsIgnoreCase(Constants.INTENT_FILTER_QUEST_UPDATE)) {
                Quest q = new Quest();
                q.setProgress(questObj.getInt("progress"));
                q.setStatus(Quest.QuestStatus.values()[questObj.getInt("status")]);
                q.setId(questObj.getLong("questId"));
                questsAdapter.update(q);
            }
            checkEmptyList();
        }
    };
}
