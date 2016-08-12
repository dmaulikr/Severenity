package com.severenity.view.fragments.clans;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.NetworkResponse;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.adapters.UsersSearchAdapter;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.User;
import com.severenity.utils.Utils;
import com.severenity.view.custom.CustomListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andriy on 7/28/2016.
 */
public class ClansWorldFragment extends Fragment implements CustomListView.LoadDataListener {

    private final int ITEM_PER_REQUEST = 15;

    private CustomListView mUsersList;
    private int mOffset = 0;

    public ClansWorldFragment() {
        // Required empty public constructor
    }

    private RelativeLayout chatLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans_world, container, false);

        UsersSearchAdapter searchAdapter = new UsersSearchAdapter(getContext());

        mUsersList = (CustomListView)view.findViewById(R.id.usersList);
        mUsersList.setAdapter(searchAdapter);
        mUsersList.setListener(this);

        requestUsers();

        return view;
    }

    @Override
    public void loadData() {
        requestUsers();
    }

    private void requestUsers() {

        App.getUserManager().getUsersAsPage(mOffset, ITEM_PER_REQUEST, "profile.experience", new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {

                try {
                    JSONArray data = response.getJSONArray("docs");
                    mOffset += data.length();

                    List<User> result = new ArrayList<User>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonUser = data.getJSONObject(i);
                        User user = Utils.createLimitedUserFromJSON(jsonUser);
                        if (user != null) {
                            result.add(user);
                        }
                    }

                    mUsersList.addNewData(result);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorCallback(NetworkResponse response) {

            }
        });

    }
}
