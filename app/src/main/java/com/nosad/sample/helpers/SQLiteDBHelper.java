package com.nosad.sample.helpers;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nosad.sample.entity.contracts.MsgContract;
import com.nosad.sample.entity.contracts.QuestContract;
import com.nosad.sample.entity.contracts.UserContract;

/**
 * Created by Novosad on 2/17/16.
 */
public class SQLiteDBHelper extends SQLiteOpenHelper {
    // 1 - UserName table created
    // 2 - added Message table
    // 3 - added Quests table
    private static final int DB_VERSION = 3;
    private static final String DB_NAME = "Filter.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER ";
    private static final String COMMA_SEP = ",";

    private static final String DB_SQL_CREATE_USERS =
            "CREATE TABLE " + UserContract.DBUser.TABLE_USERS + " (" +
                    UserContract.DBUser._ID + " INTEGER PRIMARY KEY," +
                    UserContract.DBUser.COLUMN_ID + TEXT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_DISTANCE + INT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_EXPERIENCE + INT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_LEVEL + INT_TYPE +
                    " )";

    private static final String DB_SQL_CREATE_MESSAGES =
            "CREATE TABLE " + MsgContract.DBMsg.TABLE_MESSAGE + " (" +
                    MsgContract.DBMsg._ID + " INTEGER PRIMARY KEY," +
                    MsgContract.DBMsg.COLUMN_USER_ID   + TEXT_TYPE + COMMA_SEP +
                    MsgContract.DBMsg.COLUMN_MESSAGE   + TEXT_TYPE + COMMA_SEP +
                    MsgContract.DBMsg.COLUMN_USER_NAME + TEXT_TYPE + COMMA_SEP +
                    MsgContract.DBMsg.COLUMN_TIMESTAMP + TEXT_TYPE +
                    " )";

    private static final String DB_SQL_CREATE_QUESTS =
            "CREATE TABLE " + QuestContract.DBQuest.TABLE_QUESTS + " (" +
                    QuestContract.DBQuest._ID + " INTEGER PRIMARY KEY," +
                    QuestContract.DBQuest.COLUMN_ID   + INT_TYPE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_TITLE   + TEXT_TYPE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_DESCRIPTION + TEXT_TYPE +
                    " )";

    private static final String DB_SQL_DELETE_USERS = "DROP TABLE IF EXISTS " + UserContract.DBUser.TABLE_USERS;

    private static final String DB_SQL_DELETE_MESSAGES = "DROP TABLE IF EXISTS " + MsgContract.DBMsg.TABLE_MESSAGE;

    private static final String DB_SQL_DELETE_QUESTS = "DROP TABLE IF EXISTS " + QuestContract.DBQuest.TABLE_QUESTS;

    public SQLiteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public SQLiteDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TEMP: clearing on creation
        db.execSQL(DB_SQL_CREATE_USERS);
        db.execSQL(DB_SQL_CREATE_MESSAGES);
        db.execSQL(DB_SQL_CREATE_QUESTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL(DB_SQL_CREATE_MESSAGES);
        }

        if (oldVersion == 2 && newVersion == DB_VERSION) {
            db.execSQL(DB_SQL_CREATE_QUESTS);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
