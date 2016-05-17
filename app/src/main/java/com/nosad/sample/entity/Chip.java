package com.nosad.sample.entity;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.nosad.sample.BR;
import com.nosad.sample.R;

/**
 * Created by Novosad on 4/4/16.
 */
public class Chip extends BaseObservable {
    public enum ChipType {
        Capture,
        Dispel,
        Shield,
        Attack
    }

    private ChipType chipType;
    private int chipIconResource;

    public Chip(ChipType chipType) {
        this.chipType = chipType;

        switch (chipType) {
            case Capture:
                setChipIconResource(R.drawable.menu_marker);
                break;
            case Dispel:
                setChipIconResource(R.drawable.chip_wave);
                break;
            case Shield:
                setChipIconResource(R.drawable.chip_ward);
                break;
            case Attack:
                setChipIconResource(R.drawable.menu_arrow_right);
                break;
        }
    }

    @Bindable
    public String getTitle() {
        switch (chipType) {
            case Capture:
                return "Capture";
            case Dispel:
                return "Dispel";
            case Shield:
                return "Shield";
            case Attack:
                return "Attack";
            default:
                return "Unknown";
        }
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
