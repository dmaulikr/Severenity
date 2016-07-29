package com.severenity.engine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.severenity.App;
import com.severenity.R;

/**
 * Created by Andriy on 5/12/2016.
 */
public class MarkerInfoAdapter implements MapboxMap.InfoWindowAdapter {
    @Override
    public View getInfoWindow(Marker marker) {
        LayoutInflater inflater = (LayoutInflater) App.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.marker_info, null);

        if (customView != null) {
            TextView markerName = (TextView)customView.findViewById(R.id.markerName);
            if (markerName == null) {
                return null;
            }

            markerName.setText(marker.getTitle());

            TextView hiddenTextForID = (TextView)customView.findViewById(R.id.place_ID);
            if (hiddenTextForID == null) {
                return null;
            }

            hiddenTextForID.setText(marker.getSnippet());

            return customView;
        }
        return null;
    }
}
