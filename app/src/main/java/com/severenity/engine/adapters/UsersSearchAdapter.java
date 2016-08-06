package com.severenity.engine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.severenity.R;
import com.severenity.view.fragments.ClansWorldFragment;

import java.util.List;

/**
 * Created by Andriy on 8/4/2016.
 */

public class UsersSearchAdapter extends ArrayAdapter<String> {

    private List<String> mItemList;
    private Context mContext;
    private int mLayoutId;

    public UsersSearchAdapter(Context ctx, List<String> itemList, int layoutId) {
        super(ctx, layoutId, itemList);
        this.mItemList = itemList;
        this.mContext= ctx;
        this.mLayoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mItemList.size() ;
    }

    @Override
    public String getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;

        if (result == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            result = inflater.inflate(mLayoutId, parent, false);
        }

        // We should use class holder pattern
        TextView tv = (TextView) result.findViewById(R.id.txt1);
        tv.setText(mItemList.get(position));

        return result;
    }
}

