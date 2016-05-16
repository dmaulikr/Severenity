package com.nosad.sample.entity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Andriy on 5/15/2016.
 */
public class Place {

    private String mPlaceID;
    private String mPlaceName;
    private ArrayList<String> mPlaceOwnerIDs;
    private LatLng mPlacePos;

    public Place(String placeID, String placeName, LatLng latlng) {

        this.mPlaceID = placeID;
        this.mPlaceName = placeName;
        this.mPlacePos = latlng;
        mPlaceOwnerIDs = new ArrayList<>();
    }

    public void    addOwner(String owner) { this.mPlaceOwnerIDs.add(owner); };
    //public boolean hasOwner(Srring ownerID) { return this.mPlaceOwnerID; };

    public String getPlaceID() { return this.mPlaceID; };
    public String getPlaceName() { return this.mPlaceName; };

    public void   setPlacePos(LatLng pos) { this.mPlacePos = pos; };
    public LatLng getPlacePos() { return this.mPlacePos; };
}
