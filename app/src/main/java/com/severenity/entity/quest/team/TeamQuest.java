package com.severenity.entity.quest.team;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Novosad on 5/30/17.
 */

public class TeamQuest extends RealmObject {
    @Required
    @PrimaryKey
    private String id;

    @Required
    private String title;

    @Required
    private String description;
    private int status;
    private RealmList<Participant> participants;
    private RealmList<TeamQuestPart> parts;
    private QuestReward questReward;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public RealmList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(RealmList<Participant> participants) {
        this.participants = participants;
    }

    public RealmList<TeamQuestPart> getParts() {
        return parts;
    }

    public void setParts(RealmList<TeamQuestPart> parts) {
        this.parts = parts;
    }

    public QuestReward getQuestReward() {
        return questReward;
    }

    public void setQuestReward(QuestReward questReward) {
        this.questReward = questReward;
    }
}

