package com.nosad.sample.entity.quest;

import com.nosad.sample.utils.Utils;
import com.nosad.sample.utils.common.Constants;

import java.util.Date;

/**
 * Created by Novosad on 5/10/16.
 */
public class CollectQuest extends Quest {
    private Quest.QuestType type = QuestType.Collect;
    private Constants.Characteristic characteristic;
    private int amount;

    public CollectQuest(long id, String title, Date expirationTime, long experience, long credits, Quest.QuestStatus status, Constants.Characteristic characteristic, int amount) {
        super(id, title, expirationTime, experience, credits, status);

        this.characteristic = characteristic;
        this.amount = amount;

        if (expirationTime == null) {
            setDescription("Get " + characteristic.toString() + " " + amount);
        } else {
            setExpirationTime(expirationTime);
            setDescription("Get " + characteristic.toString() + " " + amount + " in " + Utils.dateDifference(new Date(), expirationTime));
        }
    }
}
