package com.nosad.sample.entity.quest;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.nosad.sample.BR;
import com.nosad.sample.utils.common.Constants;

/**
 * Created by Novosad on 5/9/16.
 */
public class Quest extends BaseObservable {
    public enum QuestStatus {
        Created,
        Accepted,
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

    private boolean isFinished;
    private long id;
    private String title;
    private String description;
    private long experience;
    private long credits;
    private QuestStatus status;

    /**
     * In format of {@link Constants}: yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
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

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
        notifyPropertyChanged(BR.experience);
    }

    @Bindable
    public long getCredits() {
        return credits;
    }

    public void setCredits(long credits) {
        this.credits = credits;
        notifyPropertyChanged(BR.credits);
    }

    @Bindable
    public QuestStatus getStatus() {
        return status;
    }

    public void setStatus(QuestStatus status) {
        this.status = status;
        isFinished = status == QuestStatus.Finished;
        notifyPropertyChanged(BR.status);
        notifyPropertyChanged(BR.isFinished);
    }

    public QuestType getType() {
        return type;
    }

    public void setType(QuestType type) {
        this.type = type;
    }

    @Bindable
    public boolean getIsFinished() {
        return isFinished;
    }
}
