package com.nosad.sample.engine.network;

import android.location.Location;
import android.util.Log;

import com.nosad.sample.App;
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
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", App.getUserManager().getCurrentUser().getId());
                    socket.emit(Socket.EVENT_CONNECT, jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for (Object arg : args) {
                    Log.i(Constants.TAG, "Socket message received: " + arg.toString());
                }
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(Constants.TAG, "Disconnected " + App.getUserManager().getCurrentUser().getName());
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

    public Socket getSocket() {
        return socket;
    }

    public void sendLocationToServer(Location location) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lat", location.getLatitude());
            jsonObject.put("lng", location.getLongitude());
            socket.emit("location", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
