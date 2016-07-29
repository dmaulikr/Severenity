package com.severenity.entity;

import com.mapbox.mapboxsdk.annotations.Marker;

/**
 * Created by Andriy on 6/10/2016.
 */
public class UserMarkerInfo {
    private User mUser;
    private Marker mMarker;
    private long   mLastUpdateTime;

    public UserMarkerInfo(Marker marker, User user) {
        mMarker = marker;
        mLastUpdateTime = System.currentTimeMillis();
        mUser = user;
    }

    public long getLastUpdate() {
        return mLastUpdateTime;
    }

    public void setUpdateTime(long updatedIn) {
        mLastUpdateTime = updatedIn;
    }

    public Marker getMarker() {
        return mMarker;
    }

    public void setMarker(Marker marker) {
        mMarker = marker;
    }

    public User getUser() {
        return mUser;
    }
}
