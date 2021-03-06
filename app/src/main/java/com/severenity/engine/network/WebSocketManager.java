package com.severenity.engine.network;

import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphResponse;
import com.google.android.gms.maps.model.LatLng;
import com.severenity.App;
import com.severenity.entity.Message;
import com.severenity.entity.user.User;
import com.severenity.utils.FacebookUtils;
import com.severenity.utils.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Manages web socket communications, opening, sending of messages and closing of opened
 * sockets. Responsible for managing states of the mSocket and communication layer.
 *
 * Created by Oleg Novosad on 8/26/2015.
 */
public class WebSocketManager {
    private Socket mSocket = null;

    public WebSocketManager() {
        createSocket(Constants.HOST, true);
    }

    /**
     * Initializes web mSocket client
     *
     * @param connect - if true - calls .connect() method on create mSocket
     */
    private boolean createSocket(String address, boolean connect) {
        if (address == null || address.equalsIgnoreCase("")) {
            return false;
        }

        URI uri;
        try {
            uri = new URI(address);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }

        mSocket = IO.socket(uri);

        if (connect) {
            mSocket.connect();
        }

        return true;
    }

    /**
     * Disconnects web mSocket client from server if web mSocket exists or is opened.
     */
    public void disconnectSocket() {
        if (mSocket == null || mSocket.connected()) {
            return;
        }

        // TODO: Replace device info with user info
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", App.getUserManager().getCurrentUser().getId());
            mSocket.emit(Socket.EVENT_DISCONNECT, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            unSubscribeFromEvents();
            mSocket.close();
        }
    }

    /**
     * Shows whether current socket connection is opened.
     *
     * @return state of the connection of current socket.
     */
    public boolean isConnected() {
        return mSocket != null && mSocket.connected();
    }

    /**
     * Subscription for user, place and other entity updates sent from server in order
     * to update local database.
     */
    private void subscribeForEntityUpdateEvents() {
        if (mSocket == null) {
            return;
        }

        mSocket.on(Constants.SOCKET_EVENT_UPDATE_USER, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for (Object arg : args) {
                    if (arg != null) {
                        try {
                            JSONObject response = (JSONObject) arg;
                            User user = new User();
                            user.setId(response.getString("userId"));
                            user.setActionRadius(response.getInt("actionRadius"));
                            user.setViewRadius(response.getInt("viewRadius"));
                            user.setCredits(response.getInt("credits"));
                            user.setMaxEnergy(response.getInt("maxEnergy"));
                            user.setEnergy(response.getInt("energy"));
                            user.setExperience(response.getInt("experience"));
                            user.setDistance(response.getInt("distance"));
                            user.setLevel(response.getInt("level"));
                            user.setTickets(response.getInt("tickets"));
                            user.setTips(response.getInt("tips"));

                            App.getUserManager().updateCurrentUserLocallyWithUser(user);
                            App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_UPDATE_UI));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        // subscribe for place updates events
        mSocket.on(Constants.SOCKET_EVENT_UPDATE_PLACE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for (Object arg : args) {
                    JSONObject response = (JSONObject) arg;
                    Log.e(Constants.TAG, "Updated place event: " + response.toString());
                    try {
                        String result = response.getString("result");
                        if (!result.equalsIgnoreCase("success")) {
                            return;
                        }

                        final JSONObject data = response.getJSONObject("data");
                        String action = data.getString("action");
                        final String by = data.getString("by");
                        final String placeId = data.getJSONObject("place").getString("placeId");

                        Handler handler = new Handler(Looper.getMainLooper());
                        Runnable runnable = null;
                        switch (action) {
                            case "capture": // Constants.PlaceAction
                                runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        // Changes Marker on the map to replicate current place state.
                                        App.getPlacesManager().addOwnerToPlace(placeId, by);
                                        App.getLocationManager().markPlaceMarkerAsCapturedUncaptured(placeId);
                                        // instruct to hide actions buttons
                                        App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_HIDE_USER_ACTIONS));
                                    }
                                };
                                break;
                            case "remove": // Constants.PlaceAction
                                runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String removedOwnerId = data.getString("removed");
                                            Intent intent = new Intent(Constants.INTENT_FILTER_DELETE_OWNER);
                                            intent.putExtra(Constants.USER_ID, removedOwnerId);
                                            App.getLocalBroadcastManager().sendBroadcast(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                break;
                        }

                        if (runnable != null) {
                            handler.post(runnable);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Unsubscription from user, place and other entity updates sent from server in order
     * to stop updating local database.
     */
    private void unSubscribeFromEntityUpdateEvents() {
        if (mSocket == null) {
            return;
        }

        mSocket.off(Constants.SOCKET_EVENT_UPDATE_USER);
    }

    /**
     * Subscribes to location events from the server.
     *
     */
    public void subscribeForLocationEvent() {
        if (mSocket == null) {
            return;
        }

        // subscribe for location events
        mSocket.on(Constants.SOCKET_EVENT_LOCATION, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for (Object arg : args) {
                    try {
                        JSONObject jsonObject = (JSONObject) arg;

                        String id = jsonObject.getString("id");
                        final User user = new User();

                        FacebookUtils.getFacebookUserById(id, "id, name, email", new FacebookUtils.Callback() {
                            @Override
                            public void onResponse(GraphResponse response) {
                                try {
                                    JSONObject data = response.getJSONObject();
                                    if (data.has("name") && data.has("id")) {
                                        if (data.has("email")) {
                                            user.setEmail(data.getString("email"));
                                        }
                                        user.setName(data.getString("name"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        user.setId(id);

                        final LatLng latLng = new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lng"));
                        final Handler handler = new Handler(Looper.getMainLooper());
                        final Runnable displayUserAtLocation = new Runnable() {
                            @Override
                            public void run() {
                                App.getLocationManager().displayUserAt(user, latLng);
                            }
                        };

                       if (App.getUserManager().getUserById(id) == null) {
                           App.getUserManager().addUser(user);
                           handler.post(displayUserAtLocation);
                        } else {
                            handler.post(displayUserAtLocation);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Unsubscribe from location events from the server.
     *
     */
    public void unSubscribeFromLocationEvents() {
        if (mSocket == null) {
            return;
        }

        mSocket.off(Constants.SOCKET_EVENT_LOCATION);
    }

    /**
     * Sends location specified via mSocket if mSocket is connected.
     * Message structure is:
     *
     * @param location - current location of the user
     */
    public void sendLocationToServer(Location location) {
        if (!mSocket.connected()) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", AccessToken.getCurrentAccessToken().getUserId());
            jsonObject.put("lat", location.getLatitude());
            jsonObject.put("lng", location.getLongitude());
            mSocket.emit("location", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send user updates to the server (commonly distance passed update).
     *
     * @param requestData - object with data for the server.
     */
    public void sendUserUpdateToServer(JSONObject requestData) {
        if (!mSocket.connected()) {
            return;
        }

        mSocket.emit(Constants.SOCKET_EVENT_UPDATE_USER, requestData);
    }

    /**
     * Sends authenticated event to the server so new session record is registered.
     */
    public void sendAuthenticatedToServer() {
        if (!mSocket.connected()) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", AccessToken.getCurrentAccessToken().getUserId());
            mSocket.emit(Constants.SOCKET_EVENT_AUTHENTICATE, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribes to messages events from the server.
     */
    private void subscribeForMessageEvent() {
        if (mSocket == null) {
            return;
        }

        // subscribe for message event
        mSocket.on(Constants.SOCKET_EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for (Object arg : args) {
                    final JSONObject jsonObject = (JSONObject) arg;
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            App.getMessageManager().onMessageRetrieved(jsonObject);
                        }
                    });
                }
            }
        });
    }

    /**
     * Unsubscribe from location events from the server.
     *
     */
    private void unSubscribeFromMessageEvents() {
        if (mSocket == null) {
            return;
        }

        mSocket.off(Constants.SOCKET_EVENT_MESSAGE);
    }

    /**
     * Sends message via mSocket if mSocket is connected.
     * Message structure is:
     *
     * @param message - chat message to be sent
     */
    public void sendMessageToServer(Message message) {
        if (!mSocket.connected()) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("messageId", message.getMessageId());
            jsonObject.put("senderId", AccessToken.getCurrentAccessToken().getUserId());
            jsonObject.put("text", message.getText());
            jsonObject.put("senderName", message.getSenderName());
            jsonObject.put("timestamp", message.getTimestamp());
            mSocket.emit(Constants.SOCKET_EVENT_MESSAGE, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends users action to the server. For now sedns this call sends actions thet are done
     * on the user. Attack/defend/
     *
     * @param data - data related to action to be proceed, includes user id in "by" field.
     * @param action - identifies action that was performed.
     */
    public void sendUserActionToServer(JSONObject data, Constants.UsersActions action) {
        if (!mSocket.connected()) {
            return;
        }

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("data", data);
            requestData.put("action", action.toString().toLowerCase());
            mSocket.emit(Constants.SOCKET_EVENT_USER_ACTION, requestData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribes for events from server via web sockets.
     */
    public void subscribeForEvents() {
        subscribeForMessageEvent();
        subscribeForEntityUpdateEvents();
    }

    /**
     * Unsubscribes for events from server via web sockets.
     */
    private void unSubscribeFromEvents() {
        unSubscribeFromEntityUpdateEvents();
        unSubscribeFromLocationEvents();
        unSubscribeFromMessageEvents();
    }
}
