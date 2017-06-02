package com.severenity.entity.skill;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.severenity.BR;
import com.severenity.R;

/**
 * Created by Novosad on 4/4/16.
 */
public class Skill extends BaseObservable implements Parcelable {
    protected Skill(Parcel in) {
        skillIconResource = in.readInt();
        description = in.readString();
        level = in.readInt();
        skillType = SkillType.values()[in.readInt()];
        rarity = Rarity.values()[in.readInt()];
    }

    public static final Creator<Skill> CREATOR = new Creator<Skill>() {
        @Override
        public Skill createFromParcel(Parcel in) {
            return new Skill(in);
        }

        @Override
        public Skill[] newArray(int size) {
            return new Skill[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(skillIconResource);
        dest.writeString(description);
        dest.writeInt(level);
        dest.writeInt(skillType.ordinal());
        dest.writeInt(rarity.ordinal());
    }

    public enum SkillType {
        CapturePlace,
        Remove,
        Defend,
        Attack,
        Invisibility
    }

    public enum Rarity {
        Common,
        Uncommon,
        Rare
    }

    private SkillType skillType;
    private int skillIconResource;
    private String description;
    private int level;
    private Rarity rarity;

    public Skill(SkillType skillType, String description, int level, Rarity rarity) {
        setSkillType(skillType);
        setDescription(description);
        setLevel(level);
        setRarity(rarity);

        switch (skillType) {
            case CapturePlace:
                setSkillIconResource(R.drawable.sig_capture_place);
                break;
            case Remove:
                setSkillIconResource(R.drawable.sig_remove);
                break;
            case Defend:
                setSkillIconResource(R.drawable.sig_defend);
                break;
            case Attack:
                setSkillIconResource(R.drawable.sig_attack);
                break;
            case Invisibility:
                setSkillIconResource(R.drawable.sig_invisibility);
                break;
        }
    }

    @Bindable
    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
        notifyPropertyChanged(BR.rarity);
    }

    @Bindable
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        notifyPropertyChanged(BR.level);
    }

    @Bindable
    public String getTitle() {
        switch (skillType) {
            case CapturePlace:
                return "Capture Place";
            case Remove:
                return "Remove";
            case Defend:
                return "Defend";
            case Attack:
                return "Attack";
            case Invisibility:
                return "Invisibility";
            default:
                return "Unknown";
        }
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
    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
        notifyPropertyChanged(BR.skillType);
    }

    @Bindable
    public int getSkillIconResource() {
        return skillIconResource;
    }

    public void setSkillIconResource(int skillIconResource) {
        this.skillIconResource = skillIconResource;
        notifyPropertyChanged(BR.skillIconResource);
    }

    public boolean is(SkillType skillType) {
        return this.skillType == skillType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Skill)) {
            return false;
        }

        Skill s = (Skill) o;
        return this.skillType == s.skillType;
    }
}
