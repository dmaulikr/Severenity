package com.nosad.sample.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.nosad.sample.App;
import com.nosad.sample.engine.network.RequestCallback;
import com.nosad.sample.entity.quest.CaptureQuest;
import com.nosad.sample.entity.quest.CollectQuest;
import com.nosad.sample.entity.quest.DistanceQuest;
import com.nosad.sample.entity.quest.Quest;
import com.nosad.sample.utils.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_CHARACTERISTIC;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_CHARACTERISTIC_AMOUNT;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_CREDITS_AMOUNT;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_DESCRIPTION;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_DISTANCE;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_EXPIRATION_TIME;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_EXP_AMOUNT;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_ID;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_PLACE_TYPE;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_PLACE_TYPE_VALUE;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_PROGRESS;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_STATUS;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_TITLE;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_TYPE;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.TABLE_QUESTS;

/**
 * Class is responsible for managing quests access / logic between database and other modules.
 *
 * Created by Novosad on 5/9/16.
 */
public class QuestManager extends DataManager {
    public QuestManager(Context context) {
        super(context);
    }

    public void addQuests(ArrayList<Quest> quests) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            for (Quest quest : quests) {
                db.insert(TABLE_QUESTS, "NULL", createValuesFrom(quest));
            }
            db.setTransactionSuccessful();
        } finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
            db.close();
        }
    }

    public void deleteQuest(Quest quest) {
        checkIfNull(quest);

        deleteQuestById(quest.getId());
    }

    public void deleteQuestById(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_QUESTS, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    private ContentValues createValuesFrom(Quest quest) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, quest.getId());
        values.put(COLUMN_TITLE, quest.getTitle());
        values.put(COLUMN_DESCRIPTION, quest.getDescription());
        values.put(COLUMN_EXP_AMOUNT, quest.getExperience());
        values.put(COLUMN_CREDITS_AMOUNT, quest.getCredits());
        values.put(COLUMN_STATUS, quest.getStatus().ordinal());
        values.put(COLUMN_TYPE, quest.getType().ordinal());
        values.put(COLUMN_EXPIRATION_TIME, quest.getExpirationTime());

        if (quest.getType() == Quest.QuestType.Distance) {
            values.put(COLUMN_DISTANCE, ((DistanceQuest) quest).getDistance());
        } else if (quest.getType() == Quest.QuestType.Capture) {
            values.put(COLUMN_PLACE_TYPE, ((CaptureQuest) quest).getPlaceType());
            values.put(COLUMN_PLACE_TYPE_VALUE, ((CaptureQuest) quest).getPlaceTypeValue());
        } else if (quest.getType() == Quest.QuestType.Collect) {
            values.put(COLUMN_CHARACTERISTIC, ((CollectQuest) quest).getCharacteristic().ordinal());
            values.put(COLUMN_CHARACTERISTIC_AMOUNT, ((CollectQuest) quest).getAmount());
        }

        return values;
    }

    public boolean addQuest(Quest quest) {
        Quest q = getQuest(quest);
        if (q != null) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long success = db.insert(TABLE_QUESTS, "NULL", createValuesFrom(quest));
        db.close();

        return success != -1;
    }


    public Quest getQuestById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_QUESTS,
                new String[]{
                        COLUMN_ID, COLUMN_TITLE, COLUMN_DESCRIPTION,
                        COLUMN_EXP_AMOUNT, COLUMN_CREDITS_AMOUNT, COLUMN_EXPIRATION_TIME,
                        COLUMN_STATUS, COLUMN_TYPE, COLUMN_DISTANCE,
                        COLUMN_PLACE_TYPE, COLUMN_PLACE_TYPE_VALUE,
                        COLUMN_CHARACTERISTIC, COLUMN_CHARACTERISTIC_AMOUNT
                },
                "id = " + id,
                null, null, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            Quest quest = getQuestFromCursor(cursor);

            if (quest.getType() == Quest.QuestType.Distance) {
                quest = new DistanceQuest(quest, cursor.getInt(cursor.getColumnIndex(COLUMN_DISTANCE)));
            } else if (quest.getType() == Quest.QuestType.Capture) {
                quest = new CaptureQuest(quest,
                        cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_TYPE)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_PLACE_TYPE_VALUE))
                );
            } else if (quest.getType() == Quest.QuestType.Collect) {
                Constants.Characteristic characteristic = Constants.Characteristic.values()[
                        cursor.getInt(cursor.getColumnIndex(COLUMN_CHARACTERISTIC))
                        ];

                quest = new CollectQuest(quest, characteristic, cursor.getInt(cursor.getColumnIndex(COLUMN_CHARACTERISTIC_AMOUNT)));
            }

            cursor.close();
            db.close();

            return quest;
        } else {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            return null;
        }
    }

    private Quest getQuestFromCursor(Cursor cursor) {
        Quest quest = new Quest();
        quest.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
        quest.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        quest.setType(Quest.QuestType.values()[cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE))]);
        quest.setStatus(Quest.QuestStatus.values()[cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS))]);
        quest.setExperience(cursor.getLong(cursor.getColumnIndex(COLUMN_EXP_AMOUNT)));
        quest.setCredits(cursor.getLong(cursor.getColumnIndex(COLUMN_CREDITS_AMOUNT)));

        String expirationTime = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRATION_TIME));
        if (!expirationTime.equals("null")) {
            quest.setExpirationTime(expirationTime);
        }

        return quest;
    }

    /**
     * Returns specific quest queried by quest object.
     *
     * @param quest - quest to find.
     * @return quest if found.
     */
    public Quest getQuest(Quest quest) {
        checkIfNull(quest);

        return getQuestById(quest.getId());
    }

    /**
     * Returns a list of quests from local DB.
     *
     * @return list of quests.
     */
    public ArrayList<Quest> getQuests() {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query(TABLE_QUESTS, null, null, null, null, null, null, null)) {

            if (cursor.getCount() == 0) {
                return null;
            }

            ArrayList<Quest> questsList = new ArrayList<>(cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    Quest quest = getQuestFromCursor(cursor);

                    if (quest.getType() == Quest.QuestType.Distance) {
                        DistanceQuest distanceQuest = new DistanceQuest(quest,
                                cursor.getInt(cursor.getColumnIndex(COLUMN_DISTANCE))
                        );

                        questsList.add(distanceQuest);
                    } else if (quest.getType() == Quest.QuestType.Capture) {
                        CaptureQuest captureQuest = new CaptureQuest(quest,
                                cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_TYPE)),
                                cursor.getInt(cursor.getColumnIndex(COLUMN_PLACE_TYPE_VALUE))
                        );

                        questsList.add(captureQuest);
                    } else if (quest.getType() == Quest.QuestType.Collect) {
                        Constants.Characteristic characteristic = Constants.Characteristic.values()[
                                cursor.getInt(cursor.getColumnIndex(COLUMN_CHARACTERISTIC))
                                ];

                        CollectQuest collectQuest = new CollectQuest(quest,
                                characteristic,
                                cursor.getInt(cursor.getColumnIndex(COLUMN_CHARACTERISTIC_AMOUNT))
                        );

                        questsList.add(collectQuest);
                    } else {
                        questsList.add(quest);
                    }
                } while (cursor.moveToNext());
            }

            return questsList;
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Add quest to local db if it was accepted.
     *
     * @param quest - accepted quest.
     */
    public void onQuestAccepted(Quest quest) {
        if (quest == null) {
            return;
        }

        if (!addQuest(quest)) {
            return;
        }

        try {
            JSONObject data = new JSONObject();
            data.put("questId", quest.getId());
            data.put("reason", "accepted");

            App.getRestManager().updateQuestWithData(App.getUserManager().getCurrentUser().getId(), data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates status of quest if found and populates it to UI via intent.
     *
     * @param questId - quest to update
     * @param status  - new {@link com.nosad.sample.entity.quest.Quest.QuestStatus}. None < New < Progress < Finished.
     */
    public void updateQuestStatusAndPopulate(long questId, int status) {
        Quest quest = getQuestById(questId);

        if (quest == null) {
            Log.e(Constants.TAG, "Quest with id " + questId + " not found.");
            return;
        }

        if (status > quest.getStatus().ordinal()) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_STATUS, status);
            db.update(TABLE_QUESTS, values, "id = " + questId, null);
            db.close();
        } else {
            Log.e(Constants.TAG, "New quest status is incorrect for quest " + questId);
            return;
        }

        populateQuest(quest);
    }

    /**
     * Populates quest with intent so UI can be updated.
     *
     * @param quest - quest to send via 'new quest' intent.
     */
    private void populateQuest(Quest quest) {
        Intent intent = new Intent(Constants.INTENT_FILTER_NEW_QUEST);
        intent.putExtra(Constants.INTENT_EXTRA_SINGLE_QUEST, true);
        intent.putExtra(COLUMN_ID, quest.getId());
        intent.putExtra(COLUMN_TITLE, quest.getTitle());
        intent.putExtra(COLUMN_DESCRIPTION, quest.getDescription());
        intent.putExtra(COLUMN_EXP_AMOUNT, quest.getExperience());
        intent.putExtra(COLUMN_CREDITS_AMOUNT, quest.getCredits());
        intent.putExtra(COLUMN_STATUS, quest.getStatus().ordinal());
        intent.putExtra(COLUMN_TYPE, quest.getType().ordinal());
        intent.putExtra(COLUMN_EXPIRATION_TIME, quest.getExpirationTime());

        if (quest.getType() == Quest.QuestType.Distance) {
            intent.putExtra(COLUMN_TYPE, Quest.QuestType.Distance.ordinal());
            intent.putExtra(COLUMN_DISTANCE, ((DistanceQuest) quest).getDistance());
        }

        if (quest.getType() == Quest.QuestType.Capture) {
            intent.putExtra(COLUMN_TYPE, Quest.QuestType.Capture.ordinal());
            intent.putExtra(COLUMN_PLACE_TYPE, ((CaptureQuest) quest).getPlaceType());
            intent.putExtra(COLUMN_PLACE_TYPE_VALUE, ((CaptureQuest) quest).getPlaceTypeValue());
        }

        if (quest.getType() == Quest.QuestType.Collect) {
            intent.putExtra(COLUMN_TYPE, Quest.QuestType.Collect.ordinal());
            intent.putExtra(COLUMN_CHARACTERISTIC, ((CollectQuest) quest).getCharacteristic().ordinal());
            intent.putExtra(COLUMN_PLACE_TYPE_VALUE, ((CollectQuest) quest).getAmount());
        }

        App.getLocalBroadcastManager().sendBroadcast(intent);
    }

    /**
     * Adds {@link Quest} object to DB parsing it before the addition.
     *
     * @param questObj - JSON object to be parsed and added to db.
     */
    public void addQuest(JSONObject questObj) {
        Quest quest = getQuestFromJSON(questObj);
        addQuest(quest);
    }

    /**
     * Parses JSON object and returns {@link Quest} object.
     *
     * @param questObj - JSON object to retrieve quest from.
     * @return {@link Quest} object.
     */
    public Quest getQuestFromJSON(JSONObject questObj) {
        Quest quest = new Quest();
        try {
            int questType = questObj.getInt("type");
            long id = questObj.getLong("questId");

            Quest existingQuest = App.getQuestManager().getQuestById(id);
            if (existingQuest != null) {
                return existingQuest;
            }

            quest.setId(id);
            quest.setTitle(questObj.getString("title"));
            quest.setCredits(questObj.getLong("credits"));
            quest.setExperience(questObj.getLong("experience"));
            quest.setExpirationTime(questObj.getString("expirationDate"));
            quest.setStatus(Quest.QuestStatus.values()[questObj.getInt("status")]);

            if (questType == Quest.QuestType.Distance.ordinal()) {
                quest.setType(Quest.QuestType.Distance);
                quest = new DistanceQuest(quest, questObj.getInt("distance"));
            } else if (questType == Quest.QuestType.Capture.ordinal()) {
                quest.setType(Quest.QuestType.Capture);
                quest = new CaptureQuest(quest, questObj.getString("placeType"), questObj.getInt("placeTypeValue"));
            } else if (questType == Quest.QuestType.Collect.ordinal()) {
                quest.setType(Quest.QuestType.Collect);
                Constants.Characteristic characteristic;
                switch (questObj.getString("characteristic").toLowerCase()) {
                    case "experience":
                        characteristic = Constants.Characteristic.Experience;
                        break;
                    case "immunity":
                        characteristic = Constants.Characteristic.Immunity;
                        break;
                    case "level":
                        characteristic = Constants.Characteristic.Level;
                        break;
                    case "intelligence":
                        characteristic = Constants.Characteristic.Intelligence;
                        break;
                    default:
                        characteristic = Constants.Characteristic.None;
                }

                quest = new CollectQuest(quest, characteristic, questObj.getInt("characteristicAmount"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return quest;
    }

    /**
     * Deletes all quests from the local database.
     */
    private void deleteQuests() {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
            db.delete(TABLE_QUESTS, null, null);
        } catch (SQLException e) {
            Log.e(Constants.TAG, "QuestsManager: error clearing quests tables. " + e.getMessage());
        }
    }

    /**
     * Renew quests list with quests received from the server.
     */
    public void refreshWithQuestsFromServer() {
        deleteQuests();
        App.getRestManager().getQuestsFromServer(App.getUserManager().getCurrentUser().getId(), new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                try {
                    if (!response.getString("result").equalsIgnoreCase("success")) {
                        return;
                    }

                    JSONArray quests = response.getJSONArray("data");
                    ArrayList<Quest> questsList = new ArrayList<>();
                    for (int i = 0; i < quests.length(); i++) {
                        JSONObject questObject = quests.getJSONObject(i);
                        questsList.add(App.getQuestManager().getQuestFromJSON(questObject));
                    }

                    addQuests(questsList);

                    Intent intent = new Intent(Constants.INTENT_FILTER_NEW_QUEST);
                    intent.putExtra(Constants.INTENT_EXTRA_SINGLE_QUEST, false);
                    App.getLocalBroadcastManager().sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorCallback(NetworkResponse response) {
                if (response != null) {
                    Log.e(Constants.TAG, "Error response for quests retrieve: " + response.toString());
                } else {
                    Log.e(Constants.TAG, "Error response for quests retrieve is null.");
                }
            }
        });
    }

    /**
     * Used to update quests progress whenever user action has happened.
     */
    public void updateQuestProgress(Quest.QuestType questType, String... newValues) {
        JSONObject request = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            request.put("questType", questType.ordinal());
            switch (questType) {
                case Distance:
                    int distancePassed = Integer.valueOf(newValues[0]);
                    data.put("distance", distancePassed);
                    break;
                case Capture:
                    int placeType = Integer.valueOf(newValues[0]);
                    int value = Integer.valueOf(newValues[1]);
                    data.put("placeType", placeType);
                    data.put("placeValue", value);
                    break;
                case Collect:
                    int characteristic = Integer.valueOf(newValues[0]);
                    int amount = Integer.valueOf(newValues[1]);
                    data.put("characteristic", characteristic);
                    data.put("characteristicAmount", amount);
                    break;
            }

            request.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        App.getRestManager().updateQuestWithData(App.getUserManager().getCurrentUser().getId(), request);
    }

    /**
     * Updates quest entry in local DB with new progress value.
     *
     * @param questId - quest to update.
     * @param value - new progress value to set.
     */
    public void updateQuestProgressLocally(long questId, int value) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROGRESS, value);
        db.update(TABLE_QUESTS, values, "id = " + questId, null);
        db.close();

        Intent intent = new Intent(Constants.INTENT_FILTER_QUEST_UPDATE);
        App.getLocalBroadcastManager().sendBroadcast(intent);
    }
}
