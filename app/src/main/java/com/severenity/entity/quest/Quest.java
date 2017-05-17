package com.severenity.entity.quest;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.severenity.BR;
import com.severenity.utils.common.Constants;

/**
 * Created by Novosad on 5/9/16.
 */
public class Quest extends BaseObservable {
    public enum QuestStatus {
        Created,
        Accepted,
        InProgress,
        Finished,
        Closed // identifies if user received bonuses
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

    private String id;

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

    @Bindable
    private int progress;

    /**
     * In format of {@link Constants}: yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    @Bindable
    private String expirationTime;
    protected QuestType type = QuestType.None;

    public Quest() {}

    public Quest(String id, String title, String expirationTime, long experience, long credits, QuestStatus status, int progress) {
        setId(id);
        setTitle(title);
        setExperience(experience);
        setCredits(credits);
        setStatus(status);
        setExpirationTime(expirationTime);
        setProgress(progress);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        setIsFinished(status == QuestStatus.Finished || status == QuestStatus.Closed);
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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        notifyPropertyChanged(BR.progress);
    }
}
