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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Novosad on 4/29/2016.
 */
public class MessagesAdapter extends BaseAdapter {

    public static final int OUTER_MESSAGE_DELIVERED = 0;
    public static final int INNER_MESSAGE_DELIVERED = 1;

    private ArrayList<Message> mMessages;
    private String mLocalUserID = AccessToken.getCurrentAccessToken().getUserId();

    public MessagesAdapter(ArrayList<Message> messages) {
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
        if (getItem(position).getSenderId().equals(mLocalUserID)) {
            return INNER_MESSAGE_DELIVERED;
        } else {
            return OUTER_MESSAGE_DELIVERED;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = getItem(position);
        if (message == null) {
            return null;
        }

        View messageView = convertView;

        if (messageView == null) {
            // Inflate the layout according to the view type
            LayoutInflater inflater = (LayoutInflater) App.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int itemViewType = getItemViewType(position);

            if (itemViewType == OUTER_MESSAGE_DELIVERED) {
                messageView = inflater.inflate(R.layout.message_item_left_alligment, parent, false);
            } else {
                messageView = inflater.inflate(R.layout.message_item_right_alligment, parent, false);
            }
        }

        TextView tvTime = (TextView) messageView.findViewById(R.id.messageDate);
        if (DateUtils.isToday(message.getTimestamp())) {
            tvTime.setText(DateUtils.getTimeFromTimestamp(message.getTimestamp()));
        } else {
            tvTime.setText(DateUtils.getDateFromTimestamp(message.getTimestamp()));
        }

        TextView tvUserName = (TextView) messageView.findViewById(R.id.messageUsername);
        tvUserName.setText(message.getSenderName());

        TextView tvMessage = (TextView) messageView.findViewById(R.id.messageMessage);
        tvMessage.setText(message.getText());

        CircleImageView ivProfileImage = (CircleImageView) messageView.findViewById(R.id.messageAvatar);
        Picasso.with(messageView.getContext()).load("https://graph.facebook.com/" + message.getSenderId() + "/picture?type=normal").into(ivProfileImage);
        return messageView;
    }

    public void addItem(Message message) {
        if (!exists(message)) {
            mMessages.add(message);
        }
    }

    private boolean exists(Message message) {
        for (Message msg : mMessages) {
            if (msg.getMessageId() != null && msg.getMessageId().equalsIgnoreCase(message.getMessageId())) {
                return true;
            }
        }
        return false;
    }
}