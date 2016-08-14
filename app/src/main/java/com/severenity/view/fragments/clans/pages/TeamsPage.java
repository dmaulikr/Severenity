package com.severenity.view.fragments.clans.pages;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.severenity.R;
import com.severenity.view.fragments.clans.ChatFragment;
import com.severenity.view.fragments.clans.ClansTeamsListFragment;
import com.severenity.view.fragments.clans.FragmentInfo;
import com.severenity.view.fragments.clans.UserLevelWarningFragment;

import java.util.ArrayList;

/**
 * Created by Andriy on 8/14/2016.
 */
public class TeamsPage extends ClansPageBase {

    private Context mContext;

    public TeamsPage(Context context) {
        super(  R.layout.team_clans_container_fragment,
                R.id.teamFragmentsContent);

        mContext = context;
        mPageTitle = mContext.getResources().getString(R.string.title_team);
        mFragments = new ArrayList<>(2);
        mFragments.add(new FragmentInfo(new ClansTeamsListFragment(), "teamsList", "Team list", true));
        mFragments.add(new FragmentInfo(new ChatFragment(), "chatFragment", "Chat", false));

        // if users level is lower then 3 we show warning
        mWarningContentLayoutID = R.id.warningFragmentContent;
        mWarningFragment = new FragmentInfo(new UserLevelWarningFragment(), "Warning", "Warning", true);
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

    }
}
