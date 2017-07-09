package com.severenity.view.fragments.clans.pages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.severenity.App;
import com.severenity.R;
import com.severenity.entity.user.User;
import com.severenity.utils.common.Constants;
import com.severenity.view.fragments.clans.ChatFragment;
import com.severenity.view.fragments.clans.FragmentInfo;
import com.severenity.view.fragments.clans.TeamFragment;
import com.severenity.view.fragments.clans.TeamsListFragment;
import com.severenity.view.fragments.clans.WarningFragment;

import java.util.Map;

/**
 * Created by Novosad on 8/14/2016.
 */
public class TeamsPage extends ClansPageBase implements TeamEventsListener {
    // variable to start button id's from.
    protected final int BUTTON_TEAM_LIST_ID = 10001;
    protected final int BUTTON_TEAM_ID      = 10002;
    protected final int BUTTON_CHAT_ID      = 10003;

    Intent intent = new Intent(Constants.INTENT_FILTER_TEAM_CHANGED);

    private View currentSelectedPagesButton = null;
    private LinearLayout mButtonsLayout;

    public TeamsPage() {
        super(R.layout.team_clans_container_fragment, R.id.teamFragmentsContent);
    }

    public static TeamsPage newInstance(String title) {
        TeamsPage teamsPage = new TeamsPage();
        Bundle args = new Bundle();

        args.putString(ARGUMENT_PAGE_TITLE, title);

        teamsPage.setArguments(args);
        return teamsPage;
    }

    @Override
    public View onCreate(View view) {
        mPageTitle = getResources().getString(R.string.title_team);

        View v = super.onCreate(view);
        mButtonsLayout = ((LinearLayout)v.findViewById(R.id.buttonsLayout));

        for (Map.Entry<Integer, FragmentInfo> entry : mFragments.entrySet()) {
            createButton(entry.getValue(), entry.getKey());
        }

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mFragments = new ArrayMap<>();

        boolean hasTeam = false;
        if (App.getUserManager().getCurrentUser().getTeamId() != null) {
            hasTeam = !App.getUserManager().getCurrentUser().getTeamId().isEmpty();
        }

        TeamsListFragment teamsListFragment = TeamsListFragment.newInstance();
        teamsListFragment.setListener(this);
        mFragments.put(BUTTON_CHAT_ID, new FragmentInfo(new ChatFragment(), "chatFragment", context.getResources().getString(R.string.title_chat), false));
        mFragments.put(BUTTON_TEAM_LIST_ID, new FragmentInfo(teamsListFragment, "teamsList", context.getResources().getString(R.string.team_list), !hasTeam));

        if (hasTeam) {
            String teamId = App.getUserManager().getCurrentUser().getTeamId();
            TeamFragment teamFragment = TeamFragment.newInstance(teamId);
            teamFragment.setListener(this);
            mFragments.put(BUTTON_TEAM_ID, new FragmentInfo(teamFragment, "teamFragment", context.getResources().getString(R.string.clans_team), true));
        }

        mWarningContentLayoutID = R.id.warningFragmentContent;

        // if users level is lower then 3 we show warning
        if (App.getUserManager().getCurrentUser().getLevel() < 3) {
            mWarningFragment = new FragmentInfo(new WarningFragment(), "warningFragment", context.getResources().getString(R.string.warning_fragment), true);
        }
    }

    /**
     * Creates a button as a TextView for the fragment
     *
     * @param fragmentInfo - information about the fragment fro which the button is going to be created
     * @param buttonID - identifies the ID of the resource
     */
    private void createButton(FragmentInfo fragmentInfo, int buttonID) {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                mFragments.size());

        TextView tv = new TextView(getContext());
        tv.setLayoutParams(param);
        tv.setId(buttonID);
        tv.setText(fragmentInfo.mFragmentButtonCaption);
        tv.setGravity(Gravity.CENTER);
        tv.setOnClickListener(this);
        tv.setBackgroundResource(0);
        if (fragmentInfo.mActiveFragment) {
            tv.setBackgroundResource(R.drawable.selected_view_backgroud);
            currentSelectedPagesButton = tv;
        }
        mButtonsLayout.addView(tv);
    }

    @Override
    public void onFragmentShow(boolean show) {
        // do nothing
    }

    @Override
    public void onClick(View view) {
        FragmentInfo info = mFragments.get(view.getId());

        if (info == null) {
            return;
        }

        if (getCurrentFragment().mFragmentName.equals(info.mFragmentName)) {
            return;
        }

        FragmentTransaction fragment = getFragmentManager().beginTransaction();
        fragment.setCustomAnimations(R.anim.chat_slide_up, R.anim.content_slide_up);
        switchFragmentTo(fragment, info);

        if (currentSelectedPagesButton != null) {
            currentSelectedPagesButton.setBackgroundResource(0);
        }

        view.setBackgroundResource(R.drawable.selected_view_backgroud);
        currentSelectedPagesButton = view;
    }

    /**
     * Creates team info fragment in the teams page when player has joined or created a team.
     */
    protected void createTeamFragment() {
        if (mFragments.get(BUTTON_TEAM_ID) != null) {
            return;
        }

        if (getView() == null) {
            return;
        }

        TeamFragment teamFragment = TeamFragment.newInstance(App.getUserManager().getCurrentUser().getTeamId());
        teamFragment.setListener(this);
        FragmentInfo info = new FragmentInfo(
                teamFragment,
            "teamFragment",
            getResources().getString(R.string.clans_team),
            false
        );

        mFragments.put(BUTTON_TEAM_ID, info);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.teamFragmentsContent, info.mFragment, info.mFragmentName);
        transaction.hide(info.mFragment);
        transaction.commit();

        setupLayoutParamsForInnerFragment();

        createButton(info, BUTTON_TEAM_ID);

        getView().findViewById(BUTTON_TEAM_ID).callOnClick();
    }

    /**
     * Removes team info fragment from the teams page when player does not have team.
     */
    protected void removeTeamFragment() {
        if (mFragments.get(BUTTON_TEAM_ID) == null) {
            return;
        }

        if (getView() == null) {
            return;
        }

        FragmentInfo info = mFragments.remove(BUTTON_TEAM_ID);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.remove(info.mFragment);
        transaction.commit();

        mButtonsLayout.removeView(getView().findViewById(BUTTON_TEAM_ID));

        setupLayoutParamsForInnerFragment();

        // click teamAll button.
        getView().findViewById(BUTTON_TEAM_LIST_ID).callOnClick();
    }

    /**
     * Setups layout parameters for the inner fragment in the teams page.
     */
    private void setupLayoutParamsForInnerFragment() {
        if (getView() == null) {
            return;
        }

        for (Map.Entry<Integer, FragmentInfo> entry : mFragments.entrySet()) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mFragments.size());

            TextView tv = (TextView) getView().findViewById(entry.getKey());

            if (tv != null) {
                tv.setLayoutParams(param);
            }
        }
    }

    @Override
    public void onTeamCreated() {
        createTeamFragment();

        intent.putExtra(Constants.INTENT_EXTRA_SHOW_TEAM_QUESTS, true);
        App.getLocalBroadcastManager().sendBroadcast(intent);
    }

    @Override
    public void onTeamJoined() {
        createTeamFragment();

        intent.putExtra(Constants.INTENT_EXTRA_SHOW_TEAM_QUESTS, true);
        App.getLocalBroadcastManager().sendBroadcast(intent);
    }

    @Override
    public void onTeamLeft() {
        removeTeamFragment();

        intent.putExtra(Constants.INTENT_EXTRA_SHOW_TEAM_QUESTS, false);
        App.getLocalBroadcastManager().sendBroadcast(intent);
    }
}
