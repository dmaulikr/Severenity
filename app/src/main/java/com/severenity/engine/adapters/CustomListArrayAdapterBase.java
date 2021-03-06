package com.severenity.engine.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Novosad on 9/2/2016.
 */
public abstract class CustomListArrayAdapterBase<T> extends ArrayAdapter<T> {
    protected List<T> mItemList = new ArrayList<>();
    protected Context mContext;

    public CustomListArrayAdapterBase(Context ctx, int resource) {
        super(ctx, resource);
        this.mContext = ctx;
    }

    @Override
    public int getCount() {
        return mItemList.size() ;
    }

    @Override
    public T getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).hashCode();
    }

    public abstract <T> void addList(List<T> teams);

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    public void clearData() {
        mItemList.clear();
    }
}
