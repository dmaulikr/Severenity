package com.nosad.sample.entity.quest;

import com.nosad.sample.utils.Utils;

import java.util.Date;

/**
 * Created by Novosad on 5/10/16.
 */
public class CaptureQuest extends Quest {
    protected Quest.QuestType type = QuestType.Capture;
    private String placeType;
    private int placeTypeValue;

    public CaptureQuest(Quest quest, String placeType, int placeTypeValue) {
        super(quest.getId(), quest.getTitle(), quest.getExpirationTime(), quest.getExperience(), quest.getCredits(), quest.getStatus());

        fillData(placeType, placeTypeValue);
    }

    public CaptureQuest(long id, String title, Date expirationTime, long experience, long credits, Quest.QuestStatus status, String placeType, int placeTypeValue) {
        super(id, title, expirationTime, experience, credits, status);

        fillData(placeType, placeTypeValue);
    }

    private void fillData(String placeType, int placeTypeValue) {
        this.placeType = placeType;
        this.placeTypeValue = placeTypeValue;

        if (getExpirationTime() == null) {
            setDescription("Capture " + placeType);
        } else {
            setExpirationTime(getExpirationTime());
            setDescription("Capture " + placeType + " in " + Utils.dateDifference(new Date(), getExpirationTime()));
        }
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public int getPlaceTypeValue() {
        return placeTypeValue;
    }

    public void setPlaceTypeValue(int placeTypeValue) {
        this.placeTypeValue = placeTypeValue;
    }
}
