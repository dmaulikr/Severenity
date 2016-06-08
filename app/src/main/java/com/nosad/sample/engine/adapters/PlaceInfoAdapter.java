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
import com.squareup.picasso.Picasso;

/**
 * Created by Andriy on 6/6/2016.
 */
public class PlaceInfoAdapter extends InfoAdapter {

    public static final String ITEM_INDEX = "Index";

    private boolean mObjectWithinActionView;

    public PlaceInfoAdapter(Context ctx, boolean objectWithinActionView) {
        super(ctx);

        mObjectWithinActionView = objectWithinActionView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {

            LayoutInflater inflater = (LayoutInflater) App.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.placeinfo_list_item, parent, false);
        }

        final InfoData placeData = getItem(position);

        ImageView userProfilePicture = (ImageView)listItemView.findViewById(R.id.userAvatar);
        Picasso.with(mContext).load("https://graph.facebook.com/" + placeData.dataID + "/picture?type=normal").into(userProfilePicture);

        TextView userName = (TextView)listItemView.findViewById(R.id.ownerUserName);
        userName.setText(placeData.dataString);

        if (mObjectWithinActionView) {
            ImageView delete = (ImageView)listItemView.findViewById(R.id.deleteIcon);
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Constants.INTENT_FILTER_DELETE_OWNER);
                    intent.putExtra(Constants.USER_ID, placeData.dataID);
                    intent.putExtra(ITEM_INDEX, position);
                    App.getLocalBroadcastManager().sendBroadcast(intent);
                }
            });
        }

        return listItemView;
    }
}
