package com.severenity.entity.contracts;

import android.provider.BaseColumns;

/**
 * Created by Andriy on 5/16/2016.
 */
public class PlaceContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private PlaceContract() {}

    /* Inner class that defines the table contents */
    public static abstract class DBPlaces implements BaseColumns {
        public static final String TABLE_PLACES = "places";
        public static final String COLUMN_PLACE_ID = "place_id";
        public static final String COLUMN_PLACE_NAME = "place_name";
        public static final String COLUMN_PLACE_LAT = "place_latitude";
        public static final String COLUMN_PLACE_LNG = "place_longitude";
        public static final String COLUMN_PLACE_TYPE = "place_type";
    }

    public static abstract class DBPlacesOwners {
        public static final String TABLE_PLACES_OWNERS = "places_owners";
        public static final String COLUMN_PLACE_OWNER_ID = "owner_id";
    }

}
