package com.severenity.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.severenity.entity.contracts.MsgContract;
import com.severenity.entity.contracts.PlaceContract;
import com.severenity.entity.contracts.QuestContract;
import com.severenity.entity.contracts.RecoveryContract;
import com.severenity.entity.contracts.UserContract;
import com.severenity.utils.common.Constants;

/**
 * Created by Novosad on 2/17/16.
 */
public class SQLiteDBHelper extends SQLiteOpenHelper {
    // 1 - UserName table created
    // 2 - added Message table
    // 3 - added Quests table
    // 4 - added place/owners tables
    // 5 - added recovery info table
    // 6 - added max implant hp column
    // 7 - added team column into users tanle
    private static final int DB_VERSION = 7;
    private static final String DB_NAME = "Filter.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER ";
    private static final String REAL_TYPE = " REAL ";
    private static final String COMMA_SEP = ",";

    private static final String DB_SQL_CREATE_USERS =
            "CREATE TABLE " + UserContract.DBUser.TABLE_USERS + " (" +
                    UserContract.DBUser._ID + " INTEGER PRIMARY KEY," +
                    UserContract.DBUser.COLUMN_ID + TEXT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_EMAIL + TEXT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_DISTANCE + INT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_EXPERIENCE + INT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_IMMUNITY + INT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_MAX_IMMUNITY + INT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_ENERGY + INT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_MAX_ENERGY + INT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_IMPLANT_HP + INT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_MAX_IMPLANT_HP + INT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_CREATED_DATE + TEXT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_CREDITS + INT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_VIEW_RADIUS + REAL_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_ACTION_RADIUS + REAL_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_TEAM + TEXT_TYPE + COMMA_SEP +
                    UserContract.DBUser.COLUMN_LEVEL + INT_TYPE + " )";

    private static final String DB_SQL_CREATE_MESSAGES =
            "CREATE TABLE " + MsgContract.DBMsg.TABLE_MESSAGE + " (" +
                    MsgContract.DBMsg._ID + " INTEGER PRIMARY KEY," +
                    MsgContract.DBMsg.COLUMN_USER_ID   + TEXT_TYPE + COMMA_SEP +
                    MsgContract.DBMsg.COLUMN_MESSAGE   + TEXT_TYPE + COMMA_SEP +
                    MsgContract.DBMsg.COLUMN_USER_NAME + TEXT_TYPE + COMMA_SEP +
                    MsgContract.DBMsg.COLUMN_TIMESTAMP + TEXT_TYPE + " )";

    private static final String DB_SQL_CREATE_QUESTS =
            "CREATE TABLE " + QuestContract.DBQuest.TABLE_QUESTS + " (" +
                    QuestContract.DBQuest._ID + INT_TYPE + " PRIMARY KEY," +
                    QuestContract.DBQuest.COLUMN_ID + INT_TYPE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_EXP_AMOUNT + INT_TYPE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_CREDITS_AMOUNT + INT_TYPE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_STATUS + INT_TYPE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_TYPE + INT_TYPE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_EXPIRATION_TIME + TEXT_TYPE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_PROGRESS + INT_TYPE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_DISTANCE + INT_TYPE + QuestContract.DBQuest.COLUMN_NULLABLE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_PLACE_TYPE + TEXT_TYPE + QuestContract.DBQuest.COLUMN_NULLABLE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_PLACE_TYPE_VALUE + INT_TYPE + QuestContract.DBQuest.COLUMN_NULLABLE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_CHARACTERISTIC + INT_TYPE + QuestContract.DBQuest.COLUMN_NULLABLE + COMMA_SEP +
                    QuestContract.DBQuest.COLUMN_CHARACTERISTIC_AMOUNT + INT_TYPE + QuestContract.DBQuest.COLUMN_NULLABLE + " )";

    // !!! DO NOT CALL THIS STATEMENT DIRECTLY. Use createPlaces method instead
    // should be used in pare with DB_SQL_CREATE_PLACES_OWNERS
    private static final String DB_SQL_CREATE_PLACES =
            "CREATE TABLE " + PlaceContract.DBPlaces.TABLE_PLACES + " (" +
                    PlaceContract.DBPlaces._ID + INT_TYPE    + " PRIMARY KEY," +
                    PlaceContract.DBPlaces.COLUMN_PLACE_ID   + TEXT_TYPE + COMMA_SEP +
                    PlaceContract.DBPlaces.COLUMN_PLACE_NAME + TEXT_TYPE + COMMA_SEP +
                    PlaceContract.DBPlaces.COLUMN_PLACE_LNG  + REAL_TYPE + COMMA_SEP +
                    PlaceContract.DBPlaces.COLUMN_PLACE_LAT  + REAL_TYPE + COMMA_SEP +
                    PlaceContract.DBPlaces.COLUMN_PLACE_TYPE + INT_TYPE + " )";

    // !!! DO NOT CALL THIS STATEMENT DIRECTLY. Use createPlaces method instead
    // should be used in pare with DB_SQL_CREATE_PLACES
    private static final String DB_SQL_CREATE_PLACES_OWNERS =
            "CREATE TABLE " + PlaceContract.DBPlacesOwners.TABLE_PLACES_OWNERS + " (" +
                    PlaceContract.DBPlaces.COLUMN_PLACE_ID             + TEXT_TYPE + COMMA_SEP +
                    PlaceContract.DBPlacesOwners.COLUMN_PLACE_OWNER_ID + TEXT_TYPE + " )";

    private static final String DB_SQL_CREATE_RECOVERY =
            "CREATE TABLE " + RecoveryContract.DBRecovery.TABLE_RECOVERY + " ( " +
                    RecoveryContract.DBRecovery.COLUMN_RECOVERY_TIMESTAMP + TEXT_TYPE + COMMA_SEP +
                    RecoveryContract.DBRecovery.COLUMN_RECOVERY_DISTANCE + INT_TYPE + " )";


    private static final String DB_SQL_DELETE_USERS = "DROP TABLE IF EXISTS " + UserContract.DBUser.TABLE_USERS;

    private static final String DB_SQL_DELETE_MESSAGES = "DROP TABLE IF EXISTS " + MsgContract.DBMsg.TABLE_MESSAGE;

    private static final String DB_SQL_DELETE_QUESTS = "DROP TABLE IF EXISTS " + QuestContract.DBQuest.TABLE_QUESTS;

    // !!! DO NOT CALL THIS STATEMENT DIRECTLY. Use deletePlaces method instead
    // should be used in pare with DB_SQL_DELETE_PLACES_OWNERS
    private static final String DB_SQL_DELETE_PLACES = "DROP TABLE IF EXISTS " + PlaceContract.DBPlaces.TABLE_PLACES;

    // !!! DO NOT CALL THIS STATEMENT DIRECTLY. Use deletePlaces method instead
    // should be used in pare with DB_SQL_DELETE_PLACES
    private static final String DB_SQL_DELETE_PLACES_OWNERS = "DROP TABLE IF EXISTS " + PlaceContract.DBPlacesOwners.TABLE_PLACES_OWNERS;

    private static final String DB_SQL_DELETE_RECOVERY = "DROP TABLE IF EXISTS " + RecoveryContract.DBRecovery.TABLE_RECOVERY;

    private static final String DB_SQL_ADD_MAX_IMPLANT_HP_COLUMN = "ALTER TABLE " + UserContract.DBUser.TABLE_USERS + " ADD COLUMN " + UserContract.DBUser.COLUMN_MAX_IMPLANT_HP + INT_TYPE + ";";

    private static final String DB_SQL_ADD_USER_TEAM_COLUMN = "ALTER TABLE " + UserContract.DBUser.TABLE_USERS + " ADD COLUMN " + UserContract.DBUser.COLUMN_TEAM + TEXT_TYPE + ";";

    private void createPlace(SQLiteDatabase db) {
        db.execSQL(DB_SQL_CREATE_PLACES);
        db.execSQL(DB_SQL_CREATE_PLACES_OWNERS);
    }

    private void deletePlaces(SQLiteDatabase db) {
        db.execSQL(DB_SQL_DELETE_PLACES);
        db.execSQL(DB_SQL_DELETE_PLACES_OWNERS);
    }

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
        createPlace(db);
        db.execSQL(DB_SQL_CREATE_RECOVERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        if (oldVersion == 1 && newVersion == DB_VERSION) {
            db.execSQL(DB_SQL_CREATE_MESSAGES);
        }

        if (oldVersion == 2 && newVersion == DB_VERSION) {
            db.execSQL(DB_SQL_CREATE_QUESTS);
        }

        if (oldVersion == 3 && newVersion == DB_VERSION) {
            createPlace(db);
        }

        if (oldVersion == 4 && newVersion == DB_VERSION) {
            db.execSQL(DB_SQL_CREATE_RECOVERY);
        }

        if (oldVersion == 5 && newVersion == DB_VERSION) {
            db.execSQL(DB_SQL_ADD_MAX_IMPLANT_HP_COLUMN);
        }

        if (oldVersion == 6 && newVersion == DB_VERSION) {
            db.execSQL(DB_SQL_ADD_USER_TEAM_COLUMN);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void dumpTable(String tableName) {

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                tableName,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor == null)
            return;

        if (cursor.getCount() == 0)
            return;

        Log.i(Constants.TAG, "============== dumping data from table: " + tableName + " ================ ");

        if( cursor.moveToFirst()) {
            String dump = new String();

            int colCount = cursor.getColumnCount();
            for (int i = 0; i < colCount; i++) {
                dump += cursor.getColumnName(i) + " | ";
            }
            Log.i(Constants.TAG, dump);
            Log.i(Constants.TAG, "----------------------------------------------------------------------------");

            do {
                dump = "";
                for (int i = 0; i < colCount; i++) {
                    dump += cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i)));
                    dump += " | ";
                }
                Log.i(Constants.TAG, dump);
            } while (cursor.moveToNext());
        }

        Log.i(Constants.TAG, "=========================================================================== ");
    }
}
