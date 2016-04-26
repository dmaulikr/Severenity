package com.nosad.sample.entity.contracts;

import android.provider.BaseColumns;

/**
 * Created by Novosad on 2/17/16.
 */
public final class UserContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public UserContract() {}

    /* Inner class that defines the table contents */
    public static abstract class DBUser implements BaseColumns {
        public static final String TABLE_USERS = "users";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_EXPERIENCE = "experience";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_NULLABLE = "NULL";
    }
}
