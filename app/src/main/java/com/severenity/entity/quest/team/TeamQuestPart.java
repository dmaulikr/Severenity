package com.severenity.entity.quest.team;

import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.PropertyChangeRegistry;

import com.android.databinding.library.baseAdapters.BR;
import com.severenity.entity.quest.Quest;
import com.severenity.utils.common.RealmDataBinding;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

/**
 * Created by Novosad on 5/30/17.
 */
@RealmClass
public class TeamQuestPart extends RealmObject implements Observable, RealmDataBinding {
    @Ignore
    private transient PropertyChangeRegistry mCallbacks;

    @Required
    private String placeId;

    @Required
    private String code;
    private RealmList<QuestTip> tips;

    @Required
    @Bindable
    private String description;

    @Required
    @Bindable
    private String title;

    @Bindable
    private int status;

    @Bindable
    private boolean isFinished;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public RealmList<QuestTip> getTips() {
        return tips;
    }

    public void setTips(RealmList<QuestTip> tips) {
        this.tips = tips;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
        notifyPropertyChanged(com.severenity.BR.status);
        setIsFinished(status == Quest.QuestStatus.Finished.ordinal() || status == Quest.QuestStatus.Closed.ordinal());
    }

    private void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
        notifyPropertyChanged(com.severenity.BR.isFinished);
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
