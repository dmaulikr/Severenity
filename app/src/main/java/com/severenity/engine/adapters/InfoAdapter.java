package com.severenity.engine.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Novosad on 5/18/2016.
 */

public abstract class InfoAdapter extends BaseAdapter {

    static public class InfoData {
        public String dataID;
        public String dataString;
    }

    Context mContext;
    private ArrayList<InfoData> mData;

    InfoAdapter(Context ctx) {
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
        for (InfoData item : mData) {
            if (item.dataID.equals(data.dataID)) {
                return;
            }
        }

        mData.add(data);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mData.remove(position);
    }

    public void removeItemByDataString(String dataString) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).dataID.equals(dataString)) {
                removeItem(i);
                break;
            }
        }
    }

    public void setData(ArrayList<InfoData> data) {
        mData = data;
    }

    @Override
    public abstract View getView(final int position, View convertView, ViewGroup parent);
}
