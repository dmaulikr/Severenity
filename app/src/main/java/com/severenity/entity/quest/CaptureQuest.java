package com.severenity.entity.quest;

import com.severenity.entity.GamePlace;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmObject;

/**
 * Represents base simple "Capture" quest where player has to capture some
 * building types.
 *
 * Created by Novosad on 5/10/16.
 */
public class CaptureQuest extends RealmObject {
    protected int type = Quest.QuestType.Capture.ordinal();
    private Integer placeType;
    private Integer placeTypeValue;

    public CaptureQuest() {
        // Default constructor required.
    }

    public CaptureQuest(Quest quest, GamePlace.PlaceType placeType, int placeTypeValue) {
        fillData(quest, placeType, placeTypeValue);
    }

    private void fillData(Quest baseQuest, GamePlace.PlaceType placeType, int placeTypeValue) {
        this.placeType = placeType.ordinal();
        this.placeTypeValue = placeTypeValue;

        if (baseQuest.getExpirationTime() == null || baseQuest.getExpirationTime().equalsIgnoreCase("null")) {
            baseQuest.setDescription("Capture " + placeType);
        } else {
            baseQuest.setExpirationTime(baseQuest.getExpirationTime());
            try {
                baseQuest.setDescription("Capture " + placeType + " in " +
                    Utils.dateDifference(
                        new Date(),
                        new SimpleDateFormat(Constants.TIME_FORMAT, Locale.US).parse(baseQuest.getExpirationTime())
                    )
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public GamePlace.PlaceType getPlaceType() {
        return GamePlace.PlaceType.values()[placeType];
    }

    public void setPlaceType(GamePlace.PlaceType placeType) {
        this.placeType = placeType.ordinal();
    }

    public int getPlaceTypeValue() {
        return placeTypeValue;
    }

    public void setPlaceTypeValue(int placeTypeValue) {
        this.placeTypeValue = placeTypeValue;
    }

    public Quest.QuestType getType() {
        return Quest.QuestType.values()[type];
    }
}
