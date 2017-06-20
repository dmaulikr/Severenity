package com.severenity.view.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.GamePlace;
import com.severenity.entity.User;
import com.severenity.entity.quest.Quest;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;
import com.severenity.view.dialogs.PlacesOwnedDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends DialogFragment {
    private static String ARGUMENT_USER_ID = "userId";

    private TextView tvMetersPassed;
    private TextView tvQuestsCompleted;
    private TextView tvPlacesOwned;
    private TextView tvLevel;
    private TextView tvTeam;
    private TextView tvTickets;
    private TextView tvTips;
    private TextView tvUserName;

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();

        args.putString(ARGUMENT_USER_ID, userId);

        profileFragment.setArguments(args);
        return profileFragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Bundle args = getArguments();
        final String userId = args.getString(ARGUMENT_USER_ID);

        User user = App.getUserManager().getCurrentUser();

        if (user == null || userId == null) {
            return view;
        }

        tvMetersPassed = (TextView) view.findViewById(R.id.tvProfileStatMeters);
        tvLevel = (TextView) view.findViewById(R.id.tvProfileStatLevel);
        tvTeam = (TextView) view.findViewById(R.id.tvProfileStatTeam);
        tvQuestsCompleted = (TextView) view.findViewById(R.id.tvProfileStatQuestsCompleted);
        tvPlacesOwned = (TextView) view.findViewById(R.id.tvProfileStatPlacesOwned);
        tvTickets = (TextView) view.findViewById(R.id.tvProfileStatTickets);
        tvTips = (TextView) view.findViewById(R.id.tvProfileStatTips);
        tvUserName = (TextView) view.findViewById(R.id.tvProfileStatUsername);
        CircleImageView civAvatar = (CircleImageView) view.findViewById(R.id.civAvatar);

        if (!userId.equals(user.getId())) {
            Picasso.with(getActivity()).load("https://graph.facebook.com/" + userId + "/picture?type=large").into(civAvatar);
            App.getUserManager().getUser(userId, new RequestCallback() {
                @Override
                public void onResponseCallback(JSONObject response) {
                    try {
                        if ("error".equals(response.getString("result"))) {
                            Log.e(Constants.TAG, response.getJSONObject("data").toString());
                            return;
                        }

                        User user = Utils.createUserFromJSON(response.getJSONObject("data"));
                        updateUIInfo(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorCallback(NetworkResponse response) {
                    if (response != null) {
                        Log.e(Constants.TAG, "Cannot get info for user " + userId + ". Error: " + response.toString());
                    } else {
                        Log.e(Constants.TAG, "Cannot get info for user " + userId + ". Response is null.");
                    }
                }
            });
        } else {
            Picasso.with(getActivity()).load("https://graph.facebook.com/" + user.getId() + "/picture?type=large").into(civAvatar);

            tvPlacesOwned.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlacesOwnedDialog dialog = PlacesOwnedDialog.newInstance();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    dialog.show(manager, "placesOwnedInfo");
                }
            });

            updateUIInfo(user);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateUIInfo(App.getUserManager().getCurrentUser());
        App.getLocalBroadcastManager().registerReceiver(
                updateUIReceiver,
                new IntentFilter(Constants.INTENT_FILTER_UPDATE_UI)
        );
    }

    @Override
    public void onPause() {
        App.getLocalBroadcastManager().unregisterReceiver(updateUIReceiver);

        super.onPause();
    }

    private BroadcastReceiver updateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String displayedUserId = getArguments().getString(ARGUMENT_USER_ID);
            if (displayedUserId != null && displayedUserId.equals(App.getUserManager().getCurrentUser().getId())) {
                updateUIInfo(App.getUserManager().getCurrentUser());
            }
        }
    };

    private void updateUIInfo(User user) {
        if (user == null) {
            return;
        }

        tvUserName.setText(user.getName());

        tvLevel.setText(String.format(getResources().getString(R.string.profile_stat_level), user.getLevel()));

        if (user.getTeamId().isEmpty()) {
            tvTeam.setText(getResources().getString(R.string.no_team));
        } else {
            tvTeam.setText(String.format(getResources().getString(R.string.profile_stat_team), user.getTeamName()));
        }

        tvMetersPassed.setText(String.format(getResources().getString(R.string.profile_stat_meters), user.getDistance()));

        ArrayList<GamePlace> places = App.getPlacesManager().findPlacesByOwner(user.getId());

        if (places.size() > 0) {
            tvPlacesOwned.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_place_marker, 0, android.R.drawable.ic_media_play, 0);
        } else {
            tvPlacesOwned.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_place_marker, 0, 0, 0);
        }
        tvPlacesOwned.setText(String.format(getResources().getString(R.string.profile_stat_places_owned), places.size()));

        ArrayList<Quest> completedQuests = new ArrayList<>();
        for (Quest quest : App.getQuestManager().getQuests()) {
            if (quest.getStatus() == Quest.QuestStatus.Finished) {
                completedQuests.add(quest);
            }
        }

        tvQuestsCompleted.setText(String.format(getResources().getString(R.string.profile_stat_quests), completedQuests.size()));

        tvTickets.setText(String.format(getResources().getString(R.string.profile_stat_tickets), user.getTickets()));
        tvTips.setText(String.format(getResources().getString(R.string.profile_stat_tips), user.getTips()));
    }
}
