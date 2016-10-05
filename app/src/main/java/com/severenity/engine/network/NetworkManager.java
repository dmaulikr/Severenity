package com.severenity.engine.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.severenity.R;
import com.severenity.utils.common.Constants;

import java.util.ArrayList;

/**
 * Responsible for managing connections and sending appropriate connectivity change updates to
 * observers set via <code>addOnConnectionChangedListener(...)</code>
 *
 * Created by Novosad on 10/2/16.
 */
public class NetworkManager {
    private static ConnectivityManager connectivityManager;
    private static ArrayList<OnConnectionChangedListener> connectionChangedListeners = new ArrayList<>();
    private static boolean wifiConnected = false;
    private static boolean mobileConnected = false;

    public NetworkManager(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Receives updates for the network changes.
     * Checks for both mobile / WiFi networks.
     */
    public static class NetworkReceiver extends BroadcastReceiver {
        public NetworkReceiver() {
            // Default constructor
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    wifiConnected = true;
                    Log.d(Constants.TAG, "WiFi " + context.getResources().getString(R.string.connected));
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    mobileConnected = true;
                    Log.d(Constants.TAG, "Mobile " + context.getResources().getString(R.string.connected));
                } else {
                    Log.d(Constants.TAG, context.getResources().getString(R.string.connected));
                }
            } else {
                Log.d(Constants.TAG, context.getResources().getString(R.string.disconnected));
            }

            // Notify listeners for connectivity change
            for (OnConnectionChangedListener listener : connectionChangedListeners) {
                listener.onConnectionChanged(wifiConnected || mobileConnected);
            }
        }
    }

    public interface OnConnectionChangedListener {
        void onConnectionChanged(boolean connected);
    }

    /**
     * Shows if device is currently connected to the internet.
     *
     * @return true if connected or connecting, false otherwise.
     */
    public boolean isConnected() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * Adds new observer for the connection change.
     *
     * @param listener - {@link OnConnectionChangedListener} listener instance to add.
     */
    public void addOnConnectionChangedListener(OnConnectionChangedListener listener) {
        connectionChangedListeners.add(listener);
    }

    /**
     * Removes observer from the listeners list.
     *
     * @param listener - {@link OnConnectionChangedListener} listener instance to remove.
     */
    public void removeOnConnectionChangedListener(OnConnectionChangedListener listener) {
        connectionChangedListeners.remove(listener);
    }
}
