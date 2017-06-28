package com.severenity.view.dialogs;

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
import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.adapters.InfoAdapter;
import com.severenity.engine.adapters.PlaceInfoAdapter;
import com.severenity.entity.GamePlace;
import com.severenity.entity.User;
import com.severenity.utils.FacebookUtils;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Novosad on 5/17/2016.
 */
public class PlacesInfoDialog extends DialogFragment {

    public interface OnRelocateMapListener {
         void OnRelocate(String placeID);
    }

    public static final String SHOW_RELOCATION_BUTTON = "showRelocationButton";
    private static final String ARGUMENTS_PLACE_ID = "placeId";

    private PlaceInfoAdapter mInfoAdapter;
    private OnRelocateMapListener mListener;
    private String mPlaceID;
    private TextView mCaptionView;

    public static PlacesInfoDialog newInstance(String placeID, boolean showRelocationButton) {
        PlacesInfoDialog frag = new PlacesInfoDialog();
        Bundle b = new Bundle();
        b.putString(ARGUMENTS_PLACE_ID, placeID);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnRelocateMapListener) context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnRelocateMapListener");
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
        mPlaceID = getArguments().getString(ARGUMENTS_PLACE_ID);

        GamePlace place = App.getPlacesManager().findPlaceById(mPlaceID);
        if (place == null) {
            return false;
        }

        User currentUser = App.getUserManager().getCurrentUser();
        if (currentUser == null) {
            Log.e(Constants.TAG, "was not able to get current user.");
            return false;
        }

        ArrayList<String> owners = place.getOwners();

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

        mInfoAdapter = new PlaceInfoAdapter(getContext(), isPlaceWithinUsersActionView, mPlaceID);
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
            String ownerId = extra.getString(Constants.USER_ID);
            User currentUser = App.getUserManager().getCurrentUser();

            mInfoAdapter.removeItemByDataString(ownerId);
            mInfoAdapter.notifyDataSetChanged();

            App.getPlacesManager().deleteOwnership(mPlaceID, ownerId);
            if (currentUser != null && ownerId != null) {
                if (ownerId.equals(currentUser.getId())){
                    App.getLocationManager().markPlaceMarkerAsCapturedUncaptured(mPlaceID);
                }

                if (mCaptionView != null) {
                    mCaptionView.setText(getDialogCaption(mInfoAdapter.getCount()));
                }
            }
        }
    };
}
