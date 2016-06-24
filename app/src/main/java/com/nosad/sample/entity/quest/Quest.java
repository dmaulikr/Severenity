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

    @Bindable
    private boolean isFinished;

    private long id;

    @Bindable
    private String title;

    @Bindable
    private String description;

    @Bindable
    private long experience;

    @Bindable
    private long credits;

    @Bindable
    private QuestStatus status;

    /**
     * In format of {@link Constants}: yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    @Bindable
    private String expirationTime;
    protected QuestType type = QuestType.None;

    public Quest() {}

    public Quest(long id, String title, String expirationTime, long experience, long credits, QuestStatus status) {
        setId(id);
        setTitle(title);
        setExperience(experience);
        setCredits(credits);
        setStatus(status);
        setExpirationTime(expirationTime);
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
        notifyPropertyChanged(BR.title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
        notifyPropertyChanged(BR.experience);
    }

    public long getCredits() {
        return credits;
    }

    public void setCredits(long credits) {
        this.credits = credits;
        notifyPropertyChanged(BR.credits);
    }

    public QuestStatus getStatus() {
        return status;
    }

    public void setStatus(QuestStatus status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
        setIsFinished(status == QuestStatus.Finished);
    }

    public QuestType getType() {
        return type;
    }

    public void setType(QuestType type) {
        this.type = type;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
        notifyPropertyChanged(BR.isFinished);
    }

    public boolean getIsFinished() {
        return isFinished;
    }
}
