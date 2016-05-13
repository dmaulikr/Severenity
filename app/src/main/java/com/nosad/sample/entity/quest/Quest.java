package com.nosad.sample.entity.quest;

import com.nosad.sample.utils.common.Constants;

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
    /**
     * In format of {@link Constants.TIME_FORMAT}: yyyy-MM-dd'T'HH:mm:ss.SSSZ
     */
    private String expirationTime;
    protected QuestType type = QuestType.None;

    public Quest() {}

    public Quest(long id, String title, String expirationTime, long experience, long credits, QuestStatus status) {
        this.id = id;
        this.title = title;
        this.experience = experience;
        this.credits = credits;
        this.status = status;
        this.expirationTime = expirationTime;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    /**
     * Default setter.
     *
     * @param expirationTime date of the expiration
     */
    public void setExpirationTime(String expirationTime) {
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
