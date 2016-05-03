package com.nosad.sample.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.nosad.sample.App;
import com.nosad.sample.entity.Message;
import com.nosad.sample.entity.User;
import com.nosad.sample.utils.common.Constants;

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

    public boolean addMessage(Message msg) {

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

    public ArrayList<Message> getMessages() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_MESSAGE,
                    new String[]{COLUMN_USER_ID, COLUMN_MESSAGE, COLUMN_TIMESTAMP, COLUMN_USER_NAME},
                    null,
                    null,
                    null, null, null, null
            );
        }catch (SQLException e){
            return null;
        };

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

        cursor.close();
        db.close();
        return messagesList;
    }

    public boolean sendMessage(Message msg) {

        if (msg == null) {
            return false;
        }

        if (addMessage(msg)) {

            App.getWebSocketManager().sendMessageToServer(msg);
            return true;
        }

        return false;
    }

    public void onMessageRetrieved(Message msg) {

        if (msg == null) {
            return;
        }

        User user = App.getUserManager().getCurrentUser();
        if (user != null && msg.getUserID().equals(user.getId()))
            return;
        else
            addMessage(msg);

        Intent intent = new Intent(Constants.INTENT_FILTER_NEW_MESSAGE);
        Bundle extras = intent.getExtras();
        extras.putString(COLUMN_MESSAGE, msg.getMessage());
        extras.putString(COLUMN_TIMESTAMP, msg.getTimestamp());
        extras.putString(COLUMN_USER_ID, msg.getUserID());
        extras.putString(COLUMN_USER_NAME, msg.getUserName());

        App.getLocalBroadcastManager().sendBroadcast(intent);
    }
}
