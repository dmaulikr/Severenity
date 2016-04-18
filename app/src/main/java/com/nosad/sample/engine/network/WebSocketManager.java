package com.nosad.sample.engine.network;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.AccessToken;
import com.google.android.gms.maps.model.LatLng;
import com.nosad.sample.App;
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
 * sockets. Responsible for managing states of the socket and communication layer.
 */
public class WebSocketManager {
    private Socket socket;
    private Context context;

    public WebSocketManager(Context context) {
        this.context = context;
    }

    /**
     * Initializes web socket client
     *
     * @param connect - if true - calls .connect() method on create socket
     */
    public void createSocket(String address, boolean connect) {
        if (address == null || address.equalsIgnoreCase("")) {
            return;
        }

        URI uri;
        try {
            uri = new URI(address);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        socket = IO.socket(uri);
        socket.on(Constants.SOCKET_EVENT_LOCATION, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for (Object arg : args) {
                    try {
                        JSONObject jsonObject = (JSONObject) arg;
                        final User user = new User();
                        user.setId(jsonObject.getString("id"));
                        final LatLng latLng = new LatLng(
                            jsonObject.getDouble("lat") + 0.0005,
                            jsonObject.getDouble("lng") + 0.0005
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

        if (connect) {
            socket.connect();
        }
    }

    /**
     * Disconnects web socket client from server if web socket exists or is opened.
     */
    public void disconnectSocket() {
        if (socket == null || socket.connected()) {
            return;
        }

        // TODO: Replace device info with user info
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", App.getUserManager().getCurrentUser().getId());
            socket.emit(Socket.EVENT_DISCONNECT, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    /**
     * Returns current socket.
     *
     * @return socket - current socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Sends location specified via socket if socket is connected.
     * Message structure is:
     *
     * @param location
     */
    public void sendLocationToServer(Location location) {
        if (!socket.connected()) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", AccessToken.getCurrentAccessToken().getUserId());
            jsonObject.put("lat", location.getLatitude());
            jsonObject.put("lng", location.getLongitude());
            socket.emit("location", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
