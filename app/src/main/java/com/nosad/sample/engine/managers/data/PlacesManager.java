package com.nosad.sample.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.nosad.sample.App;
import com.nosad.sample.entity.GamePlace;
import com.nosad.sample.utils.Utils;
import com.nosad.sample.utils.common.Constants;

import java.util.ArrayList;

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

    private GamePlace placeFromCursor(Cursor cursor) {

        String strID       = cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_ID));
        String placeName   = cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_NAME));
        LatLng pos         = new LatLng(cursor.getDouble(cursor.getColumnIndex(COLUMN_PLACE_LAT)), cursor.getDouble(cursor.getColumnIndex(COLUMN_PLACE_LNG)));

        return new GamePlace(strID, placeName, pos);
    }

    private boolean findPlaceOwners(GamePlace place) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        // find if place has owners
        try {
            cursor = db.query(
                    TABLE_PLACES_OWNERS,
                    new String[]{COLUMN_PLACE_OWNER_ID, COLUMN_PLACE_ID},
                    COLUMN_PLACE_ID + " = ?",
                    new String[] { place.getPlaceID() },
                    null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    place.addOwner(cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_OWNER_ID)));
                }
                while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return true;

        }catch (SQLException e){
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            db.close();
            return false;
        }
    }

    public GamePlace findPlaceByID(String placeID) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_PLACES,
                    new String[]{COLUMN_PLACE_ID, COLUMN_PLACE_NAME, COLUMN_PLACE_LAT, COLUMN_PLACE_LNG},
                    COLUMN_PLACE_ID + " = ?",
                    new String[] {placeID},
                    null, null, null, null);

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

            GamePlace place = null;
            if (cursor.moveToFirst()) {
                place = placeFromCursor(cursor);
            }

            cursor.close();
            cursor = null;
            db.close();

            if (place == null) {
                return null;
            }

            findPlaceOwners(place);

            return place;

        }catch (SQLException e){
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            return null;
        }
    }

    public ArrayList<GamePlace> findPlacesByOwner(String ownerID) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        // find if place has owners
        try {
            cursor = db.query(
                    TABLE_PLACES_OWNERS,
                    new String[]{COLUMN_PLACE_ID},
                    COLUMN_PLACE_OWNER_ID + " = ?",
                    new String[] { ownerID },
                    null, null, null, null);

            ArrayList<GamePlace> places = new ArrayList<>(cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    String placeID = cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_ID));

                    GamePlace place = findPlaceByID(placeID);
                    if (place != null) {
                        places.add(place);
                    }
                }
                while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return places;

        }catch (SQLException e){
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            db.close();
            return null;
        }
    }

    public boolean addPlace(GamePlace place) {

        if (findPlaceByID(place.getPlaceID()) != null) {
            Log.e(Constants.TAG, "PlacesManager: place with ID " + place.getPlaceID() + " already exists." );
            return false;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
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

    public GamePlace addOwnerToPlace(String placeID, String ownerID) {

        GamePlace place = findPlaceByID(placeID);
        if (place == null) {
            Log.e(Constants.TAG, "PlacesManager: no place with ID " + placeID + " already exist." );
            return null;
        };

        if (place.hasOwner(ownerID)) {
            Log.e(Constants.TAG, "PlacesManager: this owner owns this place." );
            return null;
        }

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

    /**
     * gets the list of the places that are within certain square and the
     * distance to then from current place are no longer that @param distance
     *
     * @param currentLocation - current users location
     * @param westSouth       - left bottom edge of abstract square within which places should be considered
     * @param northEast       - left bottom edge of abstract square within which places should be considered
     * @param distance        - distance within which places should be considered (besically  the radius of User's ViewArea)
     * @return the list of places that met requirements
     */
    public ArrayList<GamePlace> getLimitedPlaces(LatLng currentLocation, LatLng westSouth, LatLng northEast, double distance) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            String condition = "((" + COLUMN_PLACE_LAT + " > ? AND " + COLUMN_PLACE_LAT + " < ? ) AND ( " +
                                    COLUMN_PLACE_LNG + " > ? AND " + COLUMN_PLACE_LNG + " < ? ))";

            String[] compare = new String[]{Double.toString(westSouth.latitude), Double.toString(northEast.latitude),
                                Double.toString(westSouth.longitude), Double.toString(northEast.longitude)};

            cursor = db.query(TABLE_PLACES,
                    null,
                    condition,
                    compare,
                    null,
                    null,
                    null);

            ArrayList<GamePlace> places = new ArrayList<>(cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    GamePlace place = placeFromCursor(cursor);

                    if (Utils.distanceBetweenLocations(currentLocation, place.getPlacePos()) < distance) {
                        findPlaceOwners(place);
                        places.add(place);
                    }

                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

            return places;

        } catch (SQLException e) {
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            return null;
        }
    }

    public ArrayList<GamePlace> getPlaces() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = db.query(TABLE_PLACES,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            ArrayList<GamePlace> places = new ArrayList<>(cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    GamePlace place = placeFromCursor(cursor);
                    findPlaceOwners(place);
                    places.add(place);

                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

            return places;

        } catch (SQLException e) {
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            return null;
        }
    }

    public void dumpPlaces() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            String table = TABLE_PLACES_OWNERS+ " left join " + TABLE_PLACES  + " on " + TABLE_PLACES + "." + COLUMN_PLACE_ID + " = " + TABLE_PLACES_OWNERS + "." + COLUMN_PLACE_ID;
            cursor = db.query(table, null, null, null, null, null, null);

            Log.d(Constants.TAG, "Records: " + Integer.toString(cursor.getCount()));

            Toast.makeText(App.getInstance().getApplicationContext(), Integer.toString(cursor.getCount()).toString(), Toast.LENGTH_SHORT).show();

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

            cursor.close();
            db.close();

        }catch (SQLException e){
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            return;
        };
    }

    public boolean deleteOwnership(String placeID, String ownerID) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            String where = COLUMN_PLACE_OWNER_ID + " = ? and " + COLUMN_PLACE_ID + " = ?";
            String[] conditions = new String[]{ownerID, placeID};
            return db.delete(TABLE_PLACES_OWNERS,
                    where,
                    conditions) == 0 ? false : true;

        }catch (SQLException e){
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            return false;
        }
    };
}
