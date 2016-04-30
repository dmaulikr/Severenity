package com.nosad.sample.engine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.entity.Message;
import com.nosad.sample.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Andriy on 4/29/2016.
 */
public class MessagesAdapter extends BaseAdapter {

    private ArrayList<Message> _messages;
    private Context _context;
    private String _localUserID;
    private Date   _currentDay;

    public MessagesAdapter(Context ctx, ArrayList<Message> messages, String localUserID) {
        this._context = ctx;
        this._messages = messages;
        this._localUserID = localUserID;
        this._currentDay = new Date();
    }

    @Override
    public int getCount() {
        return _messages.size();
    }

    @Override
    public Message getItem(int position) {
        return _messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (getItem(position).getUserID().equals(_localUserID)) ? 1 : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View messageView = convertView;

        if (messageView == null) {
            // Inflate the layout according to the view type
            LayoutInflater inflater = (LayoutInflater) App.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int itemViewType = getItemViewType(position);

            if (itemViewType == 0) {
                messageView = inflater.inflate(R.layout.message_item_left_alligment, parent, false);
            }
            else {
                messageView = inflater.inflate(R.layout.message_item_right_alligment, parent, false);
            }
        }

        Message message = getItem(position);
        if (message == null)
            return null;

        TextView tvTime = (TextView) messageView.findViewById(R.id.messageDate);
        if (DateUtils.isToday(message.getTimestamp()))
            tvTime.setText(DateUtils.getTimeFromTimestamp(message.getTimestamp()));
        else
            tvTime.setText(DateUtils.getDateFromTimestamp(message.getTimestamp()));

        TextView tvUserName = (TextView) messageView.findViewById(R.id.messageUsername);
        tvUserName.setText(message.getUserName());

        TextView tvMessage = (TextView) messageView.findViewById(R.id.messageMessage);
        tvMessage.setText(message.getMessage());

        return messageView;
    }
}
