package com.severenity.view.fragments.clans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.severenity.R;
import com.severenity.view.fragments.NotifiableFragment;

/**
 * Created by Andriy on 8/1/2016.
 */
public class ClansTeamFramesContainerFragment extends Fragment implements NotifiableFragment, View.OnClickListener {

    private ClansTeamsListFragment mClansTeamsListFragment = new ClansTeamsListFragment();
    private ChatFragment mChatFragment = new ChatFragment();
    private UserLevelWarningFragment mWarning = new UserLevelWarningFragment();

    Fragment mCurrentFragment;

    public ClansTeamFramesContainerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.clans_frame_container_teams, container, false);

        getFragmentManager().beginTransaction()
                .add (R.id.TeamsFragmentContent, mChatFragment, "chatFragment")
                .hide (mChatFragment)
                .add (R.id.TeamsFragmentContent, mClansTeamsListFragment, "teamsList")
                .show(mClansTeamsListFragment)
                .commit();

        mCurrentFragment = mClansTeamsListFragment;

        ((TextView)view.findViewById(R.id.chatShow)).setOnClickListener(this);
        ((TextView)view.findViewById(R.id.teamListShow)).setOnClickListener(this);
        ((TextView)view.findViewById(R.id.teamShow)).setOnClickListener(this);

        getFragmentManager().beginTransaction()
        .add(R.id.TeamsWarningFragmentContent, mWarning, "Warning")
        .addToBackStack("Warning")
        .show(mWarning)
        .commit();

        return view;
    }

    @Override
    public void onFragmentShow(boolean show) {
        if (!show) {
            if (mCurrentFragment == mChatFragment) {
                FragmentTransaction t = getFragmentManager().beginTransaction();
                t.hide(mChatFragment);
                t.show(mClansTeamsListFragment);
                mCurrentFragment = mClansTeamsListFragment;
                t.commit();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.chatShow: {
                if (mCurrentFragment == mChatFragment)
                    break;
                FragmentTransaction t = getFragmentManager().beginTransaction();
                t.setCustomAnimations(R.anim.content_slide_up, R.anim.content_slide_up);
                t.hide(mCurrentFragment);
                t.show(mChatFragment);
                mCurrentFragment = mChatFragment;
                t.commit();
                break;
            }

            case R.id.teamListShow: {
                if (mCurrentFragment == mClansTeamsListFragment)
                    break;
                FragmentTransaction t = getFragmentManager().beginTransaction();
                t.setCustomAnimations(R.anim.content_slide_down, R.anim.chat_slide_down);
                t.hide(mCurrentFragment);
                t.show(mClansTeamsListFragment);
                mCurrentFragment = mClansTeamsListFragment;
                t.commit();
                break;
            }

            case R.id.teamShow: {
                break;
            }
        }
    }
}

