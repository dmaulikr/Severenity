package com.severenity.view.fragments.clans.pages;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.severenity.R;
import com.severenity.view.fragments.clans.ChatFragment;
import com.severenity.view.fragments.clans.ClansWorldFragment;
import com.severenity.view.fragments.clans.FragmentInfo;

import java.util.ArrayList;

/**
 * Created by Andriy on 8/14/2016.
 */
public class WorldPage extends ClansPageBase {

    private Context mContext;
    // indicate button's ID that current page has.
    private final int BUTTON_ID = 1;

    //
    TextView mSwitchButtonTextView;

    // two fragments that this page holds
    FragmentInfo mWorldFragment = new FragmentInfo(new ClansWorldFragment(), "worldClan", "World", true);
    FragmentInfo mChatFragment = new FragmentInfo(new ChatFragment(), "chatFragment", "Chat", false);

    public WorldPage(Context context) {
        super(  R.layout.world_clans_container_fragment,
                R.id.worldFragmentsContent);

        mContext   = context;
        mPageTitle = mContext.getResources().getString(R.string.title_world);
        mFragments = new ArrayList<>(2);
        mFragments.add(mWorldFragment);
        mFragments.add(mChatFragment);
    }

    @Override
    public View onCreate(View view) {
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
                if (getCurrentFragment().mFragmentName.equals("worldClan")) {
                    t.setCustomAnimations(R.anim.chat_slide_up, R.anim.content_slide_up);
                    switchFragmentTo(t, mChatFragment);

                } else if (getCurrentFragment().mFragmentName.equals("chatFragment")) {
                    t.setCustomAnimations(R.anim.content_slide_down, R.anim.chat_slide_down);
                    switchFragmentTo(t, mWorldFragment);
                }

                break;
        }
    }
}
