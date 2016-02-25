package com.nosad.sample.engine.managers.data;

import static com.nosad.sample.entity.contracts.UserContract.DBUser.*;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nosad.sample.entity.User;
import com.nosad.sample.utils.common.Constants;

/**
 * Created by Novosad on 2/17/16.
 */
public class UserManager extends DataManager {
    public UserManager(Context context) {
        super(context);
    }

    public void addUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, user.getId());
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());

        db.insert(TABLE_USERS, COLUMN_NULLABLE, values);
        db.close();
    }

    public User getUserById(int id) {
        if (id < 0) {
            Log.e(Constants.TAG, "UserManager: user id specified in query must be bigger then 0.");
            return null;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL},
                " id = ?",
                new String[]{String.valueOf(id)},
                null, null, null, null
        );

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return null;
        }

        User user = new User();
        user.setId(Integer.parseInt(cursor.getString(0)));
        user.setName(cursor.getString(1));
        user.setEmail(cursor.getString(2));

        cursor.close();
        db.close();

        return user;
    }

    public User getUser(User user) {
        checkIfNull(user);

        return getUserById(user.getId());
    }

    public void deleteUser(User user) {
        checkIfNull(user);

        deleteUserById(user.getId());
    }

    public void deleteUserById(int id) {
        if (id < 0) {
            Log.e(Constants.TAG, "UserManager: user id specified in query must be bigger then 0.");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_USERS, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllUsers() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TABLE_USERS, null, null);
        db.close();
    }
}
