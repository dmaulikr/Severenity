package com.severenity.view.fragments.clans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.severenity.R;
import com.severenity.view.fragments.NotifiableFragment;

/**
 * Created by Andriy on 8/1/2016.
 */
public class ClansWorldFramesContainerFragment extends Fragment implements NotifiableFragment {

    private ClansWorldFragment mWorldClansFragment = new ClansWorldFragment();
    private ChatFragment mChatFragment = new ChatFragment();

    Fragment mCurrentFragment;
    TextView mTvChat;

    public ClansWorldFramesContainerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.clans_frame_container_world, container, false);

        getFragmentManager().beginTransaction()
                .add (R.id.WorldFragmentContent, mChatFragment, "chatFragment")
                .hide (mChatFragment)
                .add (R.id.WorldFragmentContent, mWorldClansFragment , "worldClan")
                .commit();

        mCurrentFragment = mWorldClansFragment;

        mTvChat = (TextView)view.findViewById(R.id.chatShow);
        mTvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction t = getFragmentManager().beginTransaction();

                if (mCurrentFragment == mWorldClansFragment) {
                    t.setCustomAnimations(R.anim.chat_slide_up, R.anim.content_slide_up);
                    t.hide(mCurrentFragment);
                    t.show(mChatFragment);
                    mCurrentFragment = mChatFragment;
                    ((TextView)view).setText(getContext().getResources().getString(R.string.world_list));

                } else if (mCurrentFragment == mChatFragment){
                    t.setCustomAnimations(R.anim.content_slide_down, R.anim.chat_slide_down);
                    t.hide(mCurrentFragment);
                    t.show(mWorldClansFragment);
                    mCurrentFragment = mWorldClansFragment;
                    ((TextView)view).setText(getContext().getResources().getString(R.string.world_chat));
                }
                t.commit();
            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        onFragmentShow(isVisibleToUser);
    }

    @Override
    public void onFragmentShow(boolean show) {
        if (!show) {
            if (mCurrentFragment == mChatFragment) {
                FragmentTransaction t = getFragmentManager().beginTransaction();
                t.hide(mChatFragment);
                t.show(mWorldClansFragment);
                mCurrentFragment = mWorldClansFragment;
                mTvChat.setText(getContext().getResources().getString(R.string.world_chat));
                t.commit();
            }
        }
    }
}
