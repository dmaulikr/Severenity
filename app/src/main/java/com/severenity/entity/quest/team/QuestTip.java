package com.severenity.entity.quest.team;

import io.realm.RealmObject;

/**
 * Created by Novosad on 5/30/17.
 */
public class QuestTip extends RealmObject {
    private String description;
    private int cost;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
