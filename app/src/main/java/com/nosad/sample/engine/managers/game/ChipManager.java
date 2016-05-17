package com.nosad.sample.engine.managers.game;

import android.content.Context;

import com.google.android.gms.maps.model.Marker;
import com.nosad.sample.entity.Chip;

import java.util.ArrayList;

/**
 * Created by Novosad on 4/4/16.
 */
public class ChipManager {
    private Context context;
    private Chip currentChip = null;
    private boolean isChipMode = false;

    private Marker selectedWard = null;

    private ArrayList<Marker> placedWards = new ArrayList<>();

    public ChipManager(Context context) {
        this.context = context;
    }

    public Chip getCurrentChip() {
        return currentChip;
    }

    public void setCurrentChip(Chip currentChip) {
        if (this.currentChip != null && this.currentChip.equals(currentChip)) {
            isChipMode = false;
            this.setCurrentChip(null);
            return;
        }

        isChipMode = true;
        this.currentChip = currentChip;
    }

    public boolean isChipMode() {
        return isChipMode;
    }

    public void cancelChipMode() {
        selectedWard = null;
        isChipMode = false;
    }
}
