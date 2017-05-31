package com.severenity.entity.quest.team;

import io.realm.RealmObject;

/**
 * Created by Novosad on 5/30/17.
 */
public class QuestReward extends RealmObject {
    private int credits;
    private int rating;

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
