package com.severenity.view.custom;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.severenity.view.fragments.ChatFragment;
import com.severenity.view.fragments.InventoryFragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Andriy on 7/26/2016.
 */
public class TeamViewPagerAdapter extends FragmentStatePagerAdapter {
    private ChatFragment chatFragment = new ChatFragment();

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>(
            Arrays.asList(
                    chatFragment
            ));

    public TeamViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
