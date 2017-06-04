package com.severenity.view.fragments.clans.pages;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.severenity.R;
import com.severenity.view.fragments.clans.ChatFragment;
import com.severenity.view.fragments.clans.FragmentInfo;
import com.severenity.view.fragments.clans.WorldFragment;

/**
 * Created by Andriy on 8/14/2016.
 */
public class WorldPage extends ClansPageBase {
    // indicate button's ID that current page has.
    private static final int BUTTON_ID = 1;
    protected static final int BUTTONS_ID_OFFSET = 20001;

    TextView mSwitchButtonTextView;

    // two fragments that this page holds
    FragmentInfo mWorldFragment = new FragmentInfo(new WorldFragment(), "worldClan", "World", true);
    FragmentInfo mChatFragment = new FragmentInfo(new ChatFragment(), "chatFragment", "Chat", false);

    public WorldPage() {
        super(R.layout.world_clans_container_fragment, R.id.worldFragmentsContent);

        mFragments = new ArrayMap<>(2);
        mFragments.put(BUTTONS_ID_OFFSET, mWorldFragment);
        mFragments.put(BUTTONS_ID_OFFSET + BUTTON_ID, mChatFragment);
    }

    public static WorldPage newInstance(String pageTitle) {
        WorldPage fragment = new WorldPage();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_PAGE_TITLE, pageTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreate(View view) {
        mPageTitle = getArguments().getString(ARGUMENT_PAGE_TITLE);

        View v = super.onCreate(view);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        mSwitchButtonTextView = new TextView(getContext());
        mSwitchButtonTextView.setLayoutParams(param);
        mSwitchButtonTextView.setId(BUTTONS_ID_OFFSET + BUTTON_ID);
        mSwitchButtonTextView.setText(mChatFragment.mFragmentButtonCaption);
        mSwitchButtonTextView.setGravity(Gravity.CENTER);
        mSwitchButtonTextView.setOnClickListener(this);
        ((LinearLayout)v.findViewById(R.id.buttonsLayout)).addView(mSwitchButtonTextView);

        return v;
    }

    @Override
    public void onFragmentShow(boolean show) {
        if (!show) {
            if (getCurrentFragment() != null &&
                    getCurrentFragment().mFragment == mChatFragment.mFragment) {
                FragmentTransaction t = getFragmentManager().beginTransaction();
                switchFragmentTo(t, mWorldFragment);
                mSwitchButtonTextView.setText(mChatFragment.mFragmentButtonCaption);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case BUTTONS_ID_OFFSET + BUTTON_ID:
                FragmentTransaction t = getFragmentManager().beginTransaction();

                ((TextView) view).setText(getCurrentFragment().mFragmentButtonCaption);
                if (getCurrentFragment().mFragmentName.equals(mWorldFragment.mFragmentName)) {
                    t.setCustomAnimations(R.anim.chat_slide_up, R.anim.content_slide_up);
                    switchFragmentTo(t, mChatFragment);

                } else if (getCurrentFragment().mFragmentName.equals(mChatFragment.mFragmentName)) {
                    t.setCustomAnimations(R.anim.content_slide_down, R.anim.chat_slide_down);
                    switchFragmentTo(t, mWorldFragment);
                }

                break;
        }
    }
}
