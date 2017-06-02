package com.severenity.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.severenity.R;
import com.severenity.engine.adapters.ShopItemsAdapter;
import com.severenity.entity.ShopItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShopFragment extends Fragment {
    private RecyclerView rvShopItemsList;

    public ShopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvShopItemsList = (RecyclerView) view.findViewById(R.id.rvShopItems);
        rvShopItemsList.setLayoutManager(gridLayoutManager);
        ShopItemsAdapter adapter = new ShopItemsAdapter(createMockListData());
        rvShopItemsList.setAdapter(adapter);

        return view;
    }

    private List<ShopItem> createMockListData() {
        List<ShopItem> list = new ArrayList<>();
        list.add(new ShopItem("Credits", R.drawable.shop_item_credits, "100 credits for in-game activities.", 0, 1));
        list.add(new ShopItem("Quest tip", R.drawable.shop_item_tip, "A small tip used during the quest.", 50, 0.5));
        list.add(new ShopItem("Quest Pass", R.mipmap.shop_item_ticket, "Ticket for participation in the quest.", 0, 10));
        return list;
    }
}