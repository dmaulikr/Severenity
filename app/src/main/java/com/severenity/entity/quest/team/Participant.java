package com.severenity.entity.quest.team;

import io.realm.RealmObject;

/**
 * Created by Novosad on 5/30/17.
 */
public class Participant extends RealmObject {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
