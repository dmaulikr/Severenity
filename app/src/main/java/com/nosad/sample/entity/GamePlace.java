package com.nosad.sample.entity;

import com.google.android.gms.maps.model.LatLng;
import com.nosad.sample.utils.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andriy on 5/15/2016.
 */
public class GamePlace {
    public enum PlaceType {
        Default,
        Money,
        ImplantRecovery,
        ImplantRepair,
        ImplantIncrease,
        EnergyIncrease,
        ImmunityIncrease
    }

    private String mPlaceID;
    private String mPlaceName;
    private HashMap<Integer, String> mPlaceOwnerIDs;
    private LatLng mPlacePos;
    private PlaceType mPlaceType; // TODO: Add dependency on the place type in all places.

    public GamePlace(String placeID, String placeName, LatLng latlng, PlaceType placeType) {
        mPlaceID = placeID;
        mPlaceName = placeName;
        mPlacePos = latlng;
        mPlaceType = placeType;
        mPlaceOwnerIDs = new HashMap<>();
    }

    public void addOwner(String owner) {

        this.mPlaceOwnerIDs.put(owner.hashCode(), owner);
    };

    public boolean hasOwner(String ownerID) {

        if (mPlaceOwnerIDs == null) {
            return false;
        }

        return mPlaceOwnerIDs.containsKey(ownerID.hashCode());
    };

    public ArrayList<String> getOwners(String exceptUserID) {

        if (mPlaceOwnerIDs.isEmpty()) {
            return null;
        }

        ArrayList<String> owners = new ArrayList<>(mPlaceOwnerIDs.size());

        for (String owner: mPlaceOwnerIDs.values()) {

            if (exceptUserID.isEmpty()) {
                owners.add(owner);
            }
            else {
                if (owner.hashCode() != exceptUserID.hashCode()) {
                    owners.add(owner);
                }
            }
        }

        return owners;
    }

    public String getPlaceID() { return this.mPlaceID; }
    public String getPlaceName() { return this.mPlaceName; }

    public LatLng getPlacePos() { return this.mPlacePos; }

    public PlaceType getPlaceType() {
        return mPlaceType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.mPlaceType = placeType;
    }

    public String getJSONPlaceInfo() {

        JSONObject obj = new JSONObject();
        try {
            obj.put(Constants.OBJECT_TYPE_IDENTIFIER, Constants.TYPE_PLACE);
            obj.put(Constants.PLACE_ID, mPlaceID);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

        return obj.toString();
    }
}
