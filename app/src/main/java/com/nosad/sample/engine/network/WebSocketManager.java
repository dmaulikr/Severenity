package com.nosad.sample.engine.network;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.facebook.AccessToken;
import com.google.android.gms.maps.model.LatLng;
import com.nosad.sample.App;
import com.nosad.sample.entity.Message;
import com.nosad.sample.entity.User;
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
            unsubscribeFromMessageEvents();
            unsubscribeFromLocationEvents();
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

    /**
     * Subscribes to location events from the server.
     *
     */
    public void subscribeForLocationEvent(){

        Socket socket = getSocket();
        if (socket == null)
            return;

        // subscribe for location events
        socket.on(Constants.SOCKET_EVENT_LOCATION, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for (Object arg : args) {
                    try {
                        JSONObject jsonObject = (JSONObject) arg;
                        final User user = new User();
                        user.setId(jsonObject.getString("id"));
                        final LatLng latLng = new LatLng(
                                jsonObject.getDouble("lat"),
                                jsonObject.getDouble("lng")
                        );

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                App.getLocationManager().displayUserAt(user, latLng);
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
    public void unsubscribeFromLocationEvents(){

        Socket socket = getSocket();
        if (socket == null)
            return;

        socket.off(Constants.SOCKET_EVENT_LOCATION);
    }

    /**
     * Sends location specified via mSocket if mSocket is connected.
     * Message structure is:
     *
     * @param location
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
    public void unsubscribeFromMessageEvents() {
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
     * @param message
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
            mSocket.emit("chat message", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
