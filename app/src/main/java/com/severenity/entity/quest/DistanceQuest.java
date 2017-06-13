package com.severenity.entity.quest;

import com.severenity.App;
import com.severenity.R;
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

        if (getExpirationTime() == null || getExpirationTime().equalsIgnoreCase("null")) {
            setDescription(String.format(Locale.getDefault(), App.getInstance().getString(R.string.quest_distance_no_time), distance));
        } else {
            setExpirationTime(getExpirationTime());
            try {
                String dateDifference = Utils.dateDifference(
                        new Date(),
                        new SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault()).parse(getExpirationTime()));
                setDescription(String.format(Locale.getDefault(), App.getInstance().getString(R.string.quest_distance_time), distance, dateDifference));
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
