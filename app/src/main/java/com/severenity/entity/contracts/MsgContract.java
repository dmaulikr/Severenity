package com.severenity.entity.contracts;

import android.provider.BaseColumns;

/**
 * Created by Andriy on 4/27/2016.
 */
public class MsgContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MsgContract() {}

    /* Inner class that defines the table contents */
    public static abstract class DBMsg implements BaseColumns {
        public static final String TABLE_MESSAGE = "message";
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}