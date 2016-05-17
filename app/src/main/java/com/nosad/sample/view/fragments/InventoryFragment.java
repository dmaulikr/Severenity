package com.nosad.sample.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.nosad.sample.R;
import com.nosad.sample.entity.Chip;
import com.nosad.sample.view.custom.ChipRecycleViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends Fragment {
    private RecyclerView rvInventory;
    private ArrayList<Chip> chips = new ArrayList<>(Arrays.asList(
            new Chip(Chip.ChipType.Capture),
            new Chip(Chip.ChipType.Dispel),
            new Chip(Chip.ChipType.Shield),
            new Chip(Chip.ChipType.Attack)
    ));

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        rvInventory = (RecyclerView) view.findViewById(R.id.rvChips);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rvInventory.setLayoutManager(gridLayoutManager);
        rvInventory.setAdapter(new ChipRecycleViewAdapter(getActivity(), chips));

        return view;
    }

}
