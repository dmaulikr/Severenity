package com.severenity.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.severenity.App;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.Message;
import com.severenity.utils.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.severenity.entity.contracts.MsgContract.DBMsg.COLUMN_MESSAGE;
import static com.severenity.entity.contracts.MsgContract.DBMsg.COLUMN_TIMESTAMP;
import static com.severenity.entity.contracts.MsgContract.DBMsg.COLUMN_USER_ID;
import static com.severenity.entity.contracts.MsgContract.DBMsg.COLUMN_USER_NAME;
import static com.severenity.entity.contracts.MsgContract.DBMsg.TABLE_MESSAGE;

/**
 * Created by Andriy on 4/27/2016.
 */
public class MessageManager extends DataManager {

    public MessageManager(Context context) {
        super(context);
    }

    private boolean addMessage(Message message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID,   message.getUserID());
        values.put(COLUMN_MESSAGE,   message.getMessage());
        values.put(COLUMN_TIMESTAMP, message.getTimestamp());
        values.put(COLUMN_USER_NAME, message.getUsername());

        long success = db.insert(TABLE_MESSAGE, "NULL", values);
        db.close();

        return success != -1;
    }

    /**
     * Retrieves all messages the local database and tries to update with new ones from the server.
     *
     * @return list of messages from DB.
     */
    public ArrayList<Message> getMessages() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;
        try {
            cursor = db.query(
                    TABLE_MESSAGE,
                    new String[]{COLUMN_USER_ID, COLUMN_MESSAGE, COLUMN_TIMESTAMP, COLUMN_USER_NAME},
                    null,
                    null,
                    null, null, null, null
            );
        } catch (SQLException e) {
            return null;
        }

        if (cursor.getCount() == 0) return null;

        ArrayList<Message> messagesList = new ArrayList<>(cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setUserID(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                message.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                message.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                message.setMessage(cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE)));

                messagesList.add(message);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return messagesList;
    }

    /**
     * Retrieves messages from server, updates local database with retrieved ones and notifies
     * UI to update the list.
     */
    public void getMessagesFromServer() {
        App.getRestManager().createRequest(Constants.REST_API_MESSAGES, Request.Method.GET, null, new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                try {
                    if (!"success".equalsIgnoreCase(response.getString("result"))) {
                        Log.e(Constants.TAG, response.toString());
                        return;
                    }

                    JSONArray messageObjects = response.getJSONArray("data");
                    for (int i = 0; i < messageObjects.length(); i++) {
                        JSONObject messageObject = messageObjects.getJSONObject(i);
                        Message message = new Message();
                        message.setMessage(messageObject.getString("text"));
                        if (messageObject.optString("senderName").isEmpty()) {
                            Log.e(Constants.TAG, message.getMessage());
                        }
                        message.setUsername(messageObject.getString("senderName"));
                        message.setUserID("senderId");
                        message.setMessageId(messageObject.getString("messageId"));
                        message.setTimestamp(messageObject.getString("timestamp"));
                        onMessageRetrieved(message);
                    }

                    Intent updateStatusLabel = new Intent(Constants.INTENT_FILTER_UPDATE_STATUS_LABEL);
                    updateStatusLabel.putExtra("text", "Finishing...");
                    updateStatusLabel.putExtra("show", false);
                    App.getLocalBroadcastManager().sendBroadcast(updateStatusLabel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorCallback(NetworkResponse response) {
                if (response != null) {
                    Log.e(Constants.TAG, response.toString());
                }
            }
        });
    }

    /**
     * Sends message via web sockets.
     *
     * @param msg - {@link Message} message to send.
     * @return true if message was fired, false otherwise.
     */
    public boolean sendMessage(Message msg) {

        if (msg == null) {
            return false;
        }

        if (addMessage(msg)) {
            App.getWebSocketManager().sendMessageToServer(msg);
            return true;
        }

        return false;
    }

    public void onMessageRetrieved(Message message) {
        if (message == null) {
            return;
        }

        addMessage(message);

        Intent intent = new Intent(Constants.INTENT_FILTER_NEW_MESSAGE);
        intent.putExtra(COLUMN_MESSAGE, message.getMessage());
        intent.putExtra(COLUMN_TIMESTAMP, message.getTimestamp());
        intent.putExtra(COLUMN_USER_ID, message.getUserID());
        intent.putExtra(COLUMN_USER_NAME, message.getUsername());

        App.getLocalBroadcastManager().sendBroadcast(intent);
    }
}
