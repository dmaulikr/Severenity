package com.severenity.engine.adapters;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbRequest;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.severenity.R;
import com.severenity.entity.User;
import com.severenity.view.fragments.ClansWorldFragment;

import java.util.List;

/**
 * Created by Andriy on 8/4/2016.
 */

public class UsersSearchAdapter extends ArrayAdapter<User> {

    private List<User> mItemList;
    private Context mContext;

    public UsersSearchAdapter(Context ctx, List<User> itemList) {
        super(ctx, R.layout.usersearch_item_list);
        this.mItemList = itemList;
        this.mContext= ctx;
    }

    @Override
    public int getCount() {
        return mItemList.size() ;
    }

    @Override
    public User getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).hashCode();
    }

    public void addList(List<User> user) {
        mItemList.addAll(user);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;

        if (result == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = inflater.inflate(R.layout.usersearch_item_list, parent, false);
        }

        TextView number = (TextView) result.findViewById(R.id.recordNumber);
        number.setText(Integer.toString(position));

        TextView usrName = (TextView) result.findViewById(R.id.userName);
        usrName.setText(getItem(position).getName());

        TextView userExp = (TextView) result.findViewById(R.id.userExp);
        userExp.setText(Integer.toString(getItem(position).getExperience()));

        return result;
    }
}

