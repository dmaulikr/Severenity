package com.severenity.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.R;
import com.severenity.entity.skill.Skill;
import com.severenity.view.activities.MainActivity;
import com.severenity.view.custom.SkillRecycleViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends Fragment {
    private RecyclerView rvInventory;
    private ArrayList<Skill> skills;

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        createSkills();
        rvInventory = view.findViewById(R.id.rvSkills);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rvInventory.setLayoutManager(gridLayoutManager);
        rvInventory.setAdapter(new SkillRecycleViewAdapter((MainActivity) getActivity(), skills));
        return view;
    }

    private void createSkills() {
        skills = new ArrayList<>(Arrays.asList(
                new Skill(Skill.SkillType.CapturePlace, getResources().getString(R.string.skill_capture_place), 0, Skill.Rarity.Common)
        ));
    }

}
