package com.severenity.view.fragments.clans;

/**
 * Created by Novosad on 8/13/2016.
 */

import android.support.v4.app.Fragment;

/**
 * Class holds information about fragment.
 */
public class FragmentInfo {
    public Fragment mFragment;
    public String mFragmentName;
    public String mFragmentButtonCaption;
    public boolean mActiveFragment;

    public FragmentInfo(Fragment fragment, String fragmentName, String fragmentButtonName, boolean active) {
        mFragmentName = fragmentName;
        mFragment = fragment;
        mActiveFragment = active;
        mFragmentButtonCaption = fragmentButtonName;
    }
}
