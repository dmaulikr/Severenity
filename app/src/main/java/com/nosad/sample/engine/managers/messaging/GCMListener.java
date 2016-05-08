package com.nosad.sample.engine.managers.messaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.nosad.sample.App;
import com.nosad.sample.R;
import com.nosad.sample.view.activities.MainActivity;

/**
 * Class is responsible for handling GCM messages from application server.
 * Posts local notification if message was received.
 *
 * Created by Novosad on 5/4/16.
 */
public class GCMListener extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Intent intent = new Intent(GCMManager.MESSAGE_RECEIVED);
        intent.putExtra("message", message);
        App.getLocalBroadcastManager().sendBroadcast(intent);
        sendNotification(message);
    }

    /**
     * Sends notification to the {@link MainActivity} so notification can be displayed inside
     * of the activity.
     *
     * @param message - text of the message.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(GCMManager.MESSAGE_RECEIVED);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getResources().getString(R.string.severenity_notification))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
