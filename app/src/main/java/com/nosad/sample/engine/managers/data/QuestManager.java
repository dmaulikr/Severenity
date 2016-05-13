package com.nosad.sample.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.nosad.sample.App;
import com.nosad.sample.entity.quest.CaptureQuest;
import com.nosad.sample.entity.quest.CollectQuest;
import com.nosad.sample.entity.quest.DistanceQuest;
import com.nosad.sample.entity.quest.Quest;
import com.nosad.sample.utils.common.Constants;

import java.util.ArrayList;
import java.util.List;

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

    public void addQuests(List<Quest> quests) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            db.beginTransaction();
            for (Quest quest : quests) {
                db.insert(TABLE_QUESTS, "NULL", createValuesFrom(quest));
            }
            db.setTransactionSuccessful();
        } finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
        }

        db.close();
    }

    public void deleteQuest(Quest quest) {
        checkIfNull(quest);

        deleteQuestById(quest.getId());
    }

    public void deleteQuestById(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_QUESTS, "id = ?", new String[]{ String.valueOf(id) });
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
                new String[] {
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

    public Quest getQuest(Quest quest) {
        checkIfNull(quest);

        return getQuestById(quest.getId());
    }

    public ArrayList<Quest> getQuests() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;
        try {
            cursor = db.query(
                TABLE_QUESTS,
                new String[] {
                    COLUMN_ID, COLUMN_TITLE, COLUMN_DESCRIPTION,
                    COLUMN_EXP_AMOUNT, COLUMN_CREDITS_AMOUNT, COLUMN_EXPIRATION_TIME,
                    COLUMN_STATUS, COLUMN_TYPE, COLUMN_DISTANCE,
                    COLUMN_PLACE_TYPE, COLUMN_PLACE_TYPE_VALUE,
                    COLUMN_CHARACTERISTIC, COLUMN_CHARACTERISTIC_AMOUNT
                },
                null,
                null,
                null, null, null, null
            );
        } catch (SQLException e) {
            return null;
        }

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

        cursor.close();
        db.close();
        return questsList;
    }

    public void onQuestReceived(Quest quest) {
        if (quest == null) {
            return;
        }

        if (!addQuest(quest)) {
            return;
        }

        Intent intent = new Intent(Constants.INTENT_FILTER_NEW_QUEST);
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
}
