package com.severenity.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.severenity.R;

/**
 * Created by Andriy on 8/1/2016.
 */
public class ClansFrameContainerFragment extends Fragment {

    private ClansWorldFragment mWorldClansFragment = new ClansWorldFragment();
    private ChatFragment mChatFragment = new ChatFragment();

    Fragment mCurrentFragment;

    public ClansFrameContainerFragment() {
        // Required empty public constructor
    }

    private RelativeLayout chatLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.clans_frame_container, container, false);

        getFragmentManager().beginTransaction()
                .add (R.id.fragmentContent, mChatFragment, "chatFragment")
                .hide (mChatFragment)
                .add (R.id.fragmentContent, mWorldClansFragment , "worldClan")
                .commit();

        mCurrentFragment = mWorldClansFragment;

        TextView tvChat = (TextView)view.findViewById(R.id.chatShow);
        tvChat.setOnClickListener(new View.OnClickListener() {
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
}
