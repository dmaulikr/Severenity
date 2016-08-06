package com.severenity.view.fragments;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.severenity.R;
import com.severenity.engine.adapters.UsersSearchAdapter;
import com.severenity.view.custom.CustomListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andriy on 7/28/2016.
 */
public class ClansWorldFragment extends Fragment implements CustomListView.LoadDataListener {

    private final int ITEM_PER_REQUEST = 50;

    private CustomListView mUsersList;
    int mult = 1;

    public ClansWorldFragment() {
        // Required empty public constructor
    }

    private RelativeLayout chatLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans_world, container, false);

        UsersSearchAdapter searchAdapter = new UsersSearchAdapter(getContext(), createItems(mult), R.layout.usersearch_item_list);

        mUsersList = (CustomListView)view.findViewById(R.id.usersList);
        mUsersList.setAdapter(searchAdapter);
        mUsersList.setListener(this);

        return view;
    }

    @Override
    public void loadData() {
        System.out.println("Load data");
        mult += 10;
        // We load more data here
        FakeNetLoader fl = new FakeNetLoader();
        fl.execute(new String[]{});
    }

    private List<String> createItems(int mult) {
        List<String> result = new ArrayList<String>();

        for (int i=0; i < ITEM_PER_REQUEST; i++) {
            result.add("Item " + (i * mult));
        }

        return result;
    }


    private class FakeNetLoader extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return createItems(mult);
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            mUsersList.addNewData(result);
        }
    }
}
