package com.severenity.engine.managers.data;

import android.content.Context;
import android.content.Intent;
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
import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Andriy on 4/27/2016.
 */
public class MessageManager extends DataManager {

    Realm realm;

    public MessageManager(Context context) {
        super(context);
        realm = Realm.getDefaultInstance();
    }

    /**
     * Retrieves all messages the local database and tries to update with new ones from the server.
     *
     * @return list of messages from DB.
     */
    public ArrayList<Message> getMessages() {
        RealmResults<Message> messages = realm.where(Message.class).findAllAsync();
        return new ArrayList<>(realm.copyFromRealm(messages));
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
                        final JSONObject messageObject = messageObjects.getJSONObject(i);
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.createOrUpdateObjectFromJson(Message.class, messageObject);
                            }
                        });
                        onMessageRetrieved(messageObject);
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
     * @param message - {@link Message} message to send.
     */
    public void sendMessage(final Message message) {
        if (message == null) {
            return;
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(message);
            }
        });

        App.getWebSocketManager().sendMessageToServer(message);
    }

    public void onMessageRetrieved(final JSONObject message) {
        if (message == null) {
            return;
        }

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createOrUpdateObjectFromJson(Message.class, message);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                try {
                    Message m = realm.where(Message.class).equalTo("messageId", message.getString("messageId")).findFirst();
                    Intent intent = new Intent(Constants.INTENT_FILTER_NEW_MESSAGE);
                    intent.putExtra("messageId", m.getMessageId());
                    intent.putExtra("text", m.getText());
                    intent.putExtra("timestamp", m.getTimestamp());
                    intent.putExtra("senderId", m.getSenderId());
                    intent.putExtra("senderName", m.getSenderName());

                    App.getLocalBroadcastManager().sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }
}
