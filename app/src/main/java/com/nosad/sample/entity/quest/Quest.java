package com.nosad.sample.entity.quest;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Novosad on 5/9/16.
 */
public class Quest {
    public enum QuestStatus {
        New,
        InProgress,
        Finished
    }

    public enum QuestType {
        None("none"),
        Distance("distance"),
        Capture("capture"),
        Collect("collect");

        final String value;

        QuestType(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private long id;
    private String title;
    private String description;
    private long experience;
    private long credits;
    private QuestStatus status;
    private Date expirationTime;
    protected QuestType type = QuestType.None;

    public Quest() {}

    public Quest(long id, String title, Date expirationTime, long experience, long credits, QuestStatus status) {
        this.id = id;
        this.title = title;
        this.experience = experience;
        this.credits = credits;
        this.status = status;
        this.expirationTime = expirationTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    /**
     * Sets the expiration time in Calendar values, e.g. Calendar.HOUR, 1
     *
     * @param field {@link Calendar} field for MONTH, HOUR or so.
     * @param value value in field specified.
     */
    public void setExpirationTimeFromNow(int field, int value) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(field, value);

        this.expirationTime = calendar.getTime();
    }

    /**
     * Default setter.
     *
     * @param expirationTime date of the expiration
     */
    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public long getCredits() {
        return credits;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public void setStatus(QuestStatus status) {
        this.status = status;
    }

    public QuestType getType() {
        return type;
    }

    public void setType(QuestType type) {
        this.type = type;
    }
}
