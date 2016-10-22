package com.severenity.view.fragments.clans.pages;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.severenity.App;
import com.severenity.R;
import com.severenity.view.Dialogs.CreateTeamDialog;
import com.severenity.view.fragments.clans.ChatFragment;
import com.severenity.view.fragments.clans.FragmentInfo;
import com.severenity.view.fragments.clans.TeamFragment;
import com.severenity.view.fragments.clans.TeamsListFragment;
import com.severenity.view.fragments.clans.WarningFragment;

import java.util.Map;

/**
 * Created by Andriy on 8/14/2016.
 */
public class TeamsPage extends ClansPageBase implements TeamEventsListener {

    // variable to start button id's from.
    protected final int BUTTONS_ID_OFFSET = 10001;

    private Context mContext;

    private View currentSelectedPagesButton = null;
    private LinearLayout mButtonsLayout;

    public TeamsPage(Context context) {
        super(R.layout.team_clans_container_fragment, R.id.teamFragmentsContent);

        mContext = context;
        mPageTitle = mContext.getResources().getString(R.string.title_team);
        mFragments = new ArrayMap<>();

        boolean showTeamPageFirst = false;
        if (App.getUserManager().getCurrentUser().getTeam() != null) {
            showTeamPageFirst = !App.getUserManager().getCurrentUser().getTeam().isEmpty();
        }

        mFragments.put(BUTTONS_ID_OFFSET, new FragmentInfo(new TeamsListFragment(this), "teamsList", mContext.getResources().getString(R.string.team_list), !showTeamPageFirst));

        if (showTeamPageFirst) {
            mFragments.put(BUTTONS_ID_OFFSET + 1, new FragmentInfo(new TeamFragment(App.getUserManager().getCurrentUser().getTeam()), "teamFragment", mContext.getResources().getString(R.string.clans_team), showTeamPageFirst));
        }

        mFragments.put(BUTTONS_ID_OFFSET + 2, new FragmentInfo(new ChatFragment(), "chatFragment", mContext.getResources().getString(R.string.chat), false));
        mWarningContentLayoutID = R.id.warningFragmentContent;

        // if users level is lower then 3 we show warning
        if (App.getUserManager().getCurrentUser().getLevel() < 3) {
            mWarningFragment = new FragmentInfo(new WarningFragment(), "warningFragment", mContext.getResources().getString(R.string.warning_fragment), true);
        }
    }

    @Override
    public View onCreate(View view) {
        View v = super.onCreate(view);
        mButtonsLayout = ((LinearLayout)v.findViewById(R.id.buttonsLayout));

        for (Map.Entry<Integer, FragmentInfo> entry : mFragments.entrySet()) {
            createButton(entry.getValue(), entry.getKey());
        }

        return v;
    }

    /**
     * Creates a button as a TextView for the fragment
     *
     * @param fragmentInfo - information about the fragment fro which the
     *                     button is going to be created
     * @param buttonID     - identifies the ID of the resource
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

    protected void createFragmentAndButtonForTheTeam() {
        if (mFragments.get(BUTTONS_ID_OFFSET + 1) == null) {
            FragmentInfo info = new FragmentInfo(new TeamFragment(App.getUserManager().getCurrentUser().getTeam()), "teamFragment", "Team", false);
            mFragments.put(BUTTONS_ID_OFFSET + 1, info);

            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.add(R.id.teamFragmentsContent, info.mFragment, info.mFragmentName);
            transaction.hide(info.mFragment);
            transaction.commit();

            for (Map.Entry<Integer, FragmentInfo> entry : mFragments.entrySet()) {
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        mFragments.size());

                TextView tv = (TextView)getView().findViewById(entry.getKey());
                if (tv != null) {
                    tv.setLayoutParams(param);
                }
            }
            createButton(info, BUTTONS_ID_OFFSET + 1);
            ((TextView)getView().findViewById(BUTTONS_ID_OFFSET + 1)).callOnClick();
        }
    }

    @Override
    public void OnTeamCreated() {
        createFragmentAndButtonForTheTeam();
    }

    @Override
    public void OnTeamJoined() {
        createFragmentAndButtonForTheTeam();
    }

    @Override
    public void OnTeamLeft() {

    }
}
