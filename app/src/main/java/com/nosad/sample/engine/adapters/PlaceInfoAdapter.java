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

public class PlaceInfoAdapter extends BaseAdapter {

    static public class InfoData {
        public String userID;
        public String userName;
    }

    private Context mContext;
    private ArrayList<InfoData> mData;

    public PlaceInfoAdapter(Context ctx) {
        mContext = ctx;
        mData = new ArrayList<>();
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View ownersView = convertView;
        if (ownersView == null) {

            LayoutInflater inflater = (LayoutInflater) App.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ownersView = inflater.inflate(R.layout.placeinfo_list_item, parent, false);
        }

        InfoData userData = getItem(position);

        ImageView userProfilePicture = (ImageView)ownersView.findViewById(R.id.userAvatar);
        Picasso.with(mContext).load("https://graph.facebook.com/" + userData.userID + "/picture?type=normal").into(userProfilePicture);

        TextView userName = (TextView)ownersView.findViewById(R.id.ownerUserName);
        userName.setText(userData.userName);

        return ownersView;
    }
}
