package com.nosad.sample.engine.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.utils.common.Constants;
import com.nosad.sample.view.Dialogs.PlacesInfoDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andriy on 6/6/2016.
 */
public class UserInfoAdapter extends InfoAdapter {

    public UserInfoAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {

            LayoutInflater inflater = (LayoutInflater) App.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.userinfo_item_list, parent, false);
        }

        final InfoData userData = getItem(position);

        TextView placeName = (TextView)listItemView.findViewById(R.id.placeName);
        placeName.setText(userData.dataString);

        TextView placeID = (TextView)listItemView.findViewById(R.id.place_ID);
        placeID.setText(userData.dataID);

        final ImageView info = (ImageView)listItemView.findViewById(R.id.infoImage);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Constants.INTENT_FILTER_SHOW_PLACE_INFO_DIALOG);

                JSONObject obj = new JSONObject();
                try {
                    obj.put(Constants.OBJECT_TYPE_IDENTIFIER, Constants.TYPE_PLACE);
                    obj.put(Constants.PLACE_ID, userData.dataID);

                    intent.putExtra(Constants.OBJECT_INFO_AS_JSON, obj.toString());
                    intent.putExtra(PlacesInfoDialog.SHOW_RELOCATION_BUTTON, true);
                    App.getLocalBroadcastManager().sendBroadcast(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return listItemView;
    }
}
