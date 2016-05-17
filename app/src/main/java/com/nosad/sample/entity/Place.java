package com.nosad.sample.entity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andriy on 5/15/2016.
 */
public class Place {

    private String mPlaceID;
    private String mPlaceName;
    private HashMap<Integer, String> mPlaceOwnerIDs;
    private LatLng mPlacePos;

    public Place(String placeID, String placeName, LatLng latlng) {

        this.mPlaceID = placeID;
        this.mPlaceName = placeName;
        this.mPlacePos = latlng;
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

    public String getPlaceID() { return this.mPlaceID; };
    public String getPlaceName() { return this.mPlaceName; };

    public void   setPlacePos(LatLng pos) { this.mPlacePos = pos; };
    public LatLng getPlacePos() { return this.mPlacePos; };
}
