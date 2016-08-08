package com.severenity.view.fragments.clans;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.R;
import com.severenity.view.custom.ClansViewPagerAdapter;
import com.severenity.view.fragments.NotifiableFragment;

/**
 * A simple {@link Fragment} subclass.
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

        mTabLayout = (TabLayout) view.findViewById(R.id.tlTabs);
        mViewPager = (ViewPager) view.findViewById(R.id.vpPager);
        mTeamViewPagerAdapter = new ClansViewPagerAdapter(getChildFragmentManager(), getContext());
        mViewPager.setAdapter(mTeamViewPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        for ( int i = 0; i < mTeamViewPagerAdapter.getCount(); i++) {
            NotifiableFragment ntFragment = (NotifiableFragment)mTeamViewPagerAdapter.getItem(i);
            ntFragment.onFragmentShow(hidden ? false : true);
        }
    }
}

