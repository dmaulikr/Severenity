package com.nosad.sample.engine.managers.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.nosad.sample.App;
import com.nosad.sample.entity.quest.Quest;
import com.nosad.sample.utils.Utils;
import com.nosad.sample.utils.common.Constants;
import com.nosad.sample.view.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class is responsible for handling GCM messages from application server.
 * Posts local notification if message was received.
 *
 * Created by Novosad on 5/4/16.
 */
public class GCMListener extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String type = data.getString("type");
        if (type == null) {
            Log.e(Constants.TAG, "'type' is missing in GCM message.");
            return;
        }

        switch (type) {
            case "message":
                String message = data.getString("message");

                Intent messageIntent = new Intent(this, MainActivity.class);
                messageIntent.setAction(GCMManager.MESSAGE_RECEIVED);
                messageIntent.putExtra("message", message);

                App.getLocalBroadcastManager().sendBroadcast(messageIntent);
                Utils.sendNotification(message, this, messageIntent, 0);
                break;
            case "quest":
                Intent questIntent = new Intent(GCMManager.QUEST_RECEIVED);
                String quest = data.getString("quest");

                if (quest == null) {
                    Log.e(Constants.TAG, "'quest' object is missing in GCM message");
                    return;
                }

                try {
                    JSONObject questObj = new JSONObject(quest);
                    String questType = questObj.getString("type");
                    long id = questObj.getLong("id");

                    if (App.getQuestManager().getQuestById(id) != null) {
                        return;
                    }

                    questIntent.putExtra("id", id);
                    questIntent.putExtra("title", questObj.getString("title"));
                    questIntent.putExtra("credits", questObj.getLong("credits"));
                    questIntent.putExtra("experience", questObj.getLong("experience"));
                    questIntent.putExtra("expirationTime", questObj.getString("expirationTime"));
                    questIntent.putExtra("status", Quest.QuestStatus.New.ordinal());

                    if (questType.equals(Quest.QuestType.Distance.toString())) {
                        questIntent.putExtra("type", Quest.QuestType.Distance.ordinal());
                        questIntent.putExtra("distance", questObj.getInt("distance"));
                    } else if (questType.equals(Quest.QuestType.Capture.toString())) {
                        questIntent.putExtra("type", Quest.QuestType.Capture.ordinal());
                        questIntent.putExtra("placeType", questObj.getString("placeType"));
                        questIntent.putExtra("placeTypeValue", questObj.getInt("placeTypeValue"));
                    } else if (questType.equals(Quest.QuestType.Collect.toString())) {
                        questIntent.putExtra("type", Quest.QuestType.Collect.ordinal());
                        questIntent.putExtra("characteristic", questObj.getString("characteristic"));
                        questIntent.putExtra("characteristicAmount", questObj.getInt("characteristicAmount"));
                    }

                    App.getLocalBroadcastManager().sendBroadcast(questIntent);
                    Utils.sendNotification(Constants.NOTIFICATION_MSG_NEW_QUEST, this, questIntent, (int) id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            default:
                Log.e(Constants.TAG, "Unknown GCM message type received from the server.");
                return;
        }
    }
}
