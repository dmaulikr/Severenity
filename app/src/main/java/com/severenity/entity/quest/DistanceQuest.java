package com.severenity.entity.quest;

import com.severenity.App;
import com.severenity.R;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmObject;

/**
 * Represents base simple "Distance" quest where player has to pass some distance.
 *
 * Created by Novosad on 5/10/16.
 */
public class DistanceQuest extends RealmObject {
    protected int type = Quest.QuestType.Distance.ordinal();
    private Integer distance; // in km

    public DistanceQuest() {
        // Default constructor required.
    }

    public DistanceQuest(Quest quest, int distance) {
        fillData(quest, distance);
    }

    private void fillData(Quest baseQuest, int distance) {
        this.distance = distance;

        if (baseQuest.getExpirationTime() == null || baseQuest.getExpirationTime().equalsIgnoreCase("null")) {
            baseQuest.setDescription(String.format(Locale.getDefault(), App.getInstance().getString(R.string.quest_distance_no_time), distance));
        } else {
            baseQuest.setExpirationTime(baseQuest.getExpirationTime());
            try {
                String dateDifference = Utils.dateDifference(
                        new Date(),
                        new SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault()).parse(baseQuest.getExpirationTime()));
                baseQuest.setDescription(String.format(Locale.getDefault(), App.getInstance().getString(R.string.quest_distance_time), distance, dateDifference));
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

    public Quest.QuestType getType() {
        return Quest.QuestType.values()[type];
    }
}
