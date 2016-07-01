package com.severenity.entity.quest;

import com.severenity.entity.GamePlace;
import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Novosad on 5/10/16.
 */
public class CaptureQuest extends Quest {
    protected Quest.QuestType type = QuestType.Capture;
    private GamePlace.PlaceType placeType;
    private int placeTypeValue;

    public CaptureQuest(Quest quest, GamePlace.PlaceType placeType, int placeTypeValue) {
        super(quest.getId(), quest.getTitle(), quest.getExpirationTime(), quest.getExperience(), quest.getCredits(), quest.getStatus(), quest.getProgress());

        fillData(placeType, placeTypeValue);
    }

    private void fillData(GamePlace.PlaceType placeType, int placeTypeValue) {
        this.placeType = placeType;
        this.placeTypeValue = placeTypeValue;

        if (getExpirationTime() == null) {
            setDescription("Capture " + placeType);
        } else {
            setExpirationTime(getExpirationTime());
            try {
                setDescription("Capture " + placeType + " in " +
                    Utils.dateDifference(
                        new Date(),
                        new SimpleDateFormat(Constants.TIME_FORMAT, Locale.US).parse(getExpirationTime())
                    )
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public GamePlace.PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(GamePlace.PlaceType placeType) {
        this.placeType = placeType;
    }

    public int getPlaceTypeValue() {
        return placeTypeValue;
    }

    public void setPlaceTypeValue(int placeTypeValue) {
        this.placeTypeValue = placeTypeValue;
    }

    @Override
    public QuestType getType() {
        return type;
    }
}
