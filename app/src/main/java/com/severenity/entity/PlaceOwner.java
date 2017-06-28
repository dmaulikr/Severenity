package com.severenity.entity;

import io.realm.RealmObject;

/**
 * Created by Novosad on 6/22/17.
 */

public class PlaceOwner extends RealmObject {
    private String id;

    public PlaceOwner() {
        // Required default constructor
    }

    PlaceOwner(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}