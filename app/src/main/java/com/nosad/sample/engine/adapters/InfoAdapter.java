package com.nosad.sample.engine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nosad.sample.App;
import com.nosad.sample.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Andriy on 5/18/2016.
 */

public class InfoAdapter extends BaseAdapter {

    // identifiers for the adapter
    public static final int PLACE_INFO = 0;
    public static final int USER_INFO  = 1;

    static public class InfoData {
        public String dataID;
        public String dataString;
    }

    private Context mContext;
    private ArrayList<InfoData> mData;
    private int mAdapterIdentifier;

    public InfoAdapter(Context ctx, int identifier) {
        mContext = ctx;
        mData = new ArrayList<>();
        mAdapterIdentifier = identifier;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public InfoData getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(InfoData data) {
        mData.add(data);
    }

    public void setData(ArrayList<InfoData> data) {mData = data; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {

            LayoutInflater inflater = (LayoutInflater) App.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            switch (mAdapterIdentifier) {
                case PLACE_INFO: {
                    listItemView = inflater.inflate(R.layout.placeinfo_list_item, parent, false); break;
                }

                case USER_INFO: {
                    listItemView = inflater.inflate(R.layout.userinfo_item_list, parent, false); break;
                }
            }

            if (listItemView == null) {
                return null;
            }
        }

        switch (mAdapterIdentifier) {
            case PLACE_INFO: {

                InfoData userData = getItem(position);

                ImageView userProfilePicture = (ImageView)listItemView.findViewById(R.id.userAvatar);
                Picasso.with(mContext).load("https://graph.facebook.com/" + userData.dataID + "/picture?type=normal").into(userProfilePicture);

                TextView userName = (TextView)listItemView.findViewById(R.id.ownerUserName);
                userName.setText(userData.dataString);

                break;
            }

            case USER_INFO: {
                InfoData userData = getItem(position);

                TextView placeName = (TextView)listItemView.findViewById(R.id.placeName);
                placeName.setText(userData.dataString);

                TextView placeID = (TextView)listItemView.findViewById(R.id.place_ID);
                placeID.setText(userData.dataID);

                break;
            }
        }

        return listItemView;
    }
}
