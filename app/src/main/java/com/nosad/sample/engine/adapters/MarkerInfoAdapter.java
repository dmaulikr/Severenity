package com.nosad.sample.engine.adapters;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.utils.Utils;
import com.nosad.sample.utils.common.Constants;

/**
 * Created by Andriy on 5/12/2016.
 */
public class MarkerInfoAdapter implements GoogleMap.InfoWindowAdapter {

    @Override
    public View getInfoContents(Marker marker) {

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

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}
