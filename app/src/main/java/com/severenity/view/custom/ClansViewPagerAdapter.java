package com.severenity.view.custom;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.severenity.R;
import com.severenity.view.fragments.ChatFragment;
import com.severenity.view.fragments.ClansTeamFragment;
import com.severenity.view.fragments.ClansWorldFragment;
import com.severenity.view.fragments.ProfileFragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Andriy on 7/26/2016.
 */
public class ClansViewPagerAdapter extends FragmentStatePagerAdapter {
    private ChatFragment chatFragment = new ChatFragment();
    private ClansWorldFragment worldClansFragment = new ClansWorldFragment();
    private ClansTeamFragment teamClansFragment = new ClansTeamFragment();
    private Context mContext;

    private String mPageTitles[];

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>(
            Arrays.asList(
                    worldClansFragment,
                    teamClansFragment,
                    chatFragment
            ));

    public ClansViewPagerAdapter(FragmentManager fm, Context nContext) {
        super(fm);
        mContext = nContext;
        mPageTitles = new String[] {
                mContext.getResources().getString(R.string.title_world),
                mContext.getResources().getString(R.string.title_team),
                mContext.getResources().getString(R.string.title_chat)};
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }


    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTitles[position];
    }
}
