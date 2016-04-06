package com.nosad.sample.engine.managers.game;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;
import com.nosad.sample.App;
import com.nosad.sample.entity.Spell;
import com.nosad.sample.utils.common.Constants;

import java.util.ArrayList;

/**
 * Created by Novosad on 4/4/16.
 */
public class SpellManager {
    private Context context;
    private Spell currentSpell = null;
    private boolean isSpellMode = false;

    private Marker selectedWard = null;

    private ArrayList<Marker> placedWards = new ArrayList<>();

    public SpellManager(Context context) {
        this.context = context;
    }

    public Spell getCurrentSpell() {
        return currentSpell;
    }

    public void setCurrentSpell(Spell currentSpell) {
        this.currentSpell = currentSpell;
    }

    public boolean isSpellMode() {
        return isSpellMode;
    }

    public void setIsSpellMode(boolean isSpellMode) {
        this.isSpellMode = isSpellMode;
    }

    public boolean hasWards() {
        return placedWards.size() > 0;
    }

    public void addWard(Marker ward) {
        placedWards.add(ward);
        App.getLocalBroadcastManager().sendBroadcast(new Intent(Constants.INTENT_FILTER_WARDS_COUNT));
    }

    public void moveToNextWard() throws Exception {
        if (placedWards.size() < 0) {
            Log.wtf(Constants.TAG, "Placed wards count should be bigger than 0, something went wrong");
            return;
        }

        if (selectedWard == null) {
            selectedWard = placedWards.get(0);
        } else {
            for (int i = 0; i < placedWards.size(); i++) {
                Marker ward = placedWards.get(i);
                if (ward.getPosition().latitude == selectedWard.getPosition().latitude
                    && ward.getPosition().longitude == selectedWard.getPosition().longitude) {
                    if (i + 1 >= placedWards.size()) {
                        selectedWard = placedWards.get(0);
                    } else {
                        selectedWard = placedWards.get(i + 1);
                    }
                    break;
                }
            }
        }

        App.getLocationManager().fixCameraAtLocation(selectedWard.getPosition());
    }

    public void cancelSpellMode() {
        selectedWard = null;
        isSpellMode = false;
    }
}
