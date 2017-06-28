package com.severenity.entity.quest;

import com.severenity.utils.Utils;
import com.severenity.utils.common.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmObject;

/**
 * Represents base simple "Collect" quest where player has to collect some
 * characteristic value (e.g. "Reach level 3")
 *
 * Created by Novosad on 5/10/16.
 */
public class CollectQuest extends RealmObject {
    protected int type = Quest.QuestType.Collect.ordinal();

    private Integer characteristic;
    private Integer amount;

    public CollectQuest() {
        // Default constructor required.
    }

    public CollectQuest(Quest quest, Constants.Characteristic characteristic, int amount) {
        fillData(quest, characteristic, amount);
    }

    private void fillData(Quest baseQuest, Constants.Characteristic characteristic, int amount) {
        this.characteristic = characteristic.ordinal();
        this.amount = amount;

        if (baseQuest.getExpirationTime() == null || baseQuest.getExpirationTime().equalsIgnoreCase("null")) {
            baseQuest.setDescription("Get " + characteristic.toString() + " " + amount);
        } else {
            baseQuest.setExpirationTime(baseQuest.getExpirationTime());
            try {
                baseQuest.setDescription("Get " + characteristic.toString() + " " + amount + " in " +
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

    public int getAmount() {
        return amount;
    }

    public Constants.Characteristic getCharacteristic() {
        return Constants.Characteristic.values()[characteristic];
    }

    public Quest.QuestType getType() {
        return Quest.QuestType.values()[type];
    }
}
