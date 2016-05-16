package com.nosad.sample.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.nosad.sample.entity.Place;
import com.nosad.sample.utils.common.Constants;

import static com.nosad.sample.entity.contracts.PlaceContract.DBPlaces.TABLE_PLACES;
import static com.nosad.sample.entity.contracts.PlaceContract.DBPlaces.COLUMN_PLACE_ID;
import static com.nosad.sample.entity.contracts.PlaceContract.DBPlaces.COLUMN_PLACE_NAME;
import static com.nosad.sample.entity.contracts.PlaceContract.DBPlaces.COLUMN_PLACE_LAT;
import static com.nosad.sample.entity.contracts.PlaceContract.DBPlaces.COLUMN_PLACE_LNG;

import static com.nosad.sample.entity.contracts.PlaceContract.DBPlacesOwners.TABLE_PLACES_OWNERS;
import static com.nosad.sample.entity.contracts.PlaceContract.DBPlacesOwners.COLUMN_PLACE_OWNER_ID;


/**
 * Created by Andriy on 5/16/2016.
 */
public class PlacesManager extends DataManager {

    public PlacesManager(Context context) {
        super(context);
    }

    private Place placeFromCursor(Cursor cursor) {

        String strID       = cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_ID));
        String placeName   = cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_NAME));
        LatLng pos         = new LatLng(cursor.getLong(cursor.getColumnIndex(COLUMN_PLACE_LAT)), cursor.getLong(cursor.getColumnIndex(COLUMN_PLACE_LAT)));

        return new Place(strID, placeName, pos);
    }

    public Place findPlaceByID(String placeID) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_PLACES,
                    new String[]{COLUMN_PLACE_ID, COLUMN_PLACE_NAME, COLUMN_PLACE_LAT, COLUMN_PLACE_LNG},
                    COLUMN_PLACE_ID + " = ?",
                    new String[] {placeID},
                    null, null, null, null
            );
        }catch (SQLException e){
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            return null;
        };

        if (cursor == null) {
            return null;
        }

        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return null;
        }

        if (cursor.getCount() > 1) {
            Log.e(Constants.TAG, "PlacesManager: there are more then one with same ID. " + placeID);
            cursor.close();
            db.close();
            return null;
        }

        Place place = null;
        if (cursor.moveToFirst()) {
            place = placeFromCursor(cursor);
        }

        cursor.close();
        cursor = null;

        if (place == null) {
            db.close();
            return null;
        }

            // find if place has owners
        try {
            cursor = db.query(
                    TABLE_PLACES_OWNERS,
                    new String[]{COLUMN_PLACE_OWNER_ID, COLUMN_PLACE_ID},
                    COLUMN_PLACE_ID + " = ?",
                    new String[] { place.getPlaceID() },
                    null, null, null, null
            );
        }catch (SQLException e){
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            db.close();
            return place;
        };

        if (cursor.moveToFirst()) {
            do {
                place.addOwner(cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_OWNER_ID)));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return place;
    }

    public boolean addPlace(Place place) {

        if (findPlaceByID(place.getPlaceID()) != null) {
            Log.e(Constants.TAG, "PlacesManager: place with ID " + place.getPlaceID() + " already exists." );
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(COLUMN_PLACE_ID,   place.getPlaceID());
            values.put(COLUMN_PLACE_NAME, place.getPlaceName());
            values.put(COLUMN_PLACE_LAT,  place.getPlacePos().latitude);
            values.put(COLUMN_PLACE_LNG,  place.getPlacePos().longitude);

            db.insert(TABLE_PLACES, "NULL", values);
            db.setTransactionSuccessful();
        }
        catch (Exception ex) {
            db.close();
            Log.e(Constants.TAG, "PlacesManager: Adding data to DB failed. " + ex.getMessage());
            return false;
        }
        finally {
            db.endTransaction();
            db.close();
        }

        return true;
    }

    public Place addOwnerToPlace(String placeID, String ownerID) {

        Place place = findPlaceByID(placeID);
        if (place == null) {
            Log.e(Constants.TAG, "PlacesManager: no place with ID " + placeID + " already exist." );
            return null;
        };

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(COLUMN_PLACE_ID,         placeID);
            values.put(COLUMN_PLACE_OWNER_ID,   ownerID);

            db.insert(TABLE_PLACES_OWNERS, "NULL", values);
            db.setTransactionSuccessful();
        }
        catch (Exception ex) {
            db.close();
            Log.e(Constants.TAG, "PlacesManager: Adding data to DB failed. " + ex.getMessage());
            return null;
        }
        finally {

            db.endTransaction();
            db.close();
        }

        place.addOwner(ownerID);
        return place;
    }

    public void dumpPlaces() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            String table = TABLE_PLACES + " left join " + TABLE_PLACES_OWNERS + " on " + TABLE_PLACES + "." + COLUMN_PLACE_ID + " = " + TABLE_PLACES_OWNERS + "." + COLUMN_PLACE_ID;
            cursor = db.query(table, null, null, null, null, null, null);

            Log.d(Constants.TAG, "Records: " + Integer.toString(cursor.getCount()));

            String str;
            if (cursor.moveToFirst()) {
                do {
                    str = "";
                    for (String cn : cursor.getColumnNames()) {
                        str = str.concat(cn + " = " + cursor.getString(cursor.getColumnIndex(cn)) + "; ");
                    }
                    Log.d(Constants.TAG, str);
                } while (cursor.moveToNext());
            }

        }catch (SQLException e){
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            return;
        };
    }
}
