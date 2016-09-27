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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rvShopItemsList = (RecyclerView) view.findViewById(R.id.rvShopItems);
        rvShopItemsList.setLayoutManager(gridLayoutManager );
        ShopItemsAdapter adapter = new ShopItemsAdapter(createMockListData());
        rvShopItemsList.setAdapter(adapter);

        return view;
    }

    private List<ShopItem> createMockListData() {
        List<ShopItem> list = new ArrayList<>();
        list.add(new ShopItem("First item", R.drawable.shop_item, "Test description blah-blah-blah", "50 credits"));
        list.add(new ShopItem("First item", 0, "Test description blah-blah-blah", "70 credits"));
        list.add(new ShopItem("First item", 0, "Test description blah-blah-blah", "110 credits"));
        list.add(new ShopItem("First item", 0, "Test description blah-blah-blah", "90 credits"));
        list.add(new ShopItem("First item", 0, "Test description blah-blah-blah", "10 credits"));
        return list;
    }

    @Override
    public void onResume() {
        super.onResume();
//        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
    }
}
