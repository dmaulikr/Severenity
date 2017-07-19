package com.severenity.view.fragments.clans;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NetworkResponse;
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.adapters.UsersListAdapter;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.user.User;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;
import com.severenity.view.custom.CustomListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Displays all users of the game and handles appropriate interactions.
 *
 * Created by Novosad on 7/28/2016.
 */
public class WorldFragment extends Fragment implements CustomListView.LoadDataListener {
    private final static int ITEM_PER_REQUEST = 15;

    private CustomListView mUsersList;
    private int mOffset = 0;

    public WorldFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_clans_world, container, false);

        UsersListAdapter searchAdapter = new UsersListAdapter(getContext(), this);

        mUsersList = view.findViewById(R.id.usersList);
        mUsersList.setAdapter(searchAdapter);
        mUsersList.setListener(this);

        requestUsers();

        return view;
    }

    @Override
    public void loadData() {
        requestUsers();
    }

    /**
     * Requests and parses new users page.
     */
    private void requestUsers() {
        App.getUserManager().getUsersAsPage(mOffset, ITEM_PER_REQUEST, new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                try {
                    if ("success".equals(response.getString("result"))) {
                        JSONArray data = response.getJSONArray("data");
                        mOffset += data.length();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonUser = data.getJSONObject(i);
                            User user = Utils.createUserFromJSON(jsonUser);
                            App.getUserManager().addUser(user);
                        }

                        mUsersList.addNewData(App.getUserManager().getUsers());
                    } else {
                        Log.e(Constants.TAG, "Retrieve users error: " + response.getJSONObject("data").toString());
                    }
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
