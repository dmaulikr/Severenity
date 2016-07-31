package com.severenity.view.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.R;
import com.severenity.view.custom.ClansViewPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClansFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ClansViewPagerAdapter teamViewPagerAdapter;

    public ClansFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tlTabs);
        viewPager = (ViewPager) view.findViewById(R.id.vpPager);
        teamViewPagerAdapter = new ClansViewPagerAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(teamViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}

