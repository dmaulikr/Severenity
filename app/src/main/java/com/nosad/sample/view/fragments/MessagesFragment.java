package com.nosad.sample.view.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.engine.adapters.MessagesAdapter;
import com.nosad.sample.entity.Message;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {

    private ListView messagesList;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        ArrayList<Message> messages = App.getMessageManager().GetMessages();
        if (messages != null) {
            MessagesAdapter msgAdapter = new MessagesAdapter(getContext(), messages);
            messagesList = (ListView) view.findViewById(R.id.messagesList);
            messagesList.setAdapter(msgAdapter);
            messagesList.setSelection(msgAdapter.getCount() - 1);
        }

        return view;

    }

}
