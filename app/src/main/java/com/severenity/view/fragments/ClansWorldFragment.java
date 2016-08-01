package com.severenity.view.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.severenity.R;

/**
 * Created by Andriy on 7/28/2016.
 */
public class ClansWorldFragment extends Fragment {

    public ClansWorldFragment() {
        // Required empty public constructor
    }

    private RelativeLayout chatLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans_world, container, false);

        return view;
    }
}
