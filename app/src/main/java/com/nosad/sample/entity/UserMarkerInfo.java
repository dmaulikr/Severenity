package com.nosad.sample.entity;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Andriy on 6/10/2016.
 */
public class UserMarkerInfo {

    private Marker mMarker;
    private long   mLastUpdateTime;

    public UserMarkerInfo(Marker marker) {
        mMarker = marker;
        mLastUpdateTime = System.currentTimeMillis();
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
}
