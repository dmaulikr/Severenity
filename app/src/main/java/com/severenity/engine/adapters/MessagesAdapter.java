package com.severenity.engine.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.severenity.App;
import com.severenity.R;
import com.severenity.entity.Message;
import com.severenity.utils.DateUtils;

import java.util.ArrayList;

/**
 * Created by Andriy on 4/29/2016.
 */
public class MessagesAdapter extends BaseAdapter {

    public static final int OUTER_MESSAGE_DELIVERED = 0;
    public static final int INNER_MESSAGE_DELIVERED = 1;

    private ArrayList<Message> mMessages;
    private Context mContext;
    private String mLocalUserID = AccessToken.getCurrentAccessToken().getUserId();

    public MessagesAdapter(Context ctx, ArrayList<Message> messages) {
        this.mContext = ctx;
        this.mMessages = messages;
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Message getItem(int position) {
        return mMessages.get(position);
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
        if (getItem(position).getUserID().equals(mLocalUserID))
            return INNER_MESSAGE_DELIVERED;
        else
            return OUTER_MESSAGE_DELIVERED;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message message = getItem(position);
        if (message == null)
            return null;

        View messageView = convertView;

        if (messageView == null) {
            // Inflate the layout according to the view type
            LayoutInflater inflater = (LayoutInflater) App.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int itemViewType = getItemViewType(position);

            if (itemViewType == OUTER_MESSAGE_DELIVERED) {
                messageView = inflater.inflate(R.layout.message_item_left_alligment, parent, false);
            }
            else {
                messageView = inflater.inflate(R.layout.message_item_right_alligment, parent, false);
            }
        }

        TextView tvTime = (TextView) messageView.findViewById(R.id.messageDate);
        if (DateUtils.isToday(message.getTimestamp()))
            tvTime.setText(DateUtils.getTimeFromTimestamp(message.getTimestamp()));
        else
            tvTime.setText(DateUtils.getDateFromTimestamp(message.getTimestamp()));

        TextView tvUserName = (TextView) messageView.findViewById(R.id.messageUsername);
        tvUserName.setText(message.getUserName());

        TextView tvMessage = (TextView) messageView.findViewById(R.id.messageMessage);
        tvMessage.setText(message.getMessage());

        ImageView ivAvatar = (ImageView) messageView.findViewById(R.id.messageAvatar);


        return messageView;
    }

    public void addItem(Message msg) {

        mMessages.add(msg);
    }
}