package com.severenity.entity.user;

import io.realm.RealmObject;

/**
 * Handles info regarding progress of the each quest user has.
 *
 * Created by Novosad on 7/9/17.
 */

public class Progress extends RealmObject {
    private int progress;
    private int goal;
    private int current;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }
}
