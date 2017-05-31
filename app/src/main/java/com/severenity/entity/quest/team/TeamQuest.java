package com.severenity.entity.quest.team;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.severenity.BR;
import com.severenity.entity.quest.Quest;
import com.severenity.utils.common.RealmDataBinding;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Novosad on 5/30/17.
 */
public class TeamQuest extends RealmObject implements Observable, RealmDataBinding {
    @Ignore
    private transient PropertyChangeRegistry mCallbacks;

    @Required
    @PrimaryKey
    @Bindable
    private String id;

    @Required
    @Bindable
    private String title;

    @Required
    @Bindable
    private String description;

    @Bindable
    private int status;
    private RealmList<Participant> participants;
    private RealmList<TeamQuestPart> parts;
    private QuestReward reward;

    @Bindable
    private boolean isFinished;

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

        if (!isManaged()) {
            notifyPropertyChanged(BR.title);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;

        if (!isManaged()) {
            notifyPropertyChanged(BR.description);
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;

        if (!isManaged()) {
            notifyPropertyChanged(BR.status);
        }
        setIsFinished(status == Quest.QuestStatus.Finished.ordinal() || status == Quest.QuestStatus.Closed.ordinal());
    }

    public RealmList<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(RealmList<Participant> participants) {
        this.participants = participants;
    }

    public RealmList<TeamQuestPart> getParts() {
        return parts;
    }

    public void setParts(RealmList<TeamQuestPart> parts) {
        this.parts = parts;
    }

    public QuestReward getReward() {
        return reward;
    }

    public void setReward(QuestReward reward) {
        this.reward = reward;
    }

    private void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;

        if (!isManaged()) {
            notifyPropertyChanged(BR.isFinished);
        }
    }

    public boolean getIsFinished() {
        return isFinished;
    }

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

