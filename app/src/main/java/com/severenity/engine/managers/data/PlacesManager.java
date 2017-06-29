package com.severenity.engine.managers.data;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.google.android.gms.maps.model.LatLng;
import com.severenity.App;
import com.severenity.engine.network.RequestCallback;
import com.severenity.entity.GamePlace;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Handles all operations related to places: add, remove, update ownership etc.
 *
 * Created by Novosad on 5/16/2016.
 */
public class PlacesManager extends DataManager {

    private Realm realm;

    public PlacesManager(Context context) {
        super(context);

        realm = Realm.getInstance(new RealmConfiguration.Builder().build());
    }

    /**
     * Looks for the place in db specified with the id.
     *
     * @param placeId - id of the place to find.
     * @return {@link GamePlace} object if found, null otherwise.
     */
    public GamePlace findPlaceById(String placeId) {
        return realm.where(GamePlace.class).equalTo("placeId", placeId).findFirst();
    }

    /**
     * Searches through all entries of owners to find all {@link GamePlace}'s that
     * have this owner.
     *
     * @param ownerId - user id to look-up for.
     * @return list of the places if found, null if error occurred.
     */
    public ArrayList<GamePlace> findPlacesByOwner(String ownerId) {
        RealmResults<GamePlace> realmPlaces = realm.where(GamePlace.class).contains("owners.id", ownerId).findAll();
        List<GamePlace> places = realm.copyFromRealm(realmPlaces);

        return new ArrayList<>(places);
    }

    /**
     * Adds places from json array received from the server.
     *
     * @param data - includes JSON data for all places on the server.
     */
    public void addPlaces(final JSONArray data) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createOrUpdateAllFromJson(GamePlace.class, data);
            }
        });
    }

    /**
     * Adds places into local DB and sends the data to the server.
     *
     * @param place         - indicates the place to be stored
     * @param storeOnServer - indicates if this place should be also stored in the server.
     */
    public void addPlace(final GamePlace place, final boolean storeOnServer) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(place);

                if (!storeOnServer) {
                    return;
                }

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
        });
    }

    /**
     * Add owner to the specific place.
     *
     * @param placeId - place to update.
     * @param ownerId - owner to add to the place.
     * @return update place.
     */
    public GamePlace addOwnerToPlace(String placeId, final String ownerId) {
        final GamePlace place = findPlaceById(placeId);
        if (place == null) {
            Log.e(Constants.TAG, "PlacesManager: no place with ID " + placeId + " already exist." );
            return null;
        }

        if (place.hasOwner(ownerId)) {
            Log.e(Constants.TAG, "PlacesManager: this owner owns this place." );
            return null;
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                place.addOwner(ownerId);
                realm.copyToRealmOrUpdate(place);
            }
        });

        return place;
    }

    /**
     * Gets the list of the places that are within certain square and the
     * distance to then from current place are no longer that @param distance
     *
     * @param currentLocation - current users location
     * @param westSouth       - left bottom edge of abstract square within which places should be considered
     * @param northEast       - left bottom edge of abstract square within which places should be considered
     * @param distance        - distance within which places should be considered (basically  the radius of User's ViewArea)
     * @return the list of places that met requirements
     */
    public ArrayList<GamePlace> getLimitedPlaces(LatLng currentLocation, LatLng westSouth, LatLng northEast, double distance) {
        RealmResults<GamePlace> realmPlaces = realm.where(GamePlace.class)
                .greaterThanOrEqualTo("lat", westSouth.latitude)
                .lessThanOrEqualTo("lat", northEast.latitude)
                .greaterThanOrEqualTo("lng", westSouth.longitude)
                .lessThanOrEqualTo("lng", northEast.longitude)
                .findAll();
        List<GamePlace> allPlacesInRange = realm.copyFromRealm(realmPlaces);

        ArrayList<GamePlace> places = new ArrayList<>();

        for (GamePlace place : allPlacesInRange) {
            if (currentLocation != null && place.getPlacePos() != null &&
                    Utils.distanceBetweenLocations(currentLocation, place.getPlacePos()) < distance) {
                places.add(place);
            }
        }

        return places;
    }

    /**
     * Retrieves all places from the local DB.
     *
     * @return list of the places in local DB, null if error.
     */
    public ArrayList<GamePlace> getPlaces() {
        RealmResults<GamePlace> realmPlaces = realm.where(GamePlace.class).findAll();
        List<GamePlace> places = realm.copyFromRealm(realmPlaces);

        return new ArrayList<>(places);
    }

    /**
     * Deletes owner id from the place owners.
     *
     * @param placeId - id of the place to update.
     * @param ownerId - owner to remove
     */
    public void deleteOwnership(String placeId, String ownerId) {
        final GamePlace place = findPlaceById(placeId);
        place.removeOwner(ownerId);
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(place);
            }
        });
    }

    /**
     * Retrieve places from server based on current position and radius.
     *
     * @param currentPosition - current location of the user.
     * @param radius - radius to check places within.
     * @param callback - callback to execute after places were retrieved.
     */
    public void getPlacesFromServer(LatLng currentPosition, int radius, RequestCallback callback) {
        String request = Constants.REST_API_PLACES + "/?lng=" + Double.toString(currentPosition.longitude) + "&lat=" + Double.toString(currentPosition.latitude) + "&radius=" + Integer.toString(radius);
        App.getRestManager().createRequest(request, Request.Method.GET, null, callback);
    }

    /**
     * Sends API request to store place on the server DB.
     *
     * @param place - place to store on the server.
     */
    private void sendPlaceToServer(GamePlace place, RequestCallback callback) {
        JSONObject data = new JSONObject();
        try {
            data.put("placeId", place.getPlaceID());
            data.put("name", place.getPlaceName());
            data.put("lng", String.valueOf(place.getPlacePos().longitude));
            data.put("lat", String.valueOf(place.getPlacePos().latitude));
            data.put("type", place.getPlaceType().ordinal());

            App.getRestManager().createRequest(Constants.REST_API_PLACES, Request.Method.POST, data, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
