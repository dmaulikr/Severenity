package com.severenity.view.fragments.clans.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.severenity.view.fragments.clans.FragmentInfo;

import java.util.Map;

/**
 * Created by Andriy on 8/13/2016.
 */
public abstract class ClansPageBase extends Fragment implements View.OnClickListener {
    protected static final String ARGUMENT_PAGE_TITLE = "pageTitle";

    protected Map<Integer, FragmentInfo> mFragments;
    protected FragmentManager    mFragmentManager;
    protected String             mPageTitle;

    private   FragmentInfo       mCurrentFragment;
    private   int                mPageResource;
    private   int                mContentFrameLayout;

    // data that describes warning Fragment that needs to pop
    // up under all other content. Note that the page XML file
    // defined ny mPageResource needs to have this additional
    // FrameLayout added.
    protected int                mWarningContentLayoutID = 0;
    protected FragmentInfo       mWarningFragment = null;

    public ClansPageBase() {
    }

    public FragmentInfo getCurrentFragment() {
        return mCurrentFragment;
    }

    protected ClansPageBase(int pageRecourse, int contentFrameLayoutID) {
        mPageResource       = pageRecourse;
        mContentFrameLayout = contentFrameLayoutID;
    }

    public String getTitle() {
        if (mPageTitle == null || mPageTitle.isEmpty()) {
            mPageTitle = getArguments().getString(ARGUMENT_PAGE_TITLE);
        }

        return mPageTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(mPageResource, container, false);
        return onCreate(view);
    }

    public View onCreate(View view) {

        mFragmentManager = getFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        for (Map.Entry<Integer, FragmentInfo> entry : mFragments.entrySet()) {

            FragmentInfo info = entry.getValue();
            transaction.add(mContentFrameLayout, info.mFragment, info.mFragmentName);
            if (!info.mActiveFragment) {
                transaction.hide(info.mFragment);
            } else {
                mCurrentFragment = info;
            }
        }

        FrameLayout warningFragment = (FrameLayout)view.findViewById(mWarningContentLayoutID);
        if (warningFragment == null) {
            transaction.commit();
            return view;
        }

        if (mWarningFragment != null && mWarningContentLayoutID != 0) {
            transaction.add(mWarningContentLayoutID, mWarningFragment.mFragment, mWarningFragment.mFragmentName)
                    .addToBackStack(mWarningFragment.mFragmentName)
                    .show(mWarningFragment.mFragment);
        } else {
            warningFragment.setVisibility(View.GONE);
        }

        transaction.commit();

        return view;
    }

    // Gets called when fragment is going to be shown/hidden
    // using this method delivered pages will define which
    // fragment should be shown on their visibility
    public abstract void onFragmentShow(boolean show);

    /**
     * Switches fragments from mCurrentFragment to newFragment
     * @param trans         - fragment transaction object that might already hold some info. e.g. animation
     * @param newFragment   - fragment which needs to be shown
     */
    public void switchFragmentTo(FragmentTransaction trans, FragmentInfo newFragment) {

        trans.hide(mCurrentFragment.mFragment);
        trans.show(newFragment.mFragment);
        trans.commit();
        mCurrentFragment = newFragment;
    }
}
