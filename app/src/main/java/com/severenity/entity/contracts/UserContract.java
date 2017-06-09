package com.severenity.entity.contracts;

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
        public static final String COLUMN_CREATED_DATE = "createdDate";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_EXPERIENCE = "experience";
        public static final String COLUMN_IMMUNITY = "immunity";
        public static final String COLUMN_ENERGY = "energy";
        public static final String COLUMN_MAX_IMMUNITY = "maxImmunity";
        public static final String COLUMN_MAX_ENERGY = "maxEnergy";
        public static final String COLUMN_CREDITS = "credits";
        public static final String COLUMN_IMPLANT_HP = "implantHP";
        public static final String COLUMN_MAX_IMPLANT_HP = "maxImplantHP";
        public static final String COLUMN_VIEW_RADIUS = "viewRadius";
        public static final String COLUMN_ACTION_RADIUS = "actionRadius";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_TEAM = "team";
        public static final String COLUMN_TEAM_NAME = "teamName";
        public static final String COLUMN_NULLABLE = "NULL";
    }
}
