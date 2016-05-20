package com.nosad.sample.view.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.adapters.PlaceInfoAdapter;
import com.nosad.sample.entity.Place;
import com.nosad.sample.entity.contracts.PlaceContract;
import com.nosad.sample.utils.FacebookUtils;
import com.nosad.sample.utils.common.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Andriy on 5/17/2016.
 */
public class PlacesInfoDialog extends DialogFragment {

    private PlaceInfoAdapter mPlaceInfoAdapter;

    public static PlacesInfoDialog newInstance(String placeID) {
        PlacesInfoDialog frag = new PlacesInfoDialog();
        Bundle b = new Bundle();
        b.putString(PlaceContract.DBPlaces.COLUMN_PLACE_ID, placeID);
        frag.setArguments(b);
        return frag;
    }

    private String getDialogCaption(int ownersCount, boolean currentUserOwner) {

        String caption = "";
        if (ownersCount == 0) {

            if (currentUserOwner) {
                caption = getResources().getString(R.string.owner_current_user);
            }
            else {
                caption = getResources().getString(R.string.owners_none);
            }

        } else {

            caption = (currentUserOwner ? (getResources().getString(R.string.owner_current_user) + " " + getResources().getString(R.string.owner_and)) : "")
                    + " " + String.format(getResources().getString(R.string.other_owners), ownersCount)
                    + " " + (ownersCount == 1 ? getResources().getString(R.string.owner_singular) : getResources().getString(R.string.owner_plural));
        }

        return caption + " " + getResources().getString(R.string.owner_own_this_place);
    }

    private boolean prepareViewForDisplaying(View view) {

        String placeID = getArguments().getString(PlaceContract.DBPlaces.COLUMN_PLACE_ID);

        Place place = App.getPlacesManager().findPlaceByID(placeID);
        if (place == null) {
            return false;
        }

        // TODO: AF: temporary add users as owners. Remove this afterwards
        place.addOwner("1245689");
        place.addOwner("1245691");
        place.addOwner("1245692");
        place.addOwner("1245697");
        place.addOwner("1245698");
        place.addOwner("1245700");
        place.addOwner("1245702");
        place.addOwner("1245703");
        place.addOwner("1245704");
        place.addOwner("1245707");
        place.addOwner("1245708");


        String currentUserID = App.getUserManager().getCurrentUser().getId();
        boolean currentUserOwnThisPlace = place.hasOwner(currentUserID);
        ArrayList<String> owners = place.getOwners(currentUserOwnThisPlace ? currentUserID : "");

        TextView captionView = (TextView)view.findViewById(R.id.placeInfoOwnersInfo);
        captionView.setText(getDialogCaption(owners.size(), currentUserOwnThisPlace));

        if (owners.size() == 0) {
            ListView ownersList = (ListView) view.findViewById(R.id.ownersList);
            ownersList.setVisibility(View.GONE);
        }

        mPlaceInfoAdapter = new PlaceInfoAdapter(getContext());
        ListView ownersList = (ListView)view.findViewById(R.id.ownersList);
        ownersList.setAdapter(mPlaceInfoAdapter);

        for (String owner: owners) {
            executePhotoAndNameRequest(owner);
        }

        return true;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_placeinfo, null);
        adb.setView(prepareViewForDisplaying(view) ? view : null);

        return adb.create();
    }

    private void executePhotoAndNameRequest(String id) {
        FacebookUtils.getFacebookUserById(id, "id,name,first_name,last_name", new FacebookUtils.Callback() {
            @Override
            public void onResponse(GraphResponse response) {
                if (response != null) {
                    try {
                        JSONObject data = response.getJSONObject();
                        if (data.has("name") && data.has("id")) {
                            PlaceInfoAdapter.InfoData infoData = new PlaceInfoAdapter.InfoData();
                            infoData.userID   = data.getString("id");
                            infoData.userName = data.getString("name");

                            mPlaceInfoAdapter.addItem(infoData);
                            mPlaceInfoAdapter.notifyDataSetChanged();
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
}
