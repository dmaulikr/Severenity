package com.severenity.view.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.severenity.App;
import com.severenity.sample.R;
import com.severenity.engine.adapters.MessagesAdapter;
import com.severenity.entity.Message;
import com.severenity.entity.User;
import com.severenity.utils.DateUtils;
import com.severenity.utils.common.Constants;

import java.util.ArrayList;

import static com.severenity.entity.contracts.MsgContract.DBMsg.COLUMN_MESSAGE;
import static com.severenity.entity.contracts.MsgContract.DBMsg.COLUMN_TIMESTAMP;
import static com.severenity.entity.contracts.MsgContract.DBMsg.COLUMN_USER_ID;
import static com.severenity.entity.contracts.MsgContract.DBMsg.COLUMN_USER_NAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment implements View.OnClickListener {

    private ListView mMessagesList;
    private Button mSendButton;
    private EditText mMessageEdit;
    private User mCurrentUser;
    private MessagesAdapter mMessageAdapter;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_messages, container, false);

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

                if (mCurrentUser == null)
                    mCurrentUser = App.getUserManager().getCurrentUser();

                if (mMessageEdit != null) {

                    Message msg = new Message();
                    msg.setMessage(mMessageEdit.getText().toString());
                    msg.setUserName(mCurrentUser.getName());
                    msg.setUserID(mCurrentUser.getId());
                    msg.setTimestamp(DateUtils.getTimestamp());
                    mMessageEdit.setText("");

                    if (mMessageAdapter == null) {

                        ArrayList<Message> messages = new ArrayList<>(1);
                        messages.add(msg);
                        setMessageAdapter(messages);
                    }
                    else {
                        mMessageAdapter.addItem(msg);
                    }

                    App.getMessageManager().sendMessage(msg);
                    mMessageAdapter.notifyDataSetChanged();
                    mMessagesList.setSelection(mMessageAdapter.getCount() - 1);
                }
                break;
            default:
                break;
        }
    }

    private void configureInnerObjects(View view) {

        mSendButton = (Button) view.findViewById(R.id.sendMessage);
        mSendButton.setOnClickListener(this);

        mMessageEdit = (EditText) view.findViewById(R.id.messageText);
        mMessageEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSendButton != null) {
                    if (s.length() > 0) {
                        mSendButton.setEnabled(true);
                        mSendButton.setTextColor(0xFF000000);
                    } else {
                        mSendButton.setEnabled(false);
                        mSendButton.setTextColor(0xFF8B8B8B);
                    }
                }
            }
        });

        mMessagesList = (ListView) view.findViewById(R.id.messagesList);

        ArrayList<Message> msg = App.getMessageManager().getMessages();
        if (msg != null && !msg.isEmpty()) {
            setMessageAdapter(msg);
        }
    }

    private void setMessageAdapter(ArrayList<Message> messages) {

        if (mMessageAdapter == null)
            mMessageAdapter = new MessagesAdapter(getContext(), messages);

        mMessagesList.setAdapter(mMessageAdapter);
        mMessagesList.setSelection(mMessageAdapter.getCount() - 1);
    }

    private BroadcastReceiver newMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extra = intent.getExtras();
            Message msg = new Message();
            msg.setMessage(extra.getString(COLUMN_MESSAGE));
            msg.setTimestamp(extra.getString(COLUMN_TIMESTAMP));
            msg.setUserName(extra.getString(COLUMN_USER_NAME));
            msg.setUserID(extra.getString(COLUMN_USER_ID));

            mMessageAdapter.addItem(msg);
            mMessageAdapter.notifyDataSetChanged();
            mMessagesList.setSelection(mMessageAdapter.getCount() - 1);
        }
    };
}
