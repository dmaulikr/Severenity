package com.severenity.engine.managers.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.severenity.App;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.GamePlace;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.severenity.entity.contracts.PlaceContract.DBPlaces.COLUMN_PLACE_TYPE;
import static com.severenity.entity.contracts.PlaceContract.DBPlaces.TABLE_PLACES;
import static com.severenity.entity.contracts.PlaceContract.DBPlaces.COLUMN_PLACE_ID;
import static com.severenity.entity.contracts.PlaceContract.DBPlaces.COLUMN_PLACE_NAME;
import static com.severenity.entity.contracts.PlaceContract.DBPlaces.COLUMN_PLACE_LAT;
import static com.severenity.entity.contracts.PlaceContract.DBPlaces.COLUMN_PLACE_LNG;

import static com.severenity.entity.contracts.PlaceContract.DBPlacesOwners.TABLE_PLACES_OWNERS;
import static com.severenity.entity.contracts.PlaceContract.DBPlacesOwners.COLUMN_PLACE_OWNER_ID;


/**
 * Created by Andriy on 5/16/2016.
 */
public class PlacesManager extends DataManager {

    public PlacesManager(Context context) {
        super(context);
    }

    /**
     * Returns {@link GamePlace} object from the cursor in DB.
     *
     * @param cursor - cursor of the row in db.
     * @return {@link GamePlace} object instance.
     */
    private GamePlace placeFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_ID));
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_NAME));
        LatLng location = new LatLng(cursor.getDouble(cursor.getColumnIndex(COLUMN_PLACE_LAT)), cursor.getDouble(cursor.getColumnIndex(COLUMN_PLACE_LNG)));
        GamePlace.PlaceType placeType = GamePlace.PlaceType.values()[cursor.getInt(cursor.getColumnIndex(COLUMN_PLACE_TYPE))];

        return new GamePlace(id, name, location, placeType);
    }

    /**
     * Looks for the owners for the {@link GamePlace} object.
     * Adds if found to the place object passed in.
     *
     * @param place - place to find owners of.
     * @return true if found, false otherwise.
     */
    private boolean findPlaceOwners(GamePlace place) {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query(
                 TABLE_PLACES_OWNERS, new String[]{COLUMN_PLACE_OWNER_ID, COLUMN_PLACE_ID},
                 COLUMN_PLACE_ID + " = ?", new String[] { place.getPlaceID() }, null, null, null, null)) {

            if (cursor.moveToFirst()) {
                do {
                    place.addOwner(cursor.getString(cursor.getColumnIndex(COLUMN_PLACE_OWNER_ID)));
                } while (cursor.moveToNext());
            }

            return true;
        } catch (SQLException e) {
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            return false;
        }
    }

    /**
     * Looks for the place in db specified with the id.
     *
     * @param placeID - id of the place to find.
     * @return {@link GamePlace} object if found, null otherwise.
     */
    public GamePlace findPlaceByID(String placeID) {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query(
                 TABLE_PLACES, null,
                 COLUMN_PLACE_ID + " = ?", new String[] {placeID}, null, null, null, null)) {

            if (cursor == null) {
                return null;
            }

            if (cursor.getCount() == 0) {
                return null;
            }

            if (cursor.getCount() > 1) {
                Log.e(Constants.TAG, "PlacesManager: there are more then one with same ID. " + placeID);
                return null;
            }

            GamePlace place = null;
            if (cursor.moveToFirst()) {
                place = placeFromCursor(cursor);
            }

            if (place == null) {
                return null;
            }

            findPlaceOwners(place);

            return place;
        } catch (SQLException e){
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            return null;
        }
    }

    /**
     * Searches through all entries of owners to find all {@link GamePlace}'s that
     * have this owner.
     *
     * @param ownerID - user id to look-up for.
     * @return list of the places if found, null if error occurred.
     */
    public ArrayList<GamePlace> findPlacesByOwner(String ownerID) {
        // find if place has owners
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query(
                 TABLE_PLACES_OWNERS, new String[]{ COLUMN_PLACE_ID },
                 COLUMN_PLACE_OWNER_ID + " = ?",
                 new String[] { ownerID }, null, null, null, null)) {

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

            return places;
        } catch (SQLException e) {
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            return null;
        }
    }

    /**
     * Adds places into local DB and sends the data to the server.
     *
     * @param place         - indicates the place to be stored
     * @param storeOnServer - indicates if this place should be also stored in the server.
     * @return  true is success, false otherwise
     */
    public boolean addPlace(GamePlace place, boolean storeOnServer) {

        if (findPlaceByID(place.getPlaceID()) != null) {
            Log.e(Constants.TAG, "PlacesManager: place with ID " + place.getPlaceID() + " already exists." );
            return false;
        }

        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PLACE_ID,   place.getPlaceID());
            values.put(COLUMN_PLACE_NAME, place.getPlaceName());
            values.put(COLUMN_PLACE_LAT,  place.getPlacePos().getLatitude());
            values.put(COLUMN_PLACE_LNG,  place.getPlacePos().getLongitude());
            values.put(COLUMN_PLACE_TYPE, place.getPlaceType().ordinal());

            db.insert(TABLE_PLACES, "NULL", values);
        } catch (Exception ex) {
            Log.e(Constants.TAG, "PlacesManager: Adding data to DB failed. " + ex.getMessage());
            return false;
        }

        if (storeOnServer) {

            sendPlaceToServer(place, new RequestCallback() {
                @Override
                public void onResponseCallback(JSONObject response) {
                    if (response != null) {
                        Log.d(Constants.TAG, response.toString());
                    } else {
                        Log.e(Constants.TAG, "Place add has null response.");
                    }
                }

                @Override
                public void onErrorCallback(NetworkResponse response) {
                    if (response != null) {
                        Log.e(Constants.TAG, response.toString());
                    } else {
                        Log.e(Constants.TAG, "Place add error has null response.");
                    }
                }
            });
        }
        return true;
    }

    /**
     * Add owner to the specific place.
     *
     * @param placeID - place to update.
     * @param ownerID - owner to add to the place.
     * @return update place.
     */
    public GamePlace addOwnerToPlace(String placeID, String ownerID) {
        GamePlace place = findPlaceByID(placeID);
        if (place == null) {
            Log.e(Constants.TAG, "PlacesManager: no place with ID " + placeID + " already exist." );
            return null;
        }

        if (place.hasOwner(ownerID)) {
            Log.e(Constants.TAG, "PlacesManager: this owner owns this place." );
            return null;
        }

        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PLACE_ID,         placeID);
            values.put(COLUMN_PLACE_OWNER_ID,   ownerID);

            db.insert(TABLE_PLACES_OWNERS, "NULL", values);
        } catch (Exception ex) {
            Log.e(Constants.TAG, "PlacesManager: Adding data to DB failed. " + ex.getMessage());
            return null;
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
        String condition = "((" + COLUMN_PLACE_LAT + " > ? AND " + COLUMN_PLACE_LAT + " < ? ) AND ( " +
                COLUMN_PLACE_LNG + " > ? AND " + COLUMN_PLACE_LNG + " < ? ))";

        String[] compare = new String[]{Double.toString(westSouth.getLatitude()), Double.toString(northEast.getLatitude()),
                Double.toString(westSouth.getLongitude()), Double.toString(northEast.getLongitude())};

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query(TABLE_PLACES, null, condition, compare, null, null, null)) {

            ArrayList<GamePlace> places = new ArrayList<>(cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    GamePlace place = placeFromCursor(cursor);

                    if (currentLocation != null && place.getPlacePos() != null &&
                            Utils.distanceBetweenLocations(currentLocation, place.getPlacePos()) < distance) {
                        findPlaceOwners(place);
                        places.add(place);
                    }

                } while (cursor.moveToNext());
            }

            return places;
        } catch (SQLException e) {
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves all places from the local DB.
     *
     * @return list of the places in local DB, null if error.
     */
    public ArrayList<GamePlace> getPlaces() {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query(TABLE_PLACES,null, null, null, null, null, null)) {

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

    /**
     * Deletes owner id from the place owners.
     *
     * @param placeID - id of the place to update.
     * @param ownerID - owner to remove
     * @return true if succeeded.
     */
    public boolean deleteOwnership(String placeID, String ownerID) {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
            String where = COLUMN_PLACE_OWNER_ID + " = ? and " + COLUMN_PLACE_ID + " = ?";
            String[] conditions = new String[]{ownerID, placeID};

            return db.delete(TABLE_PLACES_OWNERS, where, conditions) != 0;
        } catch (SQLException e){
            Log.e(Constants.TAG, "PlacesManager: error querying request. " + e.getMessage());
            return false;
        }
    }

    /**
     * Clears all places and owners in the local DB.
     */
    public void clearPlacesAndOwnersData() {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
            db.delete(TABLE_PLACES_OWNERS, null, null);
            db.delete(TABLE_PLACES, null, null);
        } catch (SQLException e){
            Log.e(Constants.TAG, "PlacesManager: error clearing place tables. " + e.getMessage());
        }
    }

    /**
     * Retrieve places from server based on current position and radius.
     *
     * @param currentPosition - current location of the user.
     * @param radius - radius to check places within.
     * @param callback - callback to execute after places were retrieved.
     */
    public void getPlacesFromServer(LatLng currentPosition, int radius, RequestCallback callback) {
        String request = Constants.REST_API_PLACES + "/?lng=" + Double.toString(currentPosition.getLongitude()) + "&lat=" + Double.toString(currentPosition.getLatitude()) + "&radius=" + Integer.toString(radius);
        App.getRestManager().createRequest(request, Request.Method.GET, null, callback);
    }

    /**
     * Sends API request to store place on the server DB.
     *
     * @param place - place to store on the server.
     */
    public void sendPlaceToServer(GamePlace place, RequestCallback callback) {
        JSONObject data = new JSONObject();
        try {
            data.put("placeId", place.getPlaceID());
            data.put("name", place.getPlaceName());
            data.put("lng", String.valueOf(place.getPlacePos().getLongitude()));
            data.put("lat", String.valueOf(place.getPlacePos().getLatitude()));
            data.put("type", place.getPlaceType().ordinal());

            App.getRestManager().createRequest(Constants.REST_API_PLACES, Request.Method.POST, data, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
