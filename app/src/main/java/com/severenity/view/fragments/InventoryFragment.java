package com.severenity.view.fragments;


import android.os.Bundle;
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
    private RecyclerView rvInventory;
    private ArrayList<Chip> chips = new ArrayList<>(Arrays.asList(
            new Chip(Chip.ChipType.CapturePlace, "Captures place to collect data that improves your implant.", 0, Chip.Rarity.Common),
            new Chip(Chip.ChipType.CapturePlayer, "Captures player to steal data that improves your implant.", 0, Chip.Rarity.Rare),
            new Chip(Chip.ChipType.Remove, "Removes player who has captured from captured object.", 1, Chip.Rarity.Uncommon),
            new Chip(Chip.ChipType.Defend, "Defends your implant from next incoming attacking signals.", 0, Chip.Rarity.Common),
            new Chip(Chip.ChipType.Attack, "Deals damage to the selected implant.", 2, Chip.Rarity.Common),
            new Chip(Chip.ChipType.Invisibility, "Puts invisibility over the object so it disappears from the map.", 0, Chip.Rarity.Rare)
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
        rvInventory.setAdapter(new ChipRecycleViewAdapter((MainActivity) getActivity(), chips));

        return view;
    }

}
