package com.nosad.sample.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.adapters.MessagesAdapter;
import com.nosad.sample.entity.Message;
import com.nosad.sample.entity.User;
import com.nosad.sample.utils.DateUtils;
import com.nosad.sample.utils.common.Constants;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment implements View.OnClickListener {

    private ListView _messagesList;
    private Button   _sendButton;
    private EditText _messageEdit;
    private User     _currentUser;
    ArrayList<Message> _messages;
    MessagesAdapter    _messageAdapter;
    View               _mainView;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _mainView = inflater.inflate(R.layout.fragment_messages, container, false);

/*        Message msg = new Message();
        for (int i = 0; i < 100; i++)
        {
            msg.setMessage("Hello " + i);
            msg.setTimestamp("H15654ello " + i);
            msg.setUserName("Name " + i);
            msg.setUserID("ID " + i);
            App.getMessageManager().AddMessage(msg);
        }*/

        configureInnerObjects();
        loadAndDisplayMessages();

        return _mainView;
    }

    @Override
    public void onClick(View v) {

        if (_messageEdit != null && _currentUser != null) {

            Message msg = new Message();
            msg.setMessage(_messageEdit.getText().toString());
            msg.setUserName(_currentUser.getName());
            msg.setUserID(_currentUser.getId());
            msg.setTimestamp(DateUtils.getTimestamp());

            if (_messages == null) {

                _messages = new ArrayList<Message>();
                _messages.add(msg);
                configureAndSetMessageAdapter(_messages);
            }
            else {

                _messages.add(msg);
            }

            App.getMessageManager().AddMessage(msg);
            _messageAdapter.notifyDataSetChanged();
            _messageEdit.setText("");
        }
    }

    private void loadAndDisplayMessages() {

        _messages = App.getMessageManager().GetMessages();
        if (_messages != null) {

            configureAndSetMessageAdapter(_messages);
        }
    };

    private void configureInnerObjects() {

        _sendButton = (Button)_mainView.findViewById(R.id.sendMessage);
        if (_sendButton != null)
            _sendButton.setOnClickListener(this);

        _messageEdit = (EditText)_mainView.findViewById(R.id.messageText);
        _currentUser = App.getUserManager().getCurrentUser();
    }

    private boolean configureAndSetMessageAdapter(ArrayList<Message> messages) {

        if (_currentUser == null) {
            Log.e(Constants.TAG, "MessageFragment: no logged-in user.");
            return false;
        }

        if (_messageAdapter == null)
            _messageAdapter = new MessagesAdapter(getContext(), messages, _currentUser.getId());

        _messagesList = (ListView) _mainView.findViewById(R.id.messagesList);
        if (_messagesList == null) {

            Log.e(Constants.TAG, "MessageFragment: no message list found.");
            return false;
        }

        _messagesList.setAdapter(_messageAdapter);
        _messagesList.setSelection(_messageAdapter.getCount() - 1);
        return true;
    }
}
