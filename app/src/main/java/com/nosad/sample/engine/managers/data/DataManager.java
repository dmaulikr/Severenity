package com.nosad.sample.engine.managers.data;

import android.content.Context;
import android.util.Log;

import com.nosad.sample.helpers.SQLiteDBHelper;
import com.nosad.sample.utils.common.Constants;

/**
 * Created by Novosad on 2/16/16.
 * Handles access to local database and shared preferences
 */
public class DataManager {
    protected SQLiteDBHelper dbHelper;
    protected Context context;

    public DataManager(Context context) {
        this.context = context;
        dbHelper = new SQLiteDBHelper(context);
    }

    protected Object checkIfNull(Object object) {
        if (object == null) {
            Log.e(Constants.TAG, "DataManager: object specified in query is null.");
            return null;
        }

        return object;
    }
}
