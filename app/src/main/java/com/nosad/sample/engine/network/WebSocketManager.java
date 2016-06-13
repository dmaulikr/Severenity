package com.nosad.sample.engine.network;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphResponse;
import com.google.android.gms.maps.model.LatLng;
import com.nosad.sample.App;
import com.nosad.sample.entity.Message;
import com.nosad.sample.entity.User;
import com.nosad.sample.utils.FacebookUtils;
import com.nosad.sample.utils.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Oleg Novosad on 8/26/2015.
 *
 * Manages web socket communications, opening, sending of messages and closing of opened
 * sockets. Responsible for managing states of the mSocket and communication layer.
 */
public class WebSocketManager {
    private Socket mSocket = null;
    private Context mContext;

    public WebSocketManager(Context context) {
        this.mContext = context;
    }

    /**
     * Initializes web mSocket client
     *
     * @param connect - if true - calls .connect() method on create mSocket
     */
    public boolean createSocket(String address, boolean connect) {
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
            unSubscribeFromQuestEvents();
            unSubscribeFromMessageEvents();
            unSubscribeFromLocationEvents();
            mSocket.close();
        }
    }

    /**
     * Returns current mSocket.
     *
     * @return mSocket - current mSocket
     */
    public Socket getSocket() {
        return mSocket;
    }

    public void subscribeForQuestEvents() {
        if (mSocket == null) {
            return;
        }

        mSocket.on(Constants.SOCKET_EVENT_UPDATE_QUEST, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for (Object arg : args) {
                    JSONObject quest = (JSONObject) arg;
                    try {
                        int status = quest.getInt("status");
                        long questId = quest.getLong("questId");

                        App.getQuestManager().updateQuestStatusAndPopulate(questId, status);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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

                                            App.getUserManager().addUser(user);
                                            handler.post(displayUserAtLocation);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            handler.post(displayUserAtLocation);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                                        App.getPlacesManager().addOwnerToPlace(placeId, by);
                                        // Changes Marker on the map to replicate current place state.
                                        App.getLocationManager().markPlaceMarkerAsCapturedUncaptured(placeId, true/*captured*/);
                                        // instruct to hide actions buttons
                                        App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_HIDE_PLACE_ACTIONS));
                                        Toast.makeText(mContext, "Place has been captured", Toast.LENGTH_SHORT).show();
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
     * Unsubscribe from location events from the server.
     *
     */
    public void unSubscribeFromLocationEvents() {

        Socket socket = getSocket();
        if (socket == null)
            return;

        socket.off(Constants.SOCKET_EVENT_LOCATION);
    }

    /**
     * Unsubscribe from quest events from the server.
     *
     */
    public void unSubscribeFromQuestEvents() {
        if (mSocket == null)
            return;

        mSocket.off(Constants.SOCKET_EVENT_UPDATE_QUEST);
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
     * Sends API request to add owner to the list of owners for the place
     *
     * @param action - action to perform.
     * @param placeId - place captured.
     * @param data - data to pass with action.
     */
    public void sendPlaceUpdateToServer(String placeId, Constants.PlaceAction action, JSONObject data) {
        if (!mSocket.connected()) {
            return;
        }

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("placeId", placeId);
            requestData.put("action", action.toString());
            requestData.put("data", data);
            mSocket.emit(Constants.SOCKET_EVENT_UPDATE_PLACE, requestData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
     * Subscribes to location events from the server.
     */
    public void subscribeForMessageEvent() {
        Socket socket = getSocket();
        if (socket == null) {
            return;
        }

        // subscribe for message event
        socket.on(Constants.SOCKET_EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for (Object arg : args) {
                    try {
                        JSONObject jsonObject = (JSONObject) arg;
                        final Message message = new Message();
                        message.setUserID(jsonObject.getString("id"));
                        message.setMessage(jsonObject.getString("text"));
                        message.setUserName(jsonObject.getString("name"));
                        message.setTimestamp(jsonObject.getString("timestamp"));

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                App.getMessageManager().onMessageRetrieved(message);
                            }
                        });
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
    public void unSubscribeFromMessageEvents() {
        Socket socket = getSocket();
        if (socket == null) {
            return;
        }

        socket.off(Constants.SOCKET_EVENT_MESSAGE);
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
            jsonObject.put("id", AccessToken.getCurrentAccessToken().getUserId());
            jsonObject.put("text", message.getMessage());
            jsonObject.put("name", message.getUserName());
            jsonObject.put("timestamp", message.getTimestamp());
            mSocket.emit(Constants.SOCKET_EVENT_MESSAGE, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends quest accepted event to the server, so quest can be attached to the player.
     *
     * @param id - id of the quest that was accepted.
     */
    public void sendQuestAccepted(String userId, long id) {
        if (!mSocket.connected()) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("questId", id);
            jsonObject.put("accepted", true);
            jsonObject.put("userId", userId);
            mSocket.emit(Constants.SOCKET_EVENT_UPDATE_QUEST, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
