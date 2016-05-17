package com.nosad.sample.view.custom;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.nosad.sample.view.fragments.InventoryFragment;
import com.nosad.sample.view.fragments.ProfileFragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Novosad on 5/17/16.
 */
public class PlayerViewPagerAdapter extends FragmentStatePagerAdapter {
    private ProfileFragment profileFragment = new ProfileFragment();
    private InventoryFragment inventoryFragment = new InventoryFragment();

    private ArrayList<Fragment> fragments = new ArrayList<>(
        Arrays.asList(
            profileFragment,
            inventoryFragment
    ));

    public PlayerViewPagerAdapter(FragmentManager fm) {
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
