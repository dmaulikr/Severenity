package com.severenity.view.fragments.clans.pages;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.severenity.App;
import com.severenity.R;
import com.severenity.view.fragments.clans.ChatFragment;
import com.severenity.view.fragments.clans.TeamFragment;
import com.severenity.view.fragments.clans.TeamsListFragment;
import com.severenity.view.fragments.clans.FragmentInfo;
import com.severenity.view.fragments.clans.WarningFragment;

import java.util.ArrayList;

/**
 * Created by Andriy on 8/14/2016.
 */
public class TeamsPage extends ClansPageBase {

    private Context mContext;

    private View currentSelectedPagesButton = null;

    public TeamsPage(Context context) {
        super(  R.layout.team_clans_container_fragment,
                R.id.teamFragmentsContent);

        mContext = context;
        mPageTitle = mContext.getResources().getString(R.string.title_team);
        mFragments = new ArrayList<>(3);

        boolean showTeamPageFirst = !App.getUserManager().getCurrentUser().getTeam().isEmpty();
        mFragments.add(new FragmentInfo(new TeamsListFragment(), "teamsList", "Team list", !showTeamPageFirst));
        mFragments.add(new FragmentInfo(new TeamFragment(), "teamFragment", "Team", showTeamPageFirst));
        mFragments.add(new FragmentInfo(new ChatFragment(), "chatFragment", "Chat", false));
        mWarningContentLayoutID = R.id.warningFragmentContent;

        // if users level is lower then 3 we show warning
        if (App.getUserManager().getCurrentUser().getLevel() < 3) {
            mWarningFragment = new FragmentInfo(new WarningFragment(), "Warning", "Warning", true);
        }
    }

    @Override
    public View onCreate(View view) {
        View v = super.onCreate(view);

        int buttonsIDcounter = 0;
        for(FragmentInfo fragmentInfo: mFragments) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mFragments.size());

            TextView tv = new TextView(getContext());
            tv.setLayoutParams(param);
            tv.setId(BUTTONS_ID_OFFSET + buttonsIDcounter);
            tv.setText(fragmentInfo.mFragmentButtonCaption);
            tv.setGravity(Gravity.CENTER);
            tv.setOnClickListener(this);
            if (fragmentInfo.mActiveFragment) {
                tv.setBackgroundResource(R.drawable.selected_view_backgroud);
                currentSelectedPagesButton = tv;
            }
            buttonsIDcounter++;
            ((LinearLayout)v.findViewById(R.id.buttonsLayout)).addView(tv);
        }

        return v;
    }

    @Override
    public void onFragmentShow(boolean show) {

    }

    @Override
    public void onClick(View view) {

        int fragmentToShow = 0;
        FragmentTransaction fragment = null;
        switch (view.getId()) {

            case BUTTONS_ID_OFFSET: /*Teams list*/ {
                fragment = getFragmentManager().beginTransaction();
                if (getCurrentFragment().mFragmentName.equals("teamsList")) {
                    return;
                }
                fragmentToShow = 0;
                break;
            }

            case BUTTONS_ID_OFFSET + 1: /*Team*/ {
                fragment = getFragmentManager().beginTransaction();
                if (getCurrentFragment().mFragmentName.equals("teamFragment")) {
                    return;
                }
                fragmentToShow = 1;
                break;
            }

            case BUTTONS_ID_OFFSET + 2: /*Chat*/ {
                fragment = getFragmentManager().beginTransaction();
                if (getCurrentFragment().mFragmentName.equals("chatFragment")) {
                    return;
                }
                fragmentToShow = 2;
                break;
            }
        }

        if (fragment != null) {
            fragment.setCustomAnimations(R.anim.chat_slide_up, R.anim.content_slide_up);
            switchFragmentTo(fragment, mFragments.get(fragmentToShow));
        }

        if (currentSelectedPagesButton != null) {
            currentSelectedPagesButton.setBackgroundResource(0);
        }
        view.setBackgroundResource(R.drawable.selected_view_backgroud);
        currentSelectedPagesButton = view;
    }
}
