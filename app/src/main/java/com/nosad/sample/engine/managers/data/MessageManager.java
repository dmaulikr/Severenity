package com.nosad.sample.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nosad.sample.entity.Message;

import java.util.ArrayList;

import static com.nosad.sample.entity.contracts.MsgContract.DBMsg.TABLE_MESSAGE;
import static com.nosad.sample.entity.contracts.MsgContract.DBMsg.COLUMN_USER_ID;
import static com.nosad.sample.entity.contracts.MsgContract.DBMsg.COLUMN_USER_NAME;
import static com.nosad.sample.entity.contracts.MsgContract.DBMsg.COLUMN_MESSAGE;
import static com.nosad.sample.entity.contracts.MsgContract.DBMsg.COLUMN_TIMESTAMP;


/**
 * Created by Andriy on 4/27/2016.
 */
public class MessageManager extends DataManager {

    public MessageManager(Context context) {
        super(context);
    }

    public boolean AddMessage(Message msg) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID,   msg.getUserID());
        values.put(COLUMN_MESSAGE,   msg.getMessage());
        values.put(COLUMN_TIMESTAMP, msg.getTimestamp());
        values.put(COLUMN_USER_NAME, msg.getUserName());

        long success = db.insert(TABLE_MESSAGE, "NULL", values);
        db.close();

        return success == -1 ? false : true;
    };

    public ArrayList<Message> GetMessages() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_MESSAGE,
                new String[]{COLUMN_USER_ID, COLUMN_MESSAGE, COLUMN_TIMESTAMP, COLUMN_USER_NAME},
                null,
                null,
                null, null, null, null
        );

        if (cursor.getCount() == 0) return null;

        ArrayList<Message> messagesList = new ArrayList<>(cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setUserID(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
                message.setUserName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                message.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
                message.setMessage(cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE)));

                messagesList.add(message);
            }
            while (cursor.moveToNext());
        }
        return messagesList;
    }
}
