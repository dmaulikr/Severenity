package com.nosad.sample.engine.managers.game;

import android.content.Context;

import com.google.android.gms.maps.model.Marker;
import com.nosad.sample.entity.Spell;

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
        if (this.currentSpell != null && this.currentSpell.equals(currentSpell)) {
            isSpellMode = false;
            this.setCurrentSpell(null);
            return;
        }

        isSpellMode = true;
        this.currentSpell = currentSpell;
    }

    public boolean isSpellMode() {
        return isSpellMode;
    }

    public void cancelSpellMode() {
        selectedWard = null;
        isSpellMode = false;
    }
}
