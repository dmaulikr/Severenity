package com.severenity.view.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.R;
import com.severenity.view.custom.PlayerViewPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PlayerViewPagerAdapter playerViewPagerAdapter;

    public PlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tlTabs);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.title_profile));

        // TODO: Re-enable when restoring actions & inventory
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.title_inventory));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) view.findViewById(R.id.vpPager);
        playerViewPagerAdapter = new PlayerViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(playerViewPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }
}
