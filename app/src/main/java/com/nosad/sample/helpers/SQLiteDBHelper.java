package com.nosad.sample.helpers;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nosad.sample.entity.contracts.UserContract;
import com.nosad.sample.utils.common.Constants;

/**
 * Created by Novosad on 2/17/16.
 */
public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Sample.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String DB_SQL_CREATE_USERS =
            "CREATE TABLE " + UserContract.DBUser.TABLE_USERS + " (" +
                    UserContract.DBUser._ID + " INTEGER PRIMARY KEY," +
                    UserContract.DBUser.COLUMN_ID + TEXT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_STEPS + " INTEGER " +
                    " )";

    private static final String DB_SQL_DELETE_USERS = "DROP TABLE IF EXISTS " + UserContract.DBUser.TABLE_USERS;

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DB_SQL_DELETE_USERS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
