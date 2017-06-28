package com.severenity.entity.quest;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.severenity.BR;
import com.severenity.utils.common.Constants;
import com.severenity.utils.common.RealmDataBinding;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Novosad on 5/9/16.
 */
public class Quest extends RealmObject implements Observable, RealmDataBinding {
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

    @Required
    @PrimaryKey
    private String id;

    @Required
    @Bindable
    private String title;

    @Bindable
    private String description;

    @Bindable
    private long experience;

    @Bindable
    private long credits;

    @Bindable
    private int status;

    private DistanceQuest distanceQuest;
    private CaptureQuest captureQuest;
    private CollectQuest collectQuest;

    @Bindable
    private int progress;

    @Ignore
    @Bindable
    private boolean isFinished;

    /**
     * In format of {@link Constants}: yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    private String expirationTime;
    protected int type = QuestType.None.ordinal();

    public Quest() {}

    public Quest(String id, String title, String expirationTime, long experience, long credits, QuestStatus status) {
        setId(id);
        setTitle(title);
        setExperience(experience);
        setCredits(credits);
        setStatus(status.ordinal());
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

    public QuestType getType() {
        return QuestType.values()[type];
    }

    public void setType(QuestType type) {
        this.type = type.ordinal();
    }

    private void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
        notifyPropertyChanged(BR.isFinished);
    }

    public boolean getIsFinished() {
        return isFinished;
    }

    public DistanceQuest getDistanceQuest() {
        return distanceQuest;
    }

    public void setDistanceQuest(DistanceQuest distanceQuest) {
        this.distanceQuest = distanceQuest;
    }

    public CaptureQuest getCaptureQuest() {
        return captureQuest;
    }

    public void setCaptureQuest(CaptureQuest captureQuest) {
        this.captureQuest = captureQuest;
    }

    public CollectQuest getCollectQuest() {
        return collectQuest;
    }

    public void setCollectQuest(CollectQuest collectQuest) {
        this.collectQuest = collectQuest;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
        setIsFinished(status == QuestStatus.Finished.ordinal() || status == QuestStatus.Closed.ordinal());
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        notifyPropertyChanged(BR.progress);
    }

    @Ignore
    private transient PropertyChangeRegistry mCallbacks;

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        if (mCallbacks == null) {
            mCallbacks = new PropertyChangeRegistry();
        }
        mCallbacks.add(onPropertyChangedCallback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback onPropertyChangedCallback) {
        if (mCallbacks != null) {
            mCallbacks.remove(onPropertyChangedCallback);
        }
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    public synchronized void notifyChange() {
        if (mCallbacks != null) {
            mCallbacks.notifyCallbacks(this, 0, null);
        }
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with {@link Bindable} to generate a field in
     * <code>BR</code> to be used as <code>fieldId</code>.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    private void notifyPropertyChanged(int fieldId) {
        if (mCallbacks != null) {
            mCallbacks.notifyCallbacks(this, fieldId, null);
        }
    }
}
