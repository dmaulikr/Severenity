package com.nosad.sample.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.nosad.sample.App;
import com.nosad.sample.entity.quest.Quest;
import com.nosad.sample.utils.common.Constants;

import java.util.ArrayList;

import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_DESCRIPTION;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_EXP_AMOUNT;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_CREDITS_AMOUNT;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_ID;
import static com.nosad.sample.entity.contracts.QuestContract.DBQuest.COLUMN_TITLE;
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

    public boolean addQuest(Quest quest) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, quest.getId());
        values.put(COLUMN_TITLE, quest.getTitle());
        values.put(COLUMN_DESCRIPTION, quest.getDescription());
        values.put(COLUMN_EXP_AMOUNT, quest.getExperience());
        values.put(COLUMN_CREDITS_AMOUNT, quest.getCredits());

        long success = db.insert(TABLE_QUESTS, "NULL", values);
        db.close();

        return success != -1;
    }

    public ArrayList<Quest> getQuests() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;
        try {
            cursor = db.query(
                    TABLE_QUESTS,
                    new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_DESCRIPTION, COLUMN_EXP_AMOUNT, COLUMN_CREDITS_AMOUNT},
                    null,
                    null,
                    null, null, null, null
            );
        } catch (SQLException e){
            return null;
        }

        if (cursor.getCount() == 0) {
            return null;
        }

        ArrayList<Quest> questsList = new ArrayList<>(cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                Quest quest = new Quest();
                quest.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                quest.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                quest.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                quest.setExperience(cursor.getLong(cursor.getColumnIndex(COLUMN_EXP_AMOUNT)));
                quest.setCredits(cursor.getLong(cursor.getColumnIndex(COLUMN_CREDITS_AMOUNT)));

                questsList.add(quest);
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

        addQuest(quest);

        Intent intent = new Intent(Constants.INTENT_FILTER_NEW_QUEST);
        intent.putExtra(COLUMN_ID, quest.getId());
        intent.putExtra(COLUMN_TITLE, quest.getTitle());
        intent.putExtra(COLUMN_DESCRIPTION, quest.getDescription());
        intent.putExtra(COLUMN_EXP_AMOUNT, quest.getExperience());
        intent.putExtra(COLUMN_CREDITS_AMOUNT, quest.getCredits());

        App.getLocalBroadcastManager().sendBroadcast(intent);
    }
}
