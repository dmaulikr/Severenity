package com.nosad.sample.engine.managers.game;

import android.content.Context;

import com.nosad.sample.entity.Spell;

/**
 * Created by Novosad on 4/4/16.
 */
public class SpellManager {
    private Context context;
    private Spell currentSpell = null;
    private boolean isSpellMode = false;

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
}
