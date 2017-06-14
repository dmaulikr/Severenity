package com.severenity.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.severenity.entity.contracts.PlaceContract;
import com.severenity.entity.contracts.QuestContract;
import com.severenity.entity.contracts.RecoveryContract;
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
    // 7 - added team column into users table
    // 8 - added team name column, tips and tickets for the user
    private static final int DB_VERSION = 8;
    private static final String DB_NAME = "Severenity.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER ";
    private static final String REAL_TYPE = " REAL ";
    private static final String COMMA_SEP = ",";

    private static final String DB_SQL_CREATE_QUESTS =
            "CREATE TABLE " + QuestContract.DBQuest.TABLE_QUESTS + " (" +
                    QuestContract.DBQuest._ID + INT_TYPE + " PRIMARY KEY," +
                    QuestContract.DBQuest.COLUMN_ID + TEXT_TYPE + COMMA_SEP +
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

    private static final String DB_SQL_DELETE_QUESTS = "DROP TABLE IF EXISTS " + QuestContract.DBQuest.TABLE_QUESTS;

    // !!! DO NOT CALL THIS STATEMENT DIRECTLY. Use deletePlaces method instead
    // should be used in pare with DB_SQL_DELETE_PLACES_OWNERS
    private static final String DB_SQL_DELETE_PLACES = "DROP TABLE IF EXISTS " + PlaceContract.DBPlaces.TABLE_PLACES;

    // !!! DO NOT CALL THIS STATEMENT DIRECTLY. Use deletePlaces method instead
    // should be used in pare with DB_SQL_DELETE_PLACES
    private static final String DB_SQL_DELETE_PLACES_OWNERS = "DROP TABLE IF EXISTS " + PlaceContract.DBPlacesOwners.TABLE_PLACES_OWNERS;

    private static final String DB_SQL_DELETE_RECOVERY = "DROP TABLE IF EXISTS " + RecoveryContract.DBRecovery.TABLE_RECOVERY;


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

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TEMP: clearing on creation
        db.execSQL(DB_SQL_CREATE_QUESTS);
        createPlace(db);
        db.execSQL(DB_SQL_CREATE_RECOVERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        if (oldVersion == 2 && newVersion == DB_VERSION) {
            db.execSQL(DB_SQL_CREATE_QUESTS);
        }

        if (oldVersion == 3 && newVersion == DB_VERSION) {
            createPlace(db);
        }

        if (oldVersion == 4 && newVersion == DB_VERSION) {
            db.execSQL(DB_SQL_CREATE_RECOVERY);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void dumpTable(String tableName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(tableName, null, null, null, null, null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return;
        }

        Log.i(Constants.TAG, "============== dumping data from table: " + tableName + " ================ ");

        if (cursor.moveToFirst()) {
            String dump = "";
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

        cursor.close();

        Log.i(Constants.TAG, "=========================================================================== ");
    }
}
