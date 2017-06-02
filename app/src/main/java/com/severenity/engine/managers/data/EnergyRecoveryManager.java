package com.severenity.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.severenity.App;
import com.severenity.entity.User;
import com.severenity.utils.DateUtils;
import com.severenity.utils.common.Constants;

import java.util.Timer;
import java.util.TimerTask;

import static com.severenity.entity.contracts.RecoveryContract.DBRecovery.TABLE_RECOVERY;
import static com.severenity.entity.contracts.RecoveryContract.DBRecovery.COLUMN_RECOVERY_TIMESTAMP;
import static com.severenity.entity.contracts.RecoveryContract.DBRecovery.COLUMN_RECOVERY_DISTANCE;

/**
 * Created by Novosad on 4/27/2016.
 */
public class EnergyRecoveryManager extends DataManager {

    private final int TIMER_SCHEDULER = 5 * 60 * 1000; // 5 minutes

    private Timer mTimer = new Timer();

    public EnergyRecoveryManager(Context context) {
        super(context);
    }

    public void start() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                tryRecovery();
            }
        }, 0, TIMER_SCHEDULER);
    }

    public void stop() {
        mTimer.cancel();
    }

    private boolean tryRecovery() {

        User currentUser = App.getUserManager().getCurrentUser();
        if (currentUser == null) {
            Log.e(Constants.TAG, "EnergyRecoveryManager: was not able to get current user.");
            return false;
        }

        Cursor cursor = getDBCursor();
        if (cursor == null) {
            updateLastRecoveryInfo(currentUser.getDistance());
            return false;
        }

        cursor.moveToFirst();

        int distance = cursor.getInt(cursor.getColumnIndex(COLUMN_RECOVERY_DISTANCE));
        String timeStamp = cursor.getString(cursor.getColumnIndex(COLUMN_RECOVERY_TIMESTAMP));
        String currentTimeStamp = DateUtils.getTimestamp();
        cursor.close();

        if ((distance - currentUser.getDistance()) > Constants.DISTANCE_TO_PASS_FOR_RECOVERY) {
            Log.i(Constants.TAG, "\"Do recovery due to distance passed: " +
                    "last rec distance: " + distance + ";" +
                    "current passed distance: " + currentUser.getDistance());

            updateLastRecoveryInfo(distance);
            return true;
        }
        else if (DateUtils.getDayDifference(timeStamp, currentTimeStamp) > Constants.DAYS_TO_PASS_FOR_RECOVERY) {
            Log.i(Constants.TAG, "Do recovery due to time passed: " +
                    "last rec timestamp: " + timeStamp + ";" +
                    "current timestamp: " + currentTimeStamp);

            updateLastRecoveryInfo(distance);
            return true;
        }

        return false;
    }

    private void updateLastRecoveryInfo(int distance) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {

            db.delete(TABLE_RECOVERY, null, null);
            ContentValues values = new ContentValues();
            values.put(COLUMN_RECOVERY_DISTANCE, distance);
            values.put(COLUMN_RECOVERY_TIMESTAMP, DateUtils.getTimestamp());

            db.insert(TABLE_RECOVERY, "NULL", values);
        } catch (Exception ex) {
            Log.e(Constants.TAG, "EnergyRecoveryManager: Adding data to DB failed. " + ex.getMessage());
            return;
        }
    }

    private Cursor getDBCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;
        try {
            cursor = db.query(
                    TABLE_RECOVERY,
                    new String[]{COLUMN_RECOVERY_TIMESTAMP, COLUMN_RECOVERY_DISTANCE},
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

        return cursor;
    }
}

