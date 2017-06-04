package com.severenity.view.fragments.clans;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.severenity.App;
import com.severenity.R;
import com.severenity.engine.adapters.MessagesAdapter;
import com.severenity.entity.Message;
import com.severenity.utils.DateUtils;
import com.severenity.utils.common.Constants;

import java.util.ArrayList;

import static com.severenity.entity.contracts.MsgContract.DBMsg.COLUMN_MESSAGE;
import static com.severenity.entity.contracts.MsgContract.DBMsg.COLUMN_TIMESTAMP;
import static com.severenity.entity.contracts.MsgContract.DBMsg.COLUMN_USER_ID;
import static com.severenity.entity.contracts.MsgContract.DBMsg.COLUMN_USER_NAME;

/**
 * Created by Andriy on 7/26/2016.
 */
public class ChatFragment extends Fragment implements View.OnClickListener {

    private ListView mMessagesList;
    private ImageView mSendButton;
    private EditText mMessageEdit;
    private MessagesAdapter mMessageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_chat, container, false);

        configureInnerObjects(mainView);

        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        Log.v(Constants.TAG, this.toString() + " onActivityCreated()");
        super.onActivityCreated(bundle);

        App.getLocalBroadcastManager().registerReceiver(
                newMessageReceiver,
                new IntentFilter(Constants.INTENT_FILTER_NEW_MESSAGE)
        );
    }

    @Override
    public void onDestroy() {
        Log.v(Constants.TAG, this.toString() + " onPause()");
        super.onDestroy();
        App.getLocalBroadcastManager().unregisterReceiver(newMessageReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendMessage:
                sendMessage();
                break;
            default:
                break;
        }
    }

    private void configureInnerObjects(View view) {

        mSendButton = (ImageView) view.findViewById(R.id.sendMessage);
        mSendButton.setColorFilter(0xFF000000, PorterDuff.Mode.MULTIPLY);
        mSendButton.setOnClickListener(this);

        mMessageEdit = (EditText) view.findViewById(R.id.messageText);
        mMessageEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && s.subSequence(s.length() - 1, s.length()).toString().equalsIgnoreCase("\n")) {
                    s.replace(s.length() - 1, s.length(), "");
                    sendMessage();
                    return;
                }

                mSendButton.setEnabled(s.length() > 0);
                mSendButton.setColorFilter(s.length() > 0 ? 0xFF5A007D : 0xFF000000, PorterDuff.Mode.MULTIPLY);
            }
        });

        mMessagesList = (ListView) view.findViewById(R.id.messagesList);

        ArrayList<Message> msg = App.getMessageManager().getMessages();
        if (msg != null && !msg.isEmpty()) {
            setMessageAdapter(msg);
        }

        Intent updateStatusLabel = new Intent(Constants.INTENT_FILTER_UPDATE_STATUS_LABEL);
        updateStatusLabel.putExtra("text", "Updating...");
        updateStatusLabel.putExtra("show", true);
        App.getLocalBroadcastManager().sendBroadcast(updateStatusLabel);
        App.getMessageManager().getMessagesFromServer();
    }

    /**
     * Creates message objects and triggers send to server.
     */
    private void sendMessage() {
        Message msg = new Message();
        msg.setMessage(mMessageEdit.getText().toString());
        msg.setUsername(App.getUserManager().getCurrentUser().getName());
        msg.setUserID(App.getUserManager().getCurrentUser().getId());
        msg.setTimestamp(DateUtils.getTimestamp());
        mMessageEdit.setText("");

        if (mMessageAdapter == null) {
            ArrayList<Message> messages = new ArrayList<>(1);
            messages.add(msg);
            setMessageAdapter(messages);
        } else {
            mMessageAdapter.addItem(msg);
        }

        App.getMessageManager().sendMessage(msg);
        mMessageAdapter.notifyDataSetChanged();
        mMessagesList.setSelection(mMessageAdapter.getCount() - 1);
    }

    /**
     * Sets adapter for the message list.
     *
     * @param messages - list of the messages to create adapter for.
     */
    private void setMessageAdapter(ArrayList<Message> messages) {
        if (mMessageAdapter == null) {
            mMessageAdapter = new MessagesAdapter(getContext(), messages);
        }

        mMessagesList.setAdapter(mMessageAdapter);

        if (mMessageAdapter.getCount() > 0) {
            mMessageAdapter.notifyDataSetChanged();
            mMessagesList.setSelection(mMessageAdapter.getCount() - 1);
        }
    }

    private BroadcastReceiver newMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extra = intent.getExtras();
            Message message = new Message();
            message.setMessage(extra.getString(COLUMN_MESSAGE));
            message.setTimestamp(extra.getString(COLUMN_TIMESTAMP));
            message.setUsername(extra.getString(COLUMN_USER_NAME));
            message.setUserID(extra.getString(COLUMN_USER_ID));

            if (mMessageAdapter != null) {
                mMessageAdapter.addItem(message);
                mMessageAdapter.notifyDataSetChanged();
                mMessagesList.setSelection(mMessageAdapter.getCount() - 1);
            } else {
                ArrayList<Message> messages = new ArrayList<>();
                messages.add(message);
                setMessageAdapter(messages);
            }
        }
    };
}