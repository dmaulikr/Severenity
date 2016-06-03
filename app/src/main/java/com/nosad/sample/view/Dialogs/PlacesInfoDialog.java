package com.nosad.sample.view.Dialogs;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.GraphResponse;
import com.google.android.gms.maps.model.LatLng;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.adapters.InfoAdapter;
import com.nosad.sample.entity.GamePlace;
import com.nosad.sample.entity.User;
import com.nosad.sample.entity.contracts.PlaceContract;
import com.nosad.sample.utils.FacebookUtils;
import com.nosad.sample.utils.Utils;
import com.nosad.sample.utils.common.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Andriy on 5/17/2016.
 */
public class PlacesInfoDialog extends DialogFragment {

    public interface OnRelocateMapListener {
         void OnRelocate(String placeID);
    }

    public static final String SHOW_RELOCATION_BUTTON = "showRelocationButton";

    private InfoAdapter mInfoAdapter;
    private OnRelocateMapListener mListener;
    private String mPlaceID;
    private TextView mCaptionView;

    public static PlacesInfoDialog newInstance(String placeID, boolean showRelocationButton) {
        PlacesInfoDialog frag = new PlacesInfoDialog();
        Bundle b = new Bundle();
        b.putString(PlaceContract.DBPlaces.COLUMN_PLACE_ID, placeID);
        b.putBoolean(SHOW_RELOCATION_BUTTON, showRelocationButton);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onResume() {
        super.onResume();
        App.getLocalBroadcastManager().registerReceiver(onDeleteOwner, new IntentFilter(Constants.INTENT_FILTER_DELETE_OWNER));
    }

    @Override
    public void onStop() {
        super.onStop();
        App.getLocalBroadcastManager().unregisterReceiver(onDeleteOwner);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnRelocateMapListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnRelocateMapListener");
        }
    }

    private String getDialogCaption(int ownersCount) {

        if (ownersCount == 0) {

            return getResources().getString(R.string.owners_none);
        } else {

            return  String.format(getResources().getString(R.string.other_owners), ownersCount)
                    + " " + (ownersCount == 1 ? getResources().getString(R.string.owner_singular) : getResources().getString(R.string.owner_plural))
                    + " " + getResources().getString(R.string.owner_own_this_place);
        }
    }

    private boolean prepareViewForDisplaying(View view) {

        mPlaceID = getArguments().getString(PlaceContract.DBPlaces.COLUMN_PLACE_ID);

        GamePlace place = App.getPlacesManager().findPlaceByID(mPlaceID);
        if (place == null) {
            return false;
        }

        User currentUser = App.getUserManager().getCurrentUser();
        if (currentUser == null) {
            Log.e(Constants.TAG, "was not able to get current user.");
            return false;
        }

        ArrayList<String> owners = place.getOwners("");

        int ownersCount = (owners != null) ? owners.size() : 0;

        mCaptionView = (TextView)view.findViewById(R.id.placeInfoOwnersInfo);
        mCaptionView.setText(getDialogCaption(ownersCount));

        if (ownersCount == 0) {
            ListView ownersList = (ListView) view.findViewById(R.id.ownersList);
            ownersList.setVisibility(View.GONE);
        }
        else {
            for (String owner: owners) {
                executePhotoAndNameRequest(owner);
            }
        }

        LatLng currentPos = Utils.latLngFromLocation(App.getLocationManager().getCurrentLocation());
        boolean isPlaceWithinUsersActionView = (Utils.distanceBetweenLocations(currentPos, place.getPlacePos()) <= currentUser.getActionRadius());

        mInfoAdapter = new InfoAdapter(getContext(), InfoAdapter.PLACE_INFO, isPlaceWithinUsersActionView);
        ListView ownersList = (ListView)view.findViewById(R.id.ownersList);
        ownersList.setAdapter(mInfoAdapter);

        boolean showRelocationButton = getArguments().getBoolean(SHOW_RELOCATION_BUTTON, false);
        Button gotobutton = (Button)view.findViewById(R.id.showLocation);
        if (showRelocationButton) {

            gotobutton.setVisibility(View.VISIBLE);
            gotobutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.OnRelocate(mPlaceID);
                }
            });
        }
        else {
            gotobutton.setVisibility(View.GONE);
        }

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_placeinfo, null);
        prepareViewForDisplaying(view);

        return view;
    }

    private void executePhotoAndNameRequest(String id) {
        FacebookUtils.getFacebookUserById(id, "id,name,first_name,last_name", new FacebookUtils.Callback() {
            @Override
            public void onResponse(GraphResponse response) {
                if (response != null) {
                    try {
                        JSONObject data = response.getJSONObject();
                        if (data.has("name") && data.has("id")) {
                            InfoAdapter.InfoData infoData = new InfoAdapter.InfoData();
                            infoData.dataID = data.getString("id");
                            infoData.dataString = data.getString("name");

                            mInfoAdapter.addItem(infoData);
                            mInfoAdapter.notifyDataSetChanged();
                        } else {
                            Log.v(Constants.TAG, "No name and id data passed for user");
                        }
                    } catch (Exception e) {
                        Log.v(Constants.TAG, "No name and id data passed for user with ID: ");
                    }
                }
            }
        });
    }

    private BroadcastReceiver onDeleteOwner = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getExtras();
            int itemIndex = extra.getInt(InfoAdapter.INDEX, -1);
            String ownerID = extra.getString(Constants.USER_ID);

            if (itemIndex != -1) {
                mInfoAdapter.removeItem(itemIndex);
                mInfoAdapter.notifyDataSetChanged();
                App.getPlacesManager().deleteOwnership(mPlaceID, ownerID);

                if (mCaptionView != null) {
                    mCaptionView.setText(getDialogCaption(mInfoAdapter.getCount()));
                }
            }
        }
    };
}
