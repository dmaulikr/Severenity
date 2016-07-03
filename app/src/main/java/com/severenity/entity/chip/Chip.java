package com.severenity.entity.chip;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.severenity.BR;
import com.severenity.R;

/**
 * Created by Novosad on 4/4/16.
 */
public class Chip extends BaseObservable implements Parcelable {
    protected Chip(Parcel in) {
        chipIconResource = in.readInt();
        description = in.readString();
        level = in.readInt();
        chipType = ChipType.values()[in.readInt()];
        rarity = Rarity.values()[in.readInt()];
    }

    public static final Creator<Chip> CREATOR = new Creator<Chip>() {
        @Override
        public Chip createFromParcel(Parcel in) {
            return new Chip(in);
        }

        @Override
        public Chip[] newArray(int size) {
            return new Chip[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(chipIconResource);
        dest.writeString(description);
        dest.writeInt(level);
        dest.writeInt(chipType.ordinal());
        dest.writeInt(rarity.ordinal());
    }

    public enum ChipType {
        CapturePlace,
        CapturePlayer,
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

    private ChipType chipType;
    private int chipIconResource;
    private String description;
    private int level;
    private Rarity rarity;

    public Chip(ChipType chipType, String description, int level, Rarity rarity) {
        setChipType(chipType);
        setDescription(description);
        setLevel(level);
        setRarity(rarity);

        switch (chipType) {
            case CapturePlace:
                setChipIconResource(R.drawable.sig_capture_place);
                break;
            case CapturePlayer:
                setChipIconResource(R.drawable.sig_capture_player);
                break;
            case Remove:
                setChipIconResource(R.drawable.sig_remove);
                break;
            case Defend:
                setChipIconResource(R.drawable.sig_defend);
                break;
            case Attack:
                setChipIconResource(R.drawable.sig_attack);
                break;
            case Invisibility:
                setChipIconResource(R.drawable.sig_invisibility);
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
        switch (chipType) {
            case CapturePlace:
                return "Capture Place";
            case CapturePlayer:
                return "Capture Player";
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
    public ChipType getChipType() {
        return chipType;
    }

    public void setChipType(ChipType chipType) {
        this.chipType = chipType;
        notifyPropertyChanged(BR.chipType);
    }

    @Bindable
    public int getChipIconResource() {
        return chipIconResource;
    }

    public void setChipIconResource(int chipIconResource) {
        this.chipIconResource = chipIconResource;
        notifyPropertyChanged(BR.chipIconResource);
    }

    public boolean is(ChipType chipType) {
        return this.chipType == chipType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Chip)) {
            return false;
        }

        Chip s = (Chip) o;
        return this.chipType == s.chipType;
    }
}
