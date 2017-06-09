package com.severenity.view.custom;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.severenity.App;
import com.severenity.view.fragments.InventoryFragment;
import com.severenity.view.fragments.ProfileFragment;

import java.util.ArrayList;

/**
 * Created by Novosad on 5/17/16.
 */
public class PlayerViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> fragments = new ArrayList<>();

    public PlayerViewPagerAdapter(FragmentManager fm) {
        super(fm);

        ProfileFragment profileFragment = ProfileFragment.newInstance(App.getUserManager().getCurrentUser().getId());
        InventoryFragment inventoryFragment = new InventoryFragment();

        fragments.add(profileFragment);
        fragments.add(inventoryFragment);
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
