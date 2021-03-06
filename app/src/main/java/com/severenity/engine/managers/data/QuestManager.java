package com.severenity.engine.managers.data;

import android.content.Context;
import android.content.Intent;
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
import com.severenity.entity.user.UserQuest;
import com.severenity.utils.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Class is responsible for managing quests access / logic between database and other modules.
 *
 * Created by Novosad on 5/9/16.
 */
public class QuestManager extends DataManager {
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_EXP_AMOUNT = "experience";
    private static final String COLUMN_CREDITS_AMOUNT = "credits";
    private static final String COLUMN_EXPIRATION_TIME = "expirationTime";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_DISTANCE = "distance";
    private static final String COLUMN_PLACE_TYPE = "placeType";
    private static final String COLUMN_PLACE_TYPE_VALUE = "placeTypeValue";
    private static final String COLUMN_CHARACTERISTIC = "characteristic";
    private static final String COLUMN_CHARACTERISTIC_AMOUNT = "characteristicAmount";
    private static final String COLUMN_PROGRESS = "progress";

    public QuestManager(Context context) {
        super(context);
    }

    /**
     * Adds list of quests to the database.
     *
     * @param quests - json array list of {@link Quest} objects to add.
     */
    private void addQuests(final JSONArray quests) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createOrUpdateAllFromJson(Quest.class, quests);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
            }
        });
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
    private void deleteQuestById(final String id) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Quest> results = realm.where(Quest.class).equalTo("id", id).findAll();
                results.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
            }
        });
    }

    /**
     * Adds quest to the database.
     *
     * @param quest - {@link Quest} object to add.
     * @return true if quest added, false otherwise.
     */
    private boolean addQuest(final Quest quest) {
        Quest q = getQuest(quest);
        if (q != null) {
            return false;
        }

        try {
            final Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(quest);
                }
            });
            realm.close();

            return true;
        } catch (IllegalArgumentException | RealmMigrationNeededException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Finds quest in the database depending on the id.
     *
     * @param id - id of the quest to find.
     * @return {@link Quest} object if found, null otherwise.
     */
    public Quest getQuestById(String id) {
        Realm realm = Realm.getDefaultInstance();
        Quest quest = realm.where(Quest.class).equalTo("id", id).findFirst();
        realm.close();

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
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<Quest> results = realm.where(Quest.class).findAll();
        List<Quest> questList = realm.copyFromRealm(results);
        realm.close();

        List<UserQuest> userQuests = App.getUserManager().getCurrentUser().getQuests();

        if (questList.isEmpty() && userQuests.isEmpty()) {
            return new ArrayList<>(questList);
        }

        // Update each quest progress from user quests.
        for (Quest quest : questList) {
            for (UserQuest userQuest : userQuests) {
                if (quest.getId().equalsIgnoreCase(userQuest.getId())) {
                    quest.setProgress(userQuest.getProgress().getProgress());
                }
            }
        }

        return new ArrayList<>(questList);
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
            request.put("id", quest.getId());
            request.put("reason", "accepted");

            updateQuestWithData(App.getUserManager().getCurrentUser().getId(), request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates status of quest if found and populates it to UI via intent.
     *
     * @param id - quest to update
     * @param status  - new {@link com.severenity.entity.quest.Quest.QuestStatus}. None < New < Progress < Finished.
     */
    private void updateQuestStatusAndPopulate(final String id, final int status) {
        final Quest quest = getQuestById(id);

        if (quest == null) {
            Log.e(Constants.TAG, "Quest with id " + id + " not found.");
            return;
        }

        if (status > quest.getStatus().ordinal()) {
            final Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Quest quest = realm.where(Quest.class).equalTo("id", id).findFirst();
                    quest.setStatus(Quest.QuestStatus.values()[status]);

                    if (status == 1 || status == 2) {
                        quest.setProgress(0);
                    }

                    realm.copyToRealmOrUpdate(quest);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    populateQuest(quest);
                    realm.close();
                }
            });
        } else {
            Log.e(Constants.TAG, "New quest status is incorrect for quest " + id);
        }
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
            intent.putExtra(COLUMN_DISTANCE, quest.getDistanceQuest().getDistance());
        }

        if (quest.getType() == Quest.QuestType.Capture) {
            intent.putExtra(COLUMN_TYPE, Quest.QuestType.Capture.ordinal());
            intent.putExtra(COLUMN_PLACE_TYPE, quest.getCaptureQuest().getPlaceType());
            intent.putExtra(COLUMN_PLACE_TYPE_VALUE, quest.getCaptureQuest().getPlaceTypeValue());
        }

        if (quest.getType() == Quest.QuestType.Collect) {
            intent.putExtra(COLUMN_TYPE, Quest.QuestType.Collect.ordinal());
            intent.putExtra(COLUMN_CHARACTERISTIC, quest.getCollectQuest().getCharacteristic().ordinal());
            intent.putExtra(COLUMN_CHARACTERISTIC_AMOUNT, quest.getCollectQuest().getAmount());
        }

        App.getLocalBroadcastManager().sendBroadcast(intent);
    }


    /**
     * Deletes all quests from the local database.
     */
    private void deleteQuests() {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Quest> results = realm.where(Quest.class).findAll();
                results.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
            }
        });
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
     * @param id - quest to update.
     * @param value - new progress value to set.
     */
    private void updateQuestProgressLocally(final String id, final int value, final int status) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Quest quest = realm.where(Quest.class).equalTo("id", id).findFirst();

                quest.setProgress(value);
                quest.setStatus(Quest.QuestStatus.values()[status]);

                if (status == Quest.QuestStatus.Finished.ordinal() ||
                        status == Quest.QuestStatus.Closed.ordinal()) {
                    quest.setExpirationTime("null");
                }
                realm.copyToRealmOrUpdate(quest);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(Constants.INTENT_FILTER_QUEST_UPDATE);
                intent.putExtra("id", id);
                intent.putExtra("progress", value);
                intent.putExtra("status", status);
                App.getLocalBroadcastManager().sendBroadcast(intent);

                realm.close();
            }
        });
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
                    if (!"success".equalsIgnoreCase(response.getString("result"))) {
                        return;
                    }

                    JSONArray quests = response.getJSONArray("data");
                    addQuests(quests);

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
     * Retrieves initial quest for the player if player was just registered.
     */
    public void getInitialQuest() {
        if (getQuestById("0") != null) {
            return;
        }

        String request = Constants.REST_API_USERS + "/" + App.getUserManager().getCurrentUser().getId() + Constants.REST_API_QUESTS + "/0";
        App.getRestManager().createRequest(request, Request.Method.GET, null, new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                if (response.length() == 0) {
                    return;
                }

                populateQuest(getQuestFromJSON(response));
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
     * Parses JSON object and returns {@link Quest} object.
     *
     * @param questObj - JSON object to retrieve quest from.
     * @return {@link Quest} object.
     */
    private Quest getQuestFromJSON(JSONObject questObj) {
        Quest quest = new Quest();
        try {
            int questType = Integer.valueOf(questObj.getString("type"));
            String id = questObj.getString("id");

            Quest existingQuest = getQuestById(id);
            if (existingQuest != null) {
                return existingQuest;
            }

            quest.setId(id);
            quest.setTitle(questObj.getString("title"));
            quest.setCredits(Integer.valueOf(questObj.getString("credits")));
            quest.setExperience(Integer.valueOf(questObj.getString("experience")));
            quest.setStatus(Quest.QuestStatus.values()[Integer.valueOf(questObj.getString("status"))]);

            if (quest.getStatus() == Quest.QuestStatus.Finished || quest.getStatus() == Quest.QuestStatus.Closed) {
                quest.setExpirationTime("null");
                quest.setProgress(100);
            } else {
                if (quest.getStatus() != Quest.QuestStatus.Created) {
                    quest.setProgress(Integer.valueOf(questObj.getJSONObject("progress").getString("progress")));
                }
                quest.setExpirationTime(questObj.getString("expirationDate"));
            }

            if (questType == Quest.QuestType.Distance.ordinal()) {
                quest.setType(Quest.QuestType.Distance);
                quest.setDistanceQuest(new DistanceQuest(quest, Integer.valueOf(questObj.getJSONObject("distanceQuest").getString("distance"))));
            } else if (questType == Quest.QuestType.Capture.ordinal()) {
                quest.setType(Quest.QuestType.Capture);
                quest.setCaptureQuest(new CaptureQuest(quest, GamePlace.PlaceType.values()[Integer.valueOf(questObj.getJSONObject("captureQuest").getString("placeType"))], Integer.valueOf(questObj.getJSONObject("captureQuest").getString("placeTypeValue"))));
            } else if (questType == Quest.QuestType.Collect.ordinal()) {
                quest.setType(Quest.QuestType.Collect);
                quest.setCollectQuest(new CollectQuest(quest, Constants.Characteristic.values()[Integer.valueOf(questObj.getJSONObject("collectQuest").getString("characteristic"))], Integer.valueOf(questObj.getJSONObject("collectQuest").getString("characteristicAmount"))));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return quest;
    }


    /**
     * Retrieve team quests from server.
     */
    public void getTeamQuestsFromServer() {
        String request = Constants.HOST + Constants.REST_API_QUESTS + "/teams/" + App.getUserManager().getCurrentUser().getTeamId() + "?leaderId=" + App.getUserManager().getCurrentUser().getId();
        App.getRestManager().createRequest(request, Request.Method.GET, null, new RequestCallback() {
            @Override
            public void onResponseCallback(JSONObject response) {
                try {
                    if (!response.getString("result").equalsIgnoreCase("success")) {
                        return;
                    }

                    JSONArray quests = response.getJSONArray("data");
                    addQuests(quests);

                    Log.e(Constants.TAG, quests.toString());
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
                    if (!"success".equalsIgnoreCase(response.getString("result"))) {
                        Log.e(Constants.TAG, "Quest update result is error.");
                        return;
                    }

                    String reason = response.getString("reason");
                    JSONObject data = response.getJSONObject("data");
                    switch (reason) {
                        case "accepted": {
                            int status = data.getInt("status");
                            String id = data.getString("id");

                            updateQuestStatusAndPopulate(id, status);
                        } break;
                        case "progress": {
                            Log.d(Constants.TAG, response.toString());
                            JSONArray quests = data.getJSONArray("quests");
                            for (int i = 0; i < quests.length(); i++) {
                                JSONObject quest = quests.getJSONObject(i);
                                JSONObject progress = quest.getJSONObject("progress");
                                int value = progress.getInt("progress");
                                String id = quest.getString("id");
                                int status = quest.getInt("status");

                                updateQuestProgressLocally(id, value, status);
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
            quest.setDistanceQuest(new DistanceQuest(quest, intent.getIntExtra("distance", 1)));
        } else if (type == Quest.QuestType.Capture) {
            quest.setCaptureQuest(new CaptureQuest(quest, GamePlace.PlaceType.values()[intent.getIntExtra("placeType", 0)], intent.getIntExtra("placeTypeValue", 0)));
        } else if (type == Quest.QuestType.Collect) {
            String characteristic = intent.getStringExtra("characteristic");
            Constants.Characteristic c = Constants.Characteristic.None;
            if (characteristic.equals(Constants.Characteristic.Level.toString())) {
                c = Constants.Characteristic.Level;
            } else if (characteristic.equals(Constants.Characteristic.Experience.toString())) {
                c = Constants.Characteristic.Experience;
            } else if (characteristic.equals(Constants.Characteristic.Energy.toString())) {
                c = Constants.Characteristic.Energy;
            }

            quest.setCollectQuest(new CollectQuest(quest, c, intent.getIntExtra("characteristicAmount", 0)));
        }

        return quest;
    }
}
