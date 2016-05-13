package com.nosad.sample.entity.quest;

import com.nosad.sample.utils.Utils;
import com.nosad.sample.utils.common.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Novosad on 5/10/16.
 */
public class CollectQuest extends Quest {
    protected Quest.QuestType type = QuestType.Collect;
    private Constants.Characteristic characteristic;
    private int amount;

    public CollectQuest(Quest quest, Constants.Characteristic characteristic, int amount) {
        super(quest.getId(), quest.getTitle(), quest.getExpirationTime(), quest.getExperience(), quest.getCredits(), quest.getStatus());

        fillData(characteristic, amount);
    }

    public CollectQuest(long id, String title, String expirationTime, long experience, long credits, Quest.QuestStatus status, Constants.Characteristic characteristic, int amount) {
        super(id, title, expirationTime, experience, credits, status);

        fillData(characteristic, amount);
    }

    private void fillData(Constants.Characteristic characteristic, int amount) {
        this.characteristic = characteristic;
        this.amount = amount;

        if (getExpirationTime() == null) {
            setDescription("Get " + characteristic.toString() + " " + amount);
        } else {
            setExpirationTime(getExpirationTime());
            try {
                setDescription("Get " + characteristic.toString() + " " + amount + " in " +
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Constants.Characteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharacteristic(Constants.Characteristic characteristic) {
        this.characteristic = characteristic;
    }

    @Override
    public QuestType getType() {
        return type;
    }
}