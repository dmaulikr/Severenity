package com.severenity.entity.contracts;

import android.provider.BaseColumns;

/**
 * Created by Andriy on 7/1/2016.
 */
public class RecoveryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private RecoveryContract() {}

    /* Inner class that defines the table contents */
    public static abstract class DBRecovery implements BaseColumns {
        public static final String TABLE_RECOVERY = "recovery";
        public static final String COLUMN_RECOVERY_TIMESTAMP = "timestamp";
        public static final String COLUMN_RECOVERY_DISTANCE = "distance";
    }

}
