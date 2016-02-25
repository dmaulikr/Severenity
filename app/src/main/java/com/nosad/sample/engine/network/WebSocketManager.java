package com.nosad.sample.engine.network;

import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.nosad.sample.utils.common.Constants;
import com.nosad.sample.entity.Message;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Oleg Novosad on 8/26/2015.
 *
 * Manages web socket communications, opening, sending of messages and closing of opened
 * sockets. Responsible for managing states of the socket and communication layer.
 */
public class WebSocketManager {
    public final static WebSocketManager instance = new WebSocketManager();
    private WebSocketClient webSocketClient;

    private WebSocketManager() {
        // Exists only to defeat instantiation.
    }

    /**
     * Initializes web socket client
     *
     * @param connect - if true - calls .connect() method on create socket
     */
    public void createWebSocket(String address, boolean connect) {
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

        webSocketClient = new WSClient(uri);

        if (connect) {
            webSocketClient.connect();
        }
    }

    /**
     * Disconnects web socket client from server if web socket exists or is opened.
     */
    public void disconnectWebSocketClient() {
        if (webSocketClient == null || webSocketClient.getConnection().isClosed()) {
            return;
        }

        // TODO: Replace device info with user info
        Message message = new Message("text", "Disconnected " + Build.MANUFACTURER + " " + Build.MODEL);
        webSocketClient.send(message.toJSON().toString());
        webSocketClient.close();
    }

    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }

    public void setWebSocketClient(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    public void sendLocationToServer(Location location) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lat", location.getLatitude());
            jsonObject.put("lng", location.getLongitude());
            Message message = new Message("location", jsonObject.toString());
            getWebSocketClient().send(message.toJSON().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class WSClient extends WebSocketClient {
        WSClient(URI uri) {
            super(uri, new Draft_17());
        }

        @Override
        public void onOpen(ServerHandshake handShakeData) {
            Log.i(Constants.TAG, "WebSocketClient opened");
            Message message = new Message("text", "Connected " + Build.MANUFACTURER + " " + Build.MODEL);
            webSocketClient.send(message.toJSON().toString());
        }

        @Override
        public void onMessage(String message) {
            Log.i(Constants.TAG, "WebSocketClient message received: " + message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.i(Constants.TAG, "Closed " + reason + ", code: " + code + ", remote: " + remote);
        }

        @Override
        public void onError(Exception e) {
            Log.i(Constants.TAG, "Error " + e.getMessage());
        }
    }
}
