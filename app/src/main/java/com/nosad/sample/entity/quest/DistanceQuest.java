package com.nosad.sample.entity.quest;

import com.nosad.sample.utils.Utils;

import java.util.Date;

/**
 * Created by Novosad on 5/10/16.
 */
public class DistanceQuest extends Quest {
    protected Quest.QuestType type = QuestType.Distance;
    private int distance; // in km

    public DistanceQuest(Quest quest, int distance) {
        super(quest.getId(), quest.getTitle(), quest.getExpirationTime(), quest.getExperience(), quest.getCredits(), quest.getStatus());

        fillData(distance);
    }

    public DistanceQuest(long id, String title, Date expirationTime, long experience, long credits, Quest.QuestStatus status, int distance) {
        super(id, title, expirationTime, experience, credits, status);

        fillData(distance);
    }

    private void fillData(int distance) {
        this.distance = distance;

        if (getExpirationTime() == null) {
            setDescription("Pass " + distance + " km");
        } else {
            setExpirationTime(getExpirationTime());
            setDescription("Pass " + distance + " km in " + Utils.dateDifference(new Date(), getExpirationTime()));
        }
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
