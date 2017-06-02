package com.severenity.engine.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.severenity.view.fragments.clans.pages.ClansPageBase;

import java.util.ArrayList;

/**
 * Created by Novosad on 8/13/2016.
 */
public class ClansViewPagerAdapter extends FragmentStatePagerAdapter {
    private String mPageTitles[];
    private ArrayList<ClansPageBase> mFragments;

    public ClansViewPagerAdapter(FragmentManager fm, ArrayList<ClansPageBase> list) {
        super(fm);
        mFragments = list;
        mPageTitles = new String[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            mPageTitles[i] = mFragments.get(i).getTitle();
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mPageTitles[position];
    }
}
