package com.severenity.engine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.severenity.App;
import com.severenity.R;
import com.severenity.utils.common.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andriy on 6/6/2016.
 */
public class PlaceInfoAdapter extends InfoAdapter {

    public static final String ITEM_INDEX = "Index";

    private boolean mObjectWithinActionView;
    private String mPlaceId;

    public PlaceInfoAdapter(Context ctx, boolean objectWithinActionView, String placeId) {
        super(ctx);

        mPlaceId = placeId;
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
                    try {
                        JSONObject data = new JSONObject();
                        data.put("userId", App.getUserManager().getCurrentUser().getId());
                        data.put("otherUserId", placeData.dataID);

                        App.getWebSocketManager().sendPlaceUpdateToServer(mPlaceId, Constants.UsersActions.REMOVE, data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        return listItemView;
    }
}
