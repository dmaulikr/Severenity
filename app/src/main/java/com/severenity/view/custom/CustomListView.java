package com.severenity.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.severenity.R;
import com.severenity.engine.adapters.CustomListArrayAdapterBase;

import java.util.List;

/**
 * Created by Novosad on 8/6/2016.
 */
public class CustomListView extends ListView implements AbsListView.OnScrollListener {

    private View mFooter;
    private LoadDataListener mListener;
    private CustomListArrayAdapterBase mAdapter;
    // When list is used not for infinite info displaying
    // we do not need to show footer.
    private boolean mShowSpinner = true;

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
        mFooter = inflater.inflate(R.layout.loading_layout, null);

        if (mShowSpinner) {
            this.addFooterView(mFooter);
        } else {
            this.removeFooterView(mFooter);
        }
    }

    public void setListener(LoadDataListener listener) {
        this.mListener = listener;
    }

    public void setAdapter(CustomListArrayAdapterBase adapter) {
        super.setAdapter(adapter);
        this.mAdapter = adapter;
        this.removeFooterView(mFooter);
    }

    public CustomListArrayAdapterBase getAdapter() {
        return this.mAdapter;
    }

    /**
     * method used to add additional data to be displayed in the list
     *
     * @param data - data to be added
     */
    public void addNewData(List data) {
        clearData();
        removeFooterView(mFooter);

        mAdapter.addList(data);
        mAdapter.notifyDataSetChanged();
    }

    public void clearData() {
        mAdapter.clearData();
        mAdapter.notifyDataSetChanged();
    }

    public interface LoadDataListener {
        void loadData();
    }

    public void showLoadSpinner(boolean show) {
        mShowSpinner = show;
        if (mShowSpinner) {
            this.addFooterView(mFooter);
        } else {
            this.removeFooterView(mFooter);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if (i == SCROLL_STATE_IDLE ) {
            // is the position is the last item within the adapter
            // request more items.
            int count = getAdapter().getCount();
            int pos = getLastVisiblePosition();
            if (count - 1 == pos) {
                if (mShowSpinner) {
                    this.addFooterView(mFooter);
                }

                if (mListener != null) {
                    mListener.loadData();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
    }
}
