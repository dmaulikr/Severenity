package com.severenity.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.severenity.R;
import com.severenity.engine.adapters.UsersSearchAdapter;

import java.util.List;

/**
 * Created by Andriy on 8/6/2016.
 */
public class CustomListView extends ListView implements AbsListView.OnScrollListener {

    private View mFooter;
    private boolean mIsLoading;
    private LoadDataListener mListener;
    private UsersSearchAdapter mAdapter;

    public CustomListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        configureListView();
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        configureListView();
    }

    public CustomListView(Context context) {
        super(context);
        configureListView();
    }

    private void configureListView() {
        this.setOnScrollListener(this);

        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFooter = (View) inflater.inflate(R.layout.loading_layout, null);
        this.addFooterView(mFooter);
    }

    public void setListener(LoadDataListener listener) {
        this.mListener = listener;
    }

    public void setAdapter(UsersSearchAdapter adapter) {
        super.setAdapter(adapter);
        this.mAdapter = adapter;
        this.removeFooterView(mFooter);
    }

    public void addNewData(List<String> data) {

        this.removeFooterView(mFooter);

        mAdapter.addAll(data);
        mAdapter.notifyDataSetChanged();
        mIsLoading = false;
    }

    public LoadDataListener setListener() {
        return mListener;
    }

    public static interface LoadDataListener {
        public void loadData() ;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (getAdapter() == null)
            return ;

        if (getAdapter().getCount() == 0)
            return ;

        int l = visibleItemCount + firstVisibleItem;
        if (l >= totalItemCount && !mIsLoading) {
            // It is time to add new data. We call the listener
            this.addFooterView(mFooter);
            mIsLoading = true;
            mListener.loadData();
        }
    }
}
