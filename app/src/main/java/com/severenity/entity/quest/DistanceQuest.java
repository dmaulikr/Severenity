package com.severenity.entity.quest;

import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Novosad on 5/10/16.
 */
public class DistanceQuest extends Quest {
    protected Quest.QuestType type = QuestType.Distance;
    private int distance; // in km

    public DistanceQuest(Quest quest, int distance) {
        super(quest.getId(), quest.getTitle(), quest.getExpirationTime(), quest.getExperience(), quest.getCredits(), quest.getStatus(), quest.getProgress());

        fillData(distance);
    }

    private void fillData(int distance) {
        this.distance = distance;

        if (getExpirationTime() == null) {
            setDescription("Pass " + distance + " km");
        } else {
            setExpirationTime(getExpirationTime());
            try {
                setDescription("Pass " + distance + " meters in " + Utils.dateDifference(
                        new Date(),
                        new SimpleDateFormat(Constants.TIME_FORMAT, Locale.US).parse(getExpirationTime())
                ));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public QuestType getType() {
        return type;
    }
}
