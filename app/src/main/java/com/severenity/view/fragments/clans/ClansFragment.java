package com.severenity.view.fragments.clans;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.R;
import com.severenity.engine.adapters.ClansViewPagerAdapter;
import com.severenity.view.fragments.clans.pages.ClansPageBase;
import com.severenity.view.fragments.clans.pages.TeamsPage;
import com.severenity.view.fragments.clans.pages.WorldPage;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * holds world and team fragments.
 */
public class ClansFragment extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ClansViewPagerAdapter mTeamViewPagerAdapter;

    public ClansFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans, container, false);

        mTabLayout = view.findViewById(R.id.tlTabs);
        mViewPager = view.findViewById(R.id.vpPager);

        ArrayList<ClansPageBase> list = new ArrayList<>(2);
        list.add(WorldPage.newInstance(getResources().getString(R.string.title_world), getActivity()));
        list.add(TeamsPage.newInstance(getResources().getString(R.string.title_team)));

        mTeamViewPagerAdapter = new ClansViewPagerAdapter(getChildFragmentManager(), list);

        mViewPager.setAdapter(mTeamViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        for (int i = 0; i < mTeamViewPagerAdapter.getCount(); i++) {
            ClansPageBase ntFragment = (ClansPageBase) mTeamViewPagerAdapter.getItem(i);
            ntFragment.onFragmentShow(!hidden);
        }
    }
}

