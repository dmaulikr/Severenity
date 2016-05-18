package com.nosad.sample.engine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.nosad.sample.App;
import com.nosad.sample.R;

import java.util.ArrayList;

/**
 * Created by Andriy on 5/18/2016.
 */
public class PlaceInfoAdapter extends BaseAdapter {

    Context mContext;
    private ArrayList<String> mData;
    ProfilePictureView mUserProfilePicture;

    public PlaceInfoAdapter(Context ctx, ArrayList<String> data) {
        mContext = ctx;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View ownersView = convertView;
        if (ownersView == null) {

            LayoutInflater inflater = (LayoutInflater) App.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ownersView = inflater.inflate(R.layout.placeinfo_list_item, parent, false);
        }

        String userID = getItem(position);

        mUserProfilePicture = (ProfilePictureView)ownersView.findViewById(R.id.mapUserAvatar);
        mUserProfilePicture.setProfileId(userID);

        TextView userName = (TextView)ownersView.findViewById(R.id.ownerUserName);
        userName.setText("User name");

        return ownersView;
    }
}
