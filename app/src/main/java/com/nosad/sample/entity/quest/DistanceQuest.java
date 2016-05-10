package com.nosad.sample.entity.quest;

import com.nosad.sample.entity.quest.Quest;
import com.nosad.sample.utils.Utils;

import java.util.Date;

/**
 * Created by Novosad on 5/10/16.
 */
public class DistanceQuest extends Quest {
    private Quest.QuestType type = QuestType.Distance;
    private int distance; // in km

    public DistanceQuest(long id, String title, Date expirationTime, long experience, long credits, Quest.QuestStatus status, int distance) {
        super(id, title, expirationTime, experience, credits, status);

        this.distance = distance;

        if (expirationTime == null) {
            setDescription("Pass " + distance + " km");
        } else {
            setExpirationTime(expirationTime);
            setDescription("Pass " + distance + " km in " + Utils.dateDifference(new Date(), expirationTime));
        }
    }

    public QuestType getType() {
        return type;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
