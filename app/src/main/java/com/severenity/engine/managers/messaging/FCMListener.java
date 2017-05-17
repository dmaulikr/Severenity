package com.severenity.engine.managers.messaging;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.severenity.App;
import com.severenity.entity.quest.Quest;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;
import com.severenity.view.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Class is responsible for handling GCM messages from application server.
 * Posts local notification if message was received.
 *
 * Created by Novosad on 5/4/16.
 */
public class FCMListener extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        Map<String, String> data = message.getData();
        String type = data.get("notificationType");
        if (type == null) {
            Log.e(Constants.TAG, "'type' is missing in GCM message.");
            return;
        }

        switch (type) {
            case "message":
                String m = data.get("message");

                Intent messageIntent = new Intent(this, MainActivity.class);
                messageIntent.setAction(GCMManager.MESSAGE_RECEIVED);
                messageIntent.putExtra("message", m);

                App.getLocalBroadcastManager().sendBroadcast(messageIntent);
                Utils.sendNotification(m, this, messageIntent, 0);
                break;
            case "quest":
                Intent questIntent = new Intent(this, MainActivity.class);
                questIntent.setAction(GCMManager.QUEST_RECEIVED);

                int questType = Integer.valueOf(data.get("type"));
                String id = data.get("id");

                if (App.getQuestManager().getQuestById(id) != null) {
                    return;
                }

                questIntent.putExtra("id", id);
                questIntent.putExtra("title", data.get("title"));
                questIntent.putExtra("credits", Long.valueOf(data.get("credits")));
                questIntent.putExtra("experience", Long.valueOf(data.get("experience")));
                questIntent.putExtra("expirationTime", data.get("expirationDate"));
                questIntent.putExtra("status", Quest.QuestStatus.Created.ordinal());

                if (questType == Quest.QuestType.Distance.ordinal()) {
                    questIntent.putExtra("type", Quest.QuestType.Distance.ordinal());
                    questIntent.putExtra("distance", Integer.valueOf(data.get("distance")));
                } else if (questType == Quest.QuestType.Capture.ordinal()) {
                    questIntent.putExtra("type", Quest.QuestType.Capture.ordinal());
                    questIntent.putExtra("placeType", data.get("placeType"));
                    questIntent.putExtra("placeTypeValue", Integer.valueOf(data.get("placeTypeValue")));
                } else if (questType == Quest.QuestType.Collect.ordinal()) {
                    questIntent.putExtra("type", Quest.QuestType.Collect.ordinal());
                    questIntent.putExtra("characteristic", data.get("characteristic"));
                    questIntent.putExtra("characteristicAmount", Integer.valueOf(data.get("characteristicAmount")));
                }

                App.getLocalBroadcastManager().sendBroadcast(questIntent);
                Utils.sendNotification(Constants.NOTIFICATION_MSG_NEW_QUEST, this, questIntent, 0);

                break;
            default:
                Log.e(Constants.TAG, "Unknown GCM message type received from the server.");
        }
    }
}
