package com.nosad.sample.entity.contracts;

import android.provider.BaseColumns;

/**
 * Created by Novosad on 5/9/16.
 */
public class QuestContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public QuestContract() {}

    /* Inner class that defines the table contents */
    public static abstract class DBQuest implements BaseColumns {
        public static final String TABLE_QUESTS = "quests";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_NULLABLE = "NULL";
    }
}
