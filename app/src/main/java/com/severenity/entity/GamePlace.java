package com.severenity.entity;

import com.google.android.gms.maps.model.LatLng;
import com.severenity.utils.common.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Represents every place in a game user can interact with.
 *
 * Created by Novosad on 5/15/2016.
 */
public class GamePlace extends RealmObject {
    public enum PlaceType {
        Default,
        Money,
        ImplantRecovery,
        ImplantRepair,
        ImplantIncrease,
        EnergyIncrease
    }

    @Required
    @PrimaryKey
    private String placeId;

    @Required
    private String name;

    private RealmList<PlaceOwner> owners;

    private double lat;
    private double lng;
    private int placeTypeValue;

    public GamePlace() {
        // Required empty constructor
    }

    public GamePlace(String placeId, String name, LatLng latlng, PlaceType placeType) {
        this.placeId = placeId;
        this.name = name;
        lat = latlng.latitude;
        lng = latlng.longitude;
        placeTypeValue = placeType.ordinal();
        owners = new RealmList<>();
    }

    public void addOwner(String owner) {
        this.owners.add(new PlaceOwner(owner));
    }

    public void removeOwner(String ownerId) {
        for (int i = 0; i < owners.size(); i++) {
            if (owners.get(i).getId().equalsIgnoreCase(ownerId)) {
                owners.remove(i);
            }
        }
    }

    public boolean hasOwner(String ownerID) {
        if (ownerID == null || owners.isEmpty()) {
            return false;
        }

        for (PlaceOwner placeOwner : owners) {
            if (placeOwner.getId().equalsIgnoreCase(ownerID)) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<String> getOwners() {
        if (owners.isEmpty()) {
            return null;
        }

        ArrayList<String> owners = new ArrayList<>(this.owners.size());

        for (PlaceOwner placeOwner : this.owners) {
            owners.add(placeOwner.getId());
        }

        return owners;
    }

    public String getPlaceID() { return this.placeId; }
    public String getPlaceName() { return this.name; }

    public LatLng getPlacePos() { return new LatLng(lat, lng); }

    public PlaceType getPlaceType() {
        return PlaceType.values()[placeTypeValue];
    }

    /**
     * Returns JSON structured info about this place.
     *
     * @return - json formatted string with info about this place
     */
    public String getJSONPlaceInfo() {
        JSONObject obj = new JSONObject();
        try {
            obj.put(Constants.OBJECT_TYPE_IDENTIFIER, Constants.TYPE_PLACE);
            obj.put(Constants.PLACE_ID, placeId);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

        return obj.toString();
    }
}