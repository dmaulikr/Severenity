package com.severenity.entity.user;

import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Handles info regarding quests that user has.
 *
 * Created by Novosad on 7/9/17.
 */
public class UserQuest extends RealmObject {
    @Required
    private String id;

    @Required
    private String objective;

    private Progress progress;

    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
