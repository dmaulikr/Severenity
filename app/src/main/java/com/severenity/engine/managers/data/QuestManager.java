package com.severenity.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.severenity.App;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.GamePlace;
import com.severenity.entity.quest.CaptureQuest;
import com.severenity.entity.quest.CollectQuest;
import com.severenity.entity.quest.DistanceQuest;
import com.severenity.entity.quest.Quest;
import com.severenity.utils.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_CHARACTERISTIC;
import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_CHARACTERISTIC_AMOUNT;
import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_CREDITS_AMOUNT;
import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_DESCRIPTION;
import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_DISTANCE;
import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_EXPIRATION_TIME;
import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_EXP_AMOUNT;
import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_ID;
import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_PLACE_TYPE;
import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_PLACE_TYPE_VALUE;
import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_PROGRESS;
import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_STATUS;
import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_TITLE;
import static com.severenity.entity.contracts.QuestContract.DBQuest.COLUMN_TYPE;
import static com.severenity.entity.contracts.QuestContract.DBQuest.TABLE_QUESTS;

/**
 * Class is responsible for managing quests access / logic between database and other modules.
 *
 * Created by Novosad on 5/9/16.
 */
public class QuestManager extends DataManager {
    public QuestManager(Context context) {
        super(context);
    }

    /**
     * Adds list of quests to the database.
     *
     * @param quests - list of {@link Quest} objects to add.
     */
    private void addQuests(ArrayList<Quest> quests) {
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

    /**
     * Deletes quest from the local database.
     *
     * @param quest - {@link Quest} object to delete.
     */
    public void deleteQuest(Quest quest) {
        checkIfNull(quest);

        deleteQuestById(quest.getId());
    }

    /**
     * Removes quest from the local database based on uuid.
     *
     * @param id - uuid of the quest
     */
    private void deleteQuestById(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_QUESTS, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * Creates {@link ContentValues} from {@link Quest} object.
     * @param quest - {@link Quest} quest object to create values from.
     * @return {@link ContentValues} instance.
     */
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
        values.put(COLUMN_PROGRESS, quest.getProgress());

        if (quest.getType() == Quest.QuestType.Distance) {
            values.put(COLUMN_DISTANCE, ((DistanceQuest) quest).getDistance());
        } else if (quest.getType() == Quest.QuestType.Capture) {
            values.put(COLUMN_PLACE_TYPE, ((CaptureQuest) quest).getPlaceType().ordinal());
            values.put(COLUMN_PLACE_TYPE_VALUE, ((CaptureQuest) quest).getPlaceTypeValue());
        } else if (quest.getType() == Quest.QuestType.Collect) {
            values.put(COLUMN_CHARACTERISTIC, ((CollectQuest) quest).getCharacteristic().ordinal());
            values.put(COLUMN_CHARACTERISTIC_AMOUNT, ((CollectQuest) quest).getAmount());
        }

        return values;
    }

    /**
     * Adds quest to the database.
     *
     * @param quest - {@link Quest} object to add.
     * @return true if added, false otherwise.
     */
    private boolean addQuest(Quest quest) {
        Quest q = getQuest(quest);
        if (q != null) {
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long success = db.insert(TABLE_QUESTS, "NULL", createValuesFrom(quest));
        db.close();

        return success != -1;
    }

    /**
     * Finds quest in the database depending on the id.
     *
     * @param id - id of the quest to find.
     * @return {@link Quest} object if found, null otherwise.
     */
    public Quest getQuestById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_QUESTS,
                null,
                "id = '" + id + "'",
                null, null, null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            Quest quest = getQuestFromCursor(cursor);

            if (quest.getType() == Quest.QuestType.Distance) {
                quest = new DistanceQuest(quest, cursor.getInt(cursor.getColumnIndex(COLUMN_DISTANCE)));
            } else if (quest.getType() == Quest.QuestType.Capture) {
                quest = new CaptureQuest(quest,
                        GamePlace.PlaceType.values()[cursor.getInt(cursor.getColumnIndex(COLUMN_PLACE_TYPE))],
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

    /**
     * Transforms cursor data to {@link Quest} object.
     *
     * @param cursor - cursor pointing to the row.
     * @return {@link Quest} object created of the data.
     */
    private Quest getQuestFromCursor(Cursor cursor) {
        Quest quest = new Quest();
        quest.setId(cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
        quest.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
        quest.setType(Quest.QuestType.values()[cursor.getInt(cursor.getColumnIndex(COLUMN_TYPE))]);
        quest.setStatus(Quest.QuestStatus.values()[cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS))]);
        quest.setExperience(cursor.getLong(cursor.getColumnIndex(COLUMN_EXP_AMOUNT)));
        quest.setCredits(cursor.getLong(cursor.getColumnIndex(COLUMN_CREDITS_AMOUNT)));
        quest.setProgress(cursor.getInt(cursor.getColumnIndex(COLUMN_PROGRESS)));

        String expirationTime = cursor.getString(cursor.getColumnIndex(COLUMN_EXPIRATION_TIME));
        if (expirationTime != null && !expirationTime.equals("null")) {
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
    private Quest getQuest(Quest quest) {
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
                                GamePlace.PlaceType.values()[cursor.getInt(cursor.getColumnIndex(COLUMN_PLACE_TYPE))],
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
            JSONObject request = new JSONObject();
            request.put("questId", quest.getId());
            request.put("reason", "accepted");

            updateQuestWithData(App.getUserManager().getCurrentUser().getId(), request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates status of quest if found and populates it to UI via intent.
     *
     * @param questId - quest to update
     * @param status  - new {@link com.severenity.entity.quest.Quest.QuestStatus}. None < New < Progress < Finished.
     */
    private void updateQuestStatusAndPopulate(String questId, int status) {
        Quest quest = getQuestById(questId);

        if (quest == null) {
            Log.e(Constants.TAG, "Quest with id " + questId + " not found.");
            return;
        }

        if (status > quest.getStatus().ordinal()) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_STATUS, status);
            if (status == 1 || status == 2) {
                values.put(COLUMN_PROGRESS, 0);
            }
            db.update(TABLE_QUESTS, values, "id = '" + questId + "'", null);
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
        intent.putExtra(COLUMN_PROGRESS, quest.getProgress());

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
    private Quest getQuestFromJSON(JSONObject questObj) {
        Quest quest = new Quest();
        try {
            int questType = questObj.getInt("type");
            String id = questObj.getString("questId");

            Quest existingQuest = getQuestById(id);
            if (existingQuest != null) {
                return existingQuest;
            }

            quest.setId(id);
            quest.setTitle(questObj.getString("title"));
            quest.setCredits(questObj.getLong("credits"));
            quest.setExperience(questObj.getLong("experience"));
            quest.setStatus(Quest.QuestStatus.values()[questObj.getInt("status")]);

            if (quest.getStatus() == Quest.QuestStatus.Finished || quest.getStatus() == Quest.QuestStatus.Closed) {
                quest.setExpirationTime("null");
            } else {
                quest.setExpirationTime(questObj.getString("expirationDate"));
            }

            quest.setProgress(questObj.getJSONObject("progress").getInt("progress"));

            if (questType == Quest.QuestType.Distance.ordinal()) {
                quest.setType(Quest.QuestType.Distance);
                quest = new DistanceQuest(quest, questObj.getInt("distance"));
            } else if (questType == Quest.QuestType.Capture.ordinal()) {
                quest.setType(Quest.QuestType.Capture);
                quest = new CaptureQuest(quest, GamePlace.PlaceType.values()[questObj.getInt("placeType")], questObj.getInt("placeTypeValue"));
            } else if (questType == Quest.QuestType.Collect.ordinal()) {
                quest.setType(Quest.QuestType.Collect);
                quest = new CollectQuest(quest, Constants.Characteristic.values()[questObj.getInt("characteristic")], questObj.getInt("characteristicAmount"));
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
        getQuestsFromServer(App.getUserManager().getCurrentUser().getId());
    }

    /**
     * Used to update quests progress whenever user action has happened.
     */
    public void updateQuestProgress(String... newValues) {
        try {
            JSONObject request = new JSONObject();
            JSONObject data = new JSONObject();

            data.put("objective", newValues[0]);
            data.put("value", Integer.valueOf(newValues[1]));

            request.put("reason", "progress");
            request.put("data", data);

            updateQuestWithData(App.getUserManager().getCurrentUser().getId(), request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates quest entry in local DB with new progress value.
     *
     * @param questId - quest to update.
     * @param value - new progress value to set.
     */
    private void updateQuestProgressLocally(long questId, int value, int status) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PROGRESS, value);
            values.put(COLUMN_STATUS, status);
            if (status == Quest.QuestStatus.Finished.ordinal() ||
                    status == Quest.QuestStatus.Closed.ordinal()) {
                values.put(COLUMN_EXPIRATION_TIME, "null");
            }
            db.update(TABLE_QUESTS, values, "id = '" + questId + "'", null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Constants.INTENT_FILTER_QUEST_UPDATE);
        intent.putExtra("questId", questId);
        intent.putExtra("progress", value);
        intent.putExtra("status", status);
        App.getLocalBroadcastManager().sendBroadcast(intent);
    }

    /**
     * Retrieve quests from server based on user id.
     *
     * @param userId - quests of this users will be retrieved.
     */
    private void getQuestsFromServer(String userId) {
        deleteQuests();
        String request = Constants.REST_API_USERS + "/" + userId + Constants.REST_API_QUESTS;
        App.getRestManager().createRequest(request, Request.Method.GET, null, new RequestCallback() {
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
                        questsList.add(getQuestFromJSON(questObject));
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
     * Retrieve team quests from server.
     */
    public void getTeamQuestsFromServer() {
        String request = Constants.HOST + Constants.REST_API_QUESTS + "/teams";
        App.getRestManager().createRequest(request, Request.Method.GET, null, new RequestCallback() {
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
                        questsList.add(getQuestFromJSON(questObject));
                    }

                    Log.e(Constants.TAG, questsList.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorCallback(NetworkResponse response) {
                if (response != null) {
                    Log.e(Constants.TAG, "Error response for team quests retrieve: " + response.toString());
                } else {
                    Log.e(Constants.TAG, "Error response for team quests retrieve is null.");
                }
            }
        });
    }

    /**
     * Updates quest(s) data on the server according to data specified and user that
     * has requested to update the quest(s).
     *
     * @param userId - id of the user which quest should be updated.
     * @param data - data to be sent together with quest update request for user.
     */
    private void updateQuestWithData(String userId, JSONObject data) {
        String request = Constants.REST_API_USERS + "/" + userId + Constants.REST_API_QUESTS_UPDATE;
        App.getRestManager().createRequest(request, Request.Method.POST, data, new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                if (response == null) {
                    Log.e(Constants.TAG, "Quest update response is null.");
                    return;
                }

                try {
                    if (!response.getString("result").equalsIgnoreCase("success")) {
                        Log.e(Constants.TAG, "Quest update result is error.");
                        return;
                    }

                    String reason = response.getString("reason");
                    JSONObject data = response.getJSONObject("data");
                    switch (reason) {
                        case "accepted": {
                            int status = data.getInt("status");
                            String questId = data.getString("questId");

                            updateQuestStatusAndPopulate(questId, status);
                        } break;
                        case "progress": {
                            Log.d(Constants.TAG, response.toString());
                            JSONArray quests = response.getJSONObject("data").getJSONArray("quests");
                            for (int i = 0; i < quests.length(); i++) {
                                JSONObject quest = quests.getJSONObject(i);
                                JSONObject progress = quest.getJSONObject("progress");
                                int value = progress.getInt("progress");
                                long questId = quest.getLong("questId");
                                int status = quest.getInt("status");

                                updateQuestProgressLocally(questId, value, status);
                            }
                        } break;
                        default:
                            Log.e(Constants.TAG, "Unknown quest update reason: " + reason);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorCallback(NetworkResponse response) {
                if (response != null) {
                    Log.e(Constants.TAG, "Update quest error response is: " + response.toString());
                } else {
                    Log.e(Constants.TAG, "Update quest error response is null");
                }
            }
        });
    }

    /**
     * Finds quest object in specific intent received from notification.
     *
     * @param intent - intent containing quest.
     * @return - {@link Quest} object.
     */
    public Quest getQuestFromIntent(Intent intent) {
        Quest quest = new Quest();
        Quest.QuestType type = Quest.QuestType.values()[intent.getIntExtra("type", 0)];
        quest.setId(intent.getStringExtra("id"));
        quest.setType(type);
        quest.setTitle(intent.getStringExtra("title"));
        quest.setStatus(Quest.QuestStatus.values()[intent.getIntExtra("status", 0)]);
        quest.setExpirationTime(intent.getStringExtra("expirationTime"));
        quest.setCredits(intent.getLongExtra("credits", 0));
        quest.setExperience(intent.getLongExtra("experience", 0));

        if (type == Quest.QuestType.Distance) {
            quest = new DistanceQuest(quest, intent.getIntExtra("distance", 1));
        } else if (type == Quest.QuestType.Capture) {
            quest = new CaptureQuest(quest, GamePlace.PlaceType.values()[intent.getIntExtra("placeType", 0)], intent.getIntExtra("placeTypeValue", 0));
        } else if (type == Quest.QuestType.Collect) {
            String characteristic = intent.getStringExtra("characteristic");
            Constants.Characteristic c = Constants.Characteristic.None;
            if (characteristic.equals(Constants.Characteristic.Level.toString())) {
                c = Constants.Characteristic.Level;
            } else if (characteristic.equals(Constants.Characteristic.Experience.toString())) {
                c = Constants.Characteristic.Experience;
            } else if (characteristic.equals(Constants.Characteristic.Energy.toString())) {
                c = Constants.Characteristic.Energy;
            } else if (characteristic.equals(Constants.Characteristic.Immunity.toString())) {
                c = Constants.Characteristic.Immunity;
            }

            quest = new CollectQuest(quest, c, intent.getIntExtra("characteristicAmount", 0));
        }

        return quest;
    }
}
