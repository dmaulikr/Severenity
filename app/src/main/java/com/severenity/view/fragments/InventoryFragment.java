package com.severenity.view.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.R;
import com.severenity.entity.chip.Chip;
import com.severenity.view.activities.MainActivity;
import com.severenity.view.custom.ChipRecycleViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends Fragment {
    private Context context;
    private RecyclerView rvInventory;
    private ArrayList<Chip> chips;

    public InventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        createChips();
        rvInventory = (RecyclerView) view.findViewById(R.id.rvChips);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rvInventory.setLayoutManager(gridLayoutManager);
        rvInventory.setAdapter(new ChipRecycleViewAdapter((MainActivity) getActivity(), chips));
        return view;
    }

    private void createChips() {
        chips = new ArrayList<>(Arrays.asList(
                new Chip(Chip.ChipType.CapturePlace, getResources().getString(R.string.chip_capture_place), 0, Chip.Rarity.Common),
                new Chip(Chip.ChipType.CapturePlayer, getResources().getString(R.string.chip_capture_player), 0, Chip.Rarity.Rare),
                new Chip(Chip.ChipType.Remove, getResources().getString(R.string.chip_remove), 1, Chip.Rarity.Uncommon),
                new Chip(Chip.ChipType.Defend, getResources().getString(R.string.chip_defend), 0, Chip.Rarity.Common),
                new Chip(Chip.ChipType.Attack, getResources().getString(R.string.chip_attack), 2, Chip.Rarity.Common),
                new Chip(Chip.ChipType.Invisibility, getResources().getString(R.string.chip_invisibility), 0, Chip.Rarity.Rare)
        ));
    }




}
